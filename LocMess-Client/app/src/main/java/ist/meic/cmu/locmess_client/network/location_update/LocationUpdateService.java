package ist.meic.cmu.locmess_client.network.location_update;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by Catarina on 01/05/2017.
 */

public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, LocationListener {

    private static final String TAG = "LocationUpdateService";
    GoogleApiClient googleApiClient;
    private static final long UPDATE_INTERVAL = 90 * 1000; // 1 minute 30 seconds
    private static final long FASTEST_UPDATE_INTERVAL = 60 * 1000; // 1 minute

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting service...");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Registering request for location updates...");
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(FASTEST_UPDATE_INTERVAL);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            Log.i(TAG, "No permission to access fine location");
            stopSelf();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Google API Client connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location changed. Storing location...");
        storeCurrentGPSLocation(location);
    }

    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
        super.onDestroy();
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

    private SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }
}
