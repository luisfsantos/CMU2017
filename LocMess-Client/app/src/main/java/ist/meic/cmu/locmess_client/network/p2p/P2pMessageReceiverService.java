package ist.meic.cmu.locmess_client.network.p2p;

import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ist.meic.cmu.locmess_client.data.KeyPair;
import ist.meic.cmu.locmess_client.network.p2p.json.P2pMatchDataElement;
import ist.meic.cmu.locmess_client.network.p2p.json.P2pMatchResponseElement;
import ist.meic.cmu.locmess_client.network.p2p.json.P2pRequest;
import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.DateUtils;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

/**
 * Created by Catarina on 13/05/2017.
 */

public class P2pMessageReceiverService extends Service {
    private static final String TAG = "P2pMsgReceiverService";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private boolean mStarted;

    @Override
    public void onCreate() {
        super.onCreate();
        SimWifiP2pSocketManager.Init(getApplicationContext());
        HandlerThread thread = new HandlerThread("P2pMessageReceiverThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mStarted = false;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping service...");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mStarted) {
            Log.d(TAG, "Service starting...");
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            // trigger a new scan
            mServiceHandler.sendMessage(msg);
            mStarted = true;
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "Receiver triggered");
            SimWifiP2pSocketServer srvSocket;

            try {
                srvSocket = new SimWifiP2pSocketServer(10001);
            } catch (IOException e) {
                Log.e(TAG, "IO error: ", e);
                return;
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket socket = srvSocket.accept();
                    Log.d(TAG, "Accepted a connection.");
                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        String message = sockIn.readLine();
                        Log.d(TAG, "Received: " + message);
                        String response = handleRequest(message);
                        socket.getOutputStream().write((response + "\n").getBytes());
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading socket: ", e);
                    } finally {
                        socket.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error socket: ", e);
                    srvSocket = null;
                    break;
                }
            }
            if (srvSocket != null) {
                try {
                    srvSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing socket: ", e);
                }
            }
        }

        private String handleRequest(String jrequest) {
            List<KeyPair> myKeypairs = getKeypairs();
            Gson gson = new GsonBuilder().setDateFormat(RequestBuilder.DATE_FORMAT).create();
            P2pRequest request = gson.fromJson(jrequest, P2pRequest.class);
            JsonElement jdata = request.getData();
            switch (request.getType()) {
                case P2pRequest.TYPE_MATCH:
                    List<P2pMatchResponseElement> response = new ArrayList<>();

                    Type type = new TypeToken<List<P2pMatchDataElement>>(){}.getType();
                    List<P2pMatchDataElement> data = gson.fromJson(jdata, type);
                    for (P2pMatchDataElement p2pData : data) {
                        //TODO check if already received that message using the unique p2p id - if so add false
                        boolean match = validatePolicy(myKeypairs, p2pData.getWhitelist(), p2pData.getBlacklist());
                        response.add(new P2pMatchResponseElement(p2pData.getId(), match));
                    }
                    return gson.toJson(response);
                case P2pRequest.TYPE_MESSAGES:
                    Type type1 = new TypeToken<List<ist.meic.cmu.locmess_client.data.Message>>(){}.getType();
                    List<ist.meic.cmu.locmess_client.data.Message> messageList = gson.fromJson(jdata, type1);
                    new DatabaseWriterTask(messageList).execute();
                    break;
            }
            return "ACK";
        }

        private boolean validatePolicy(List<KeyPair> myKeypairs, List<KeyPair> whitelist, List<KeyPair> blacklist) {
            List<KeyPair> whitelistIntersect = new ArrayList<>(whitelist);
            whitelistIntersect.retainAll(myKeypairs);
            boolean inWhitelist = whitelist.size() == 0 || !whitelistIntersect.isEmpty();
            if (!inWhitelist) {
                return false;
            }

            List<KeyPair> blacklistIntersect = new ArrayList<>(blacklist);
            blacklistIntersect.retainAll(myKeypairs);
            boolean inBlacklist = !blacklistIntersect.isEmpty();

            return inWhitelist && !inBlacklist;
        }

        private List<KeyPair> getKeypairs() {
            List<KeyPair> list = new ArrayList<>();
            String[] projection = {LocMessDBContract.KeyPair.COLUMN_KEY,
                    LocMessDBContract.KeyPair.COLUMN_VALUE};
            Cursor c = getContentResolver().query(LocMessDBContract.KeyPair.CONTENT_URI,projection , null, null, null);
            assert c != null;
            while (c.moveToNext()) {
                ContentValues values = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(c, values);
                list.add(new KeyPair(values.getAsString(LocMessDBContract.KeyPair.COLUMN_KEY),
                        values.getAsString(LocMessDBContract.KeyPair.COLUMN_VALUE)));
            }
            c.close();
            return list;
        }

        private class DatabaseWriterTask extends AsyncTask<Object, Void, Void> {

            private List<ist.meic.cmu.locmess_client.data.Message> messages;

            DatabaseWriterTask(List<ist.meic.cmu.locmess_client.data.Message> messages) {
                this.messages = messages;
            }

            @Override
            protected Void doInBackground(Object[] objects) {
                ArrayList<ContentProviderOperation> batch = new ArrayList<>();

                for (ist.meic.cmu.locmess_client.data.Message m : messages) {
                    batch.add(ContentProviderOperation.newInsert(LocMessDBContract.AvailableP2pMessages.CONTENT_URI)
                            .withValue(LocMessDBContract.AvailableP2pMessages.COLUMN_TITLE, m.getTitle())
                            .withValue(LocMessDBContract.AvailableP2pMessages.COLUMN_CONTENT, m.getText())
                            .withValue(LocMessDBContract.AvailableP2pMessages.COLUMN_AUTHOR, m.getAuthor())
                            .withValue(LocMessDBContract.AvailableP2pMessages.COLUMN_DATE_POSTED, DateUtils.formatDateTimeLocaleToDb(m.getFromDate()))
                            .withValue(LocMessDBContract.AvailableP2pMessages.COLUMN_LOCATION, m.getLocation().getName())
                            .withValue(LocMessDBContract.AvailableP2pMessages.COLUMN_READ, LocMessDBContract.AvailableP2pMessages.MESSAGE_NOT_READ)
                            .withValue(LocMessDBContract.AvailableP2pMessages.COLUMN_P2P_ID, m.getId())
                            .build());
                }
                try {
                    ContentResolver contentResolver = getContentResolver();
                    contentResolver.applyBatch(LocMessDBContract.AUTHORITY, batch);
                    contentResolver.notifyChange(
                            LocMessDBContract.AvailableP2pMessages.CONTENT_URI,
                            null,                                            // no local observer
                            false);
                    //notify observers of aggregate view
                    contentResolver.notifyChange(LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P, null);

                } catch (RemoteException | OperationApplicationException e) {
                    Log.e(TAG, "Error updating database: " + e.getMessage());
                }
                return null;
            }
        }
    }
}
