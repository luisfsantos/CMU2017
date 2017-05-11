package ist.meic.cmu.locmess_client.network.location_update;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.location.create.NewWifiLocationFragment;
import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Catarina on 01/05/2017.
 */

public class LocationUpdateService extends Service implements LocationListener,
        SimWifiP2pManager.PeerListListener {

    private static final String TAG = "LocationUpdateService";
    public static final String KEY_RECEIVER = "PermissionReceiver";
    public static final String KEY_RESULT = "PermissionResult";
    GoogleApiClient googleApiClient;
    private static final long UPDATE_INTERVAL = 90 * 1000; // 1 minute 30 seconds
    private static final long FASTEST_UPDATE_INTERVAL = 60 * 1000; // 1 minute
    private volatile ServiceHandler mHandler;
    private volatile Looper mServiceLooper;


    private final class ServiceHandler extends Handler implements GoogleApiClient.ConnectionCallbacks {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void onConnected(Bundle bundle) {
            if (ContextCompat.checkSelfPermission(LocationUpdateService.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Registering request for location updates...");
                requestLocationUpdates();
            } else {
                Log.i(TAG, "Requesting permission to access fine location");
                Intent intent = new Intent(LocationUpdateService.this, LocationPermissionsActivity.class);
                intent.putExtra(KEY_RECEIVER, new PermissionReceiver());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, "Google API Client connection suspended");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        mServiceLooper = thread.getLooper();
        mHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting service...");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(mHandler)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
        if (!mTermiteBound) {
            Intent termiteIntent = new Intent(this, SimWifiP2pService.class);
            bindService(termiteIntent, mConnection, Context.BIND_AUTO_CREATE);
            mTermiteBound = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
            registerReceiver(mOnRefreshPeers, filter);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("MissingPermission")
    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location changed. Storing location...");
        storeCurrentGPSLocation(location);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping service...");
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
        if (mTermiteBound) {
            unregisterReceiver(mOnRefreshPeers);
            unbindService(mConnection);
        }
        super.onDestroy();
    }

    private class PermissionReceiver extends ResultReceiver {
        PermissionReceiver() {
            super(null);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == RESULT_OK) {
                if (resultData.getInt(KEY_RESULT) == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates();
                } else {
                    Log.i(TAG, "No permission to access fine location");
                    stopSelf();
                }
            } else {
                Log.wtf(TAG, "Received wrong result from LocationPermissionActivity");
            }
        }
    }


    private void storeCurrentGPSLocation(Location location) {
        Context context = getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        putDouble(editor, context.getString(R.string.pref_currLatitude), location.getLatitude()); // put latitude
        putDouble(editor, context.getString(R.string.pref_currLongitude), location.getLongitude()); // put longitude
        editor.apply();
    }

    private void storeCurrentWifiLocation(Set<String> ssids) {
        Context context = getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(context.getString(R.string.pref_currLocationSsids), ssids);
        editor.apply();
    }

    private SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    /*
     * TERMITE SETUP AND CALLBACKS
     **/

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private boolean mTermiteBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mTermiteBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mManager = null;
            mChannel = null;
            mTermiteBound = false;
        }
    };

    private BroadcastReceiver mOnRefreshPeers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                if (mTermiteBound) {
                    mManager.requestPeers(mChannel, LocationUpdateService.this);
                } else {
                    Log.i(TAG, "Service not bound");
                }
            }
        }
    };

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        Log.d(TAG, "#peers=" + peers.getDeviceList().size());
        Set<String> ssids = new HashSet<>();
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            ssids.add(device.deviceName);
        }
        storeCurrentWifiLocation(Collections.unmodifiableSet(ssids));
    }
}
