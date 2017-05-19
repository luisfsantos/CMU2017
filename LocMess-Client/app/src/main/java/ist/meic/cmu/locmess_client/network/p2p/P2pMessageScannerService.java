package ist.meic.cmu.locmess_client.network.p2p;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.authentication.AccountService;
import ist.meic.cmu.locmess_client.data.KeyPair;
import ist.meic.cmu.locmess_client.network.p2p.json.P2pMatchDataElement;
import ist.meic.cmu.locmess_client.network.p2p.json.P2pMatchResponseElement;
import ist.meic.cmu.locmess_client.network.p2p.json.P2pRequest;
import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.sql.LocMessDBSQLiteHelper;
import ist.meic.cmu.locmess_client.utils.CoordinatesUtils;
import ist.meic.cmu.locmess_client.utils.DateUtils;
import ist.meic.cmu.locmess_client.utils.WhiteBlackListUtils;
import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;

/**
 * Created by Catarina on 12/05/2017.
 */

public class P2pMessageScannerService extends Service implements SimWifiP2pManager.GroupInfoListener{
    private static final String TAG = "P2pMsgScannerService";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private Set<SimWifiP2pDevice> mGroupDevices;

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication() /* try with "this" */, mServiceLooper, null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("P2pMessageScannerThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        // bind to termite service
        Intent intent = new Intent(P2pMessageScannerService.this, SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        registerReceiver(mOnRefreshNetwork, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service starting...");
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        // trigger a new scan
        mServiceHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping service...");
        if (mBound) {
            unbindService(mConnection);
            mConnection = null;
        }
        unregisterReceiver(mOnRefreshNetwork);
        super.onDestroy();
    }

    private BroadcastReceiver mOnRefreshNetwork = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mBound) {
                mManager.requestGroupInfo(mChannel, P2pMessageScannerService.this);
            } else {
                Log.i(TAG, "Service not bound");
            }
        }
    };

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        Set<SimWifiP2pDevice> deviceList = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        builder.append("Group membership changed: ");
        for (String s: groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(s);
            deviceList.add(device);
            builder.append(device.deviceName)
                    .append(" (")
                    .append(device.getVirtIp())
                    .append(":")
                    .append(device.getVirtPort())
                    .append(");");
        }
        mGroupDevices = deviceList;
        Log.d(TAG, builder.toString());
    }

    private final class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mGroupDevices == null || mGroupDevices.isEmpty()) {
                Log.i(TAG, "No devices nearby. Skipping message scan.");
                return;
            }
            Cursor msgCursor = getActiveP2pMessages();
            if (msgCursor == null || msgCursor.getCount() < 1 ) {
                Log.i(TAG, "No active Posted P2P messages to send.");
                return;
            }
            // build p2p match request
            SparseArray<ContentValues> messages = getMessages(msgCursor);
            String json = buildMatchJson(messages);
            for (SimWifiP2pDevice device : mGroupDevices) {
                new MyAsyncTask(messages, device.getVirtIp(), device.getVirtPort()).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, json
                );
            }
            msgCursor.close();
        }

        private SparseArray<ContentValues> getMessages(Cursor cursor) {
            SparseArray<ContentValues> values = new SparseArray<>(cursor.getCount());
            cursor.moveToFirst();
            do {
                ContentValues row = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, row);
                values.put(generateP2pId(getBaseContext(),row.getAsInteger(LocMessDBContract.PostedMessages._ID)), row);
            } while (cursor.moveToNext());
            return values;
        }

        private String buildMatchJson(SparseArray<ContentValues> messages) {
            Gson gson = new Gson();
            List<P2pMatchDataElement> data = new ArrayList<>();
            for (int i = 0; i < messages.size(); i++) {
                ContentValues values = messages.valueAt(i);
                P2pMatchDataElement element = new P2pMatchDataElement();
                element.setId(messages.keyAt(i)); // the unique p2p id
                List<KeyPair> whitelist = WhiteBlackListUtils.deserializeFromDbFormat(
                        values.getAsString(LocMessDBContract.PostedMessages.COLUMN_WHITELIST));
                List<KeyPair> blacklist = WhiteBlackListUtils.deserializeFromDbFormat(
                        values.getAsString(LocMessDBContract.PostedMessages.COLUMN_BLACKLIST));
                element.setWhitelist(whitelist);
                element.setBlacklist(blacklist);
                data.add(element);
            }
            P2pRequest request = new P2pRequest();
            request.setType(P2pRequest.TYPE_MATCH);
            request.setData(gson.toJsonTree(data));
            return gson.toJson(request);
        }

        private Cursor getActiveP2pMessages() {
            LocMessDBSQLiteHelper helper = new LocMessDBSQLiteHelper(getBaseContext());
            SQLiteDatabase database = helper.getReadableDatabase();
            Set<String> locationIDs = new HashSet<>();

            Cursor locations = queryForLocations(database);
            assert locations != null;
            Log.d(TAG, "#locations=" + locations.getCount());
            if (locations.getCount() > 0) {
                while (locations.moveToNext()) {
                    ContentValues values = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(locations, values);
                    CoordinatesUtils.Coordinates messageLocation =
                            new CoordinatesUtils(P2pMessageScannerService.this,
                                    values.getAsString(LocMessDBContract.Location.COLUMN_COORDINATES)).parse();
                    if (currentLocationMatches(messageLocation)) {
                        locationIDs.add(values.getAsString(LocMessDBContract.Location.COLUMN_SERVER_ID));
                    }
                }
                locations.close();
                Log.d(TAG, "locationIDs=" + TextUtils.join(",", locationIDs));
                if (!locationIDs.isEmpty()) {
                    return queryForLocationMessages(locationIDs);
                }
            }
            locations.close();
            return null;
        }

        private boolean currentLocationMatches(CoordinatesUtils.Coordinates messageLocation) {
            // TODO: 13/05/2017 see if it also works with gps locations
            SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
            if (messageLocation instanceof CoordinatesUtils.WifiCoordinates) {
                Set<String> messageSsids = new HashSet<>(((CoordinatesUtils.WifiCoordinates)messageLocation).ssids);
                Set<String> currentSsids = preferences.getStringSet(getString(R.string.pref_currLocationSsids), new HashSet<String>());
//                Log.d(TAG, "messageSsids=" + TextUtils.join(",", messageSsids));
//                Log.d(TAG, "currentSsids=" + TextUtils.join(",", currentSsids));
                currentSsids.retainAll(messageSsids); // only elements in both sets are kept in currentSsids
                return !currentSsids.isEmpty(); // if it's not empty then there was at least one match

            } else if (messageLocation instanceof CoordinatesUtils.GpsCoordinates) {

                double latitude = Double.longBitsToDouble(preferences.getLong(getString(R.string.pref_currLatitude), Double.doubleToLongBits(0.0)));
                double longitude = Double.longBitsToDouble(preferences.getLong(getString(R.string.pref_currLongitude), Double.doubleToLongBits(0.0)));

                Location currentLocation = new Location("");
                currentLocation.setLatitude(latitude);
                currentLocation.setLongitude(longitude);
                Location messageGpsLocation = new Location("");
                messageGpsLocation.setLatitude(((CoordinatesUtils.GpsCoordinates) messageLocation).latitude);
                messageGpsLocation.setLongitude(((CoordinatesUtils.GpsCoordinates) messageLocation).longitude);

                double distance = messageGpsLocation.distanceTo(currentLocation);
                return distance <= ((CoordinatesUtils.GpsCoordinates) messageLocation).radius;
            }
            return false;
        }

        private Cursor queryForLocations(SQLiteDatabase database) {
            String selection =
                    LocMessDBContract.Location.COLUMN_SERVER_ID + " = " + LocMessDBContract.PostedMessages.COLUMN_LOCATION_SERVER_ID +
                            " AND " + LocMessDBContract.PostedMessages.COLUMN_POLICY + " = " + LocMessDBContract.PostedMessages.POLICY_P2P +
                            " AND " + "(? BETWEEN " + LocMessDBContract.PostedMessages.COLUMN_DATE_FROM +
                            " AND " + LocMessDBContract.PostedMessages.COLUMN_DATE_TO +
                            " AND " + LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " +
                            AccountService.getActiveAccountHash(getBaseContext()) + ")";
            return database.query(true,
                    LocMessDBContract.Location.TABLE_NAME + ", " + LocMessDBContract.PostedMessages.TABLE_NAME,
                    new String[] { LocMessDBContract.Location.COLUMN_SERVER_ID, LocMessDBContract.Location.COLUMN_COORDINATES },
                    selection,
                    new String[] { DateUtils.formatDateTimeLocaleToDb(new Date()) },
                    LocMessDBContract.Location.COLUMN_SERVER_ID,
                    null,
                    null,
                    null
            );
        }

        private Cursor queryForLocationMessages(Set<String> serverIDs) {
            ContentResolver mResolver = getContentResolver();
            String[] projection = {
                    LocMessDBContract.PostedMessages._ID,
                    LocMessDBContract.PostedMessages.COLUMN_LOCATION_SERVER_ID,
                    LocMessDBContract.PostedMessages.COLUMN_LOCATION,
                    LocMessDBContract.PostedMessages.COLUMN_TITLE,
                    LocMessDBContract.PostedMessages.COLUMN_CONTENT,
                    LocMessDBContract.PostedMessages.COLUMN_DATE_FROM,
                    LocMessDBContract.PostedMessages.COLUMN_DATE_TO,
                    LocMessDBContract.PostedMessages.COLUMN_WHITELIST,
                    LocMessDBContract.PostedMessages.COLUMN_BLACKLIST
            };
            String selection = LocMessDBContract.PostedMessages.COLUMN_POLICY + " = " + LocMessDBContract.PostedMessages.POLICY_P2P +
                    " AND " + LocMessDBContract.PostedMessages.COLUMN_LOCATION_SERVER_ID + " IN (" + makePlaceholders(serverIDs.size()) + ")" +
                    " AND " + "(? BETWEEN " + LocMessDBContract.PostedMessages.COLUMN_DATE_FROM +
                    " AND " + LocMessDBContract.PostedMessages.COLUMN_DATE_TO + ")";

            List<String> args = new ArrayList<>(serverIDs);
            args.add(DateUtils.formatDateTimeLocaleToDb(new Date()));
            String[] selectionArgs = args.toArray(new String[args.size()]);
            Cursor cur = mResolver.query(LocMessDBContract.PostedMessages.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            assert cur != null;
            cur.getCount();
            return cur;

        }

        private String makePlaceholders(int length) {
            StringBuilder builder = new StringBuilder();
            builder.append("?");
            for (int i = 1; i < length; i++) {
                builder.append(", ?");
            }
            return builder.toString();
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, Void> {

        private SparseArray<ContentValues> messages;
        private String ip;
        private int port;

        MyAsyncTask(SparseArray<ContentValues> messages, String ip, int port) {
            this.messages = messages;
            this.ip = ip;
            this.port = port;
        }

        @Override
        protected Void doInBackground(String... strings) {
            Log.d(TAG, "MyAsyncTask starting");
            String request = strings[0];
            Intent intent = new Intent(P2pMessageScannerService.this, P2pMessageSenderService.class);
            intent.putExtra(P2pMessageSenderService.INTENT_DATA, request);
            intent.putExtra(P2pMessageSenderService.INTENT_RECEIVER, new P2pCommResponseReceiver(messages, ip, port));
            intent.putExtra(P2pMessageSenderService.INTENT_IPADDR, ip);
            intent.putExtra(P2pMessageSenderService.INTENT_PORT, port);
            startService(intent);
            return null;
        }
    }

    private class P2pCommResponseReceiver extends ResultReceiver {
        private SparseArray<ContentValues> messages;
        private String ip;
        private int port;

        public P2pCommResponseReceiver(SparseArray<ContentValues> messages, String ip, int port) {
            super(null);
            this.messages = messages;
            this.ip = ip;
            this.port = port;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == P2pMessageSenderService.MATCH_RESULT_CODE) {
                String response = resultData.getString(P2pMessageSenderService.RESULT_DATA);

                AccountManager manager = AccountManager.get(getBaseContext());
                Account account = AccountService.getActiveAccount(manager);
                assert account != null;
                String author = account.name;

                Gson gson = new GsonBuilder().setDateFormat(RequestBuilder.DATE_FORMAT).create();
                Type type = new TypeToken<List<P2pMatchResponseElement>>(){}.getType();
                List<P2pMatchResponseElement> jresponse = gson.fromJson(response, type);
                List<ist.meic.cmu.locmess_client.data.Message> messagesRequest = new ArrayList<>();
                for (P2pMatchResponseElement element : jresponse) {
                    if (element.isSend()) {
                        ContentValues values = messages.get(element.getId());
                        ist.meic.cmu.locmess_client.data.Message message = new ist.meic.cmu.locmess_client.data.Message();
                        message.setId(element.getId()); // unique p2p id
                        message.setAuthor(author);
                        message.setText(values.getAsString(LocMessDBContract.PostedMessages.COLUMN_CONTENT));
                        message.setTitle(values.getAsString(LocMessDBContract.PostedMessages.COLUMN_TITLE));
                        message.setFromDate(DateUtils.parsetDateDbToLocale(values.getAsString(LocMessDBContract.PostedMessages.COLUMN_DATE_FROM)));
                        message.setToDate(DateUtils.parsetDateDbToLocale(values.getAsString(LocMessDBContract.PostedMessages.COLUMN_DATE_TO)));
                        ist.meic.cmu.locmess_client.data.Location location = new ist.meic.cmu.locmess_client.data.Location();
                        location.setId(values.getAsInteger(LocMessDBContract.PostedMessages.COLUMN_LOCATION_SERVER_ID));
                        location.setName(values.getAsString(LocMessDBContract.PostedMessages.COLUMN_LOCATION));
                        message.setLocation(location);
                        messagesRequest.add(message);
                    }
                }

                if (messagesRequest.isEmpty()) {
                    Log.i(TAG, "No messages to send");
                    return;
                }

                P2pRequest request = new P2pRequest();
                request.setType(P2pRequest.TYPE_MESSAGES);
                request.setData(gson.toJsonTree(messagesRequest));
                String jrequest = gson.toJson(request);
                Log.d(TAG, jrequest);

                Intent intent = new Intent(P2pMessageScannerService.this, P2pMessageSenderService.class);
                intent.putExtra(P2pMessageSenderService.INTENT_DATA, jrequest);
                intent.putExtra(P2pMessageSenderService.INTENT_IPADDR, ip);
                intent.putExtra(P2pMessageSenderService.INTENT_PORT, port);
                startService(intent);
            }
        }
    }

    private static int generateP2pId(String author, int id) {
        return (String.valueOf(id).concat(author)).hashCode();
    }

    private static int generateP2pId(Context context, int id) {
        AccountManager am = AccountManager.get(context);
        Account account = AccountService.getActiveAccount(am);
        assert account != null;
        return generateP2pId(account.name, id);
    }
}
