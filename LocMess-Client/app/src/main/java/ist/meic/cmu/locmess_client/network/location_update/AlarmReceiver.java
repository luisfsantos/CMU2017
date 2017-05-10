package ist.meic.cmu.locmess_client.network.location_update;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.request_builders.UpdateLocationRequestBuilder;
import ist.meic.cmu.locmess_client.utils.DateUtils;

/**
 * Created by Catarina on 27/04/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    public static int REPEAT_INTERVAL = 60 * 1000; // 1 minute
    private static int MAX_REPEAT_INTERVAL = 5 * 60 * 1000; // 5 minutes
    private static float MAX_TOLERANCE_DISTANCE = 10; // 10 meters? FIXME
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Alarm received");
        mContext = context;
        boolean networkOn = isNetworkOn();
        if (!networkOn) {
            Intent serviceIntent = new Intent(mContext, LocationUpdateService.class);
            context.stopService(serviceIntent);
            doubleAlarmInterval();
            return;
        }

        LocationWrapper previousLocation = getPreviousLocation();
        LocationWrapper  currentLocation = getCurrentLocation();
        if (previousLocation.isEmpty() && currentLocation.isEmpty()) {
            return;
        } else
            // also double alarm interval if location has not changed since last check
            if (!locationHasChanged(previousLocation, currentLocation)) {
            doubleAlarmInterval();
        } else {
            // if network is on and location has changed, we are back to "regular state"
            // set alarm back to base repeat interval, but only if not already done before
            if (getCurrentAlarmInterval() != REPEAT_INTERVAL) {
                rescheduleAlarm(REPEAT_INTERVAL);
            }
        }

        saveCurrentLocationAsPrevious(currentLocation);
        Intent serviceIntent = new Intent(mContext, FetchLocationMessagesService.class);
        Bundle bundle = new Bundle();
        Date now = new Date();
        saveLastUpdatedDate(now);
        try {
            bundle.putSerializable(FetchLocationMessagesService.INTENT_REQUEST,
                    new UpdateLocationRequestBuilder(
                            currentLocation.getLatitude(),
                            currentLocation.getLongitude(),
                            currentLocation.getSsids(),
                            now
                    ).build(LocMessURL.UPDATE_LOCATION, RequestData.POST));
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Malformed URL: " + LocMessURL.UPDATE_LOCATION, e);
            return;
        }
        serviceIntent.putExtra(FetchLocationMessagesService.INTENT_BUNDLE, bundle);
        mContext.startService(serviceIntent);
    }

    private LocationWrapper getPreviousLocation(){
        SharedPreferences pref = mContext.getSharedPreferences(
                mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Set<String> ssids = pref.getStringSet(mContext.getString(R.string.pref_prevLocationSsids), new HashSet<String>());
        double latitude = getDouble(pref, mContext.getString(R.string.pref_prevLatitude), LocationWrapper.NO_LATITUDE);
        double longitude = getDouble(pref, mContext.getString(R.string.pref_prevLongitude), LocationWrapper.NO_LONGITUDE);
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        LocationWrapper wrapper = new LocationWrapper(ssids, location);
        Log.d(TAG, "Previous location - " + wrapper);
        return wrapper;
    }

    private LocationWrapper getCurrentLocation(){
        SharedPreferences pref = mContext.getSharedPreferences(
                mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Set<String> ssids = pref.getStringSet(mContext.getString(R.string.pref_currLocationSsids), new HashSet<String>()); //FIXME get ssids from Termite
        double latitude = getDouble(pref, mContext.getString(R.string.pref_currLatitude), LocationWrapper.NO_LATITUDE);
        double longitude = getDouble(pref, mContext.getString(R.string.pref_currLongitude), LocationWrapper.NO_LONGITUDE);
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        LocationWrapper wrapper = new LocationWrapper(ssids, location);
        Log.d(TAG, "Current location - " + wrapper);
        return wrapper;
    }

    private void saveCurrentLocationAsPrevious(LocationWrapper location){
        SharedPreferences pref = mContext.getSharedPreferences(
                mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(
                mContext.getString(R.string.pref_prevLocationSsids), location.getSsids()); // put ssids
        putDouble(editor, mContext.getString(R.string.pref_prevLatitude), location.getLatitude()); // put latitude
        putDouble(editor, mContext.getString(R.string.pref_prevLongitude), location.getLongitude()); // put longitude
        editor.apply();
    }

    private void saveLastUpdatedDate(Date date) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(
                mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
        editor.putString(mContext.getString(R.string.pref_time_last_updated_msg), DateUtils.formatDateTime(date));
        editor.apply();
    }

    private boolean locationHasChanged(LocationWrapper previousLocation, LocationWrapper currentLocation) {
        return !currentLocation.equals(previousLocation);
    }

    private int getCurrentAlarmInterval() {
        SharedPreferences pref = mContext.getSharedPreferences(
                mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return pref.getInt(mContext.getString(R.string.pref_currentAlarmInterval), REPEAT_INTERVAL);
    }
    private void saveCurrentAlarmInterval(int intervalMillis) {
        SharedPreferences pref = mContext.getSharedPreferences(
                mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(mContext.getString(R.string.pref_currentAlarmInterval), intervalMillis);
        editor.apply();
    }

    private void doubleAlarmInterval() {
        int currentInterval = getCurrentAlarmInterval();
        // we only call the method that reschedules alarm if interval is not already max
        if (currentInterval != MAX_REPEAT_INTERVAL) {
            int nextInterval = currentInterval * 2;
            rescheduleAlarm(nextInterval);
        }
    }

    private void rescheduleAlarm(int intervalMillis) {
        if (intervalMillis > MAX_REPEAT_INTERVAL) {
            intervalMillis = MAX_REPEAT_INTERVAL;
        }
        Log.i(TAG, "Setting alarm interval to " + intervalMillis);
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.cancel(alarmIntent);
        manager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + intervalMillis,
                intervalMillis, alarmIntent);
        saveCurrentAlarmInterval(intervalMillis);
    }

    private SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }
    private double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    private boolean isNetworkOn() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() &&
                (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    private class LocationWrapper {
        private static final double NO_LATITUDE = 0.0;
        private static final double NO_LONGITUDE = 0.0;

        final Set<String> ssids;
        final Location coordinates;

        LocationWrapper(Set<String> ssids, Location coordinates) {
            this.ssids = ssids;
            this.coordinates = coordinates;
        }

        public Set<String> getSsids() {
            return Collections.unmodifiableSet(ssids);
        }

        public Location getCoordinates() {
            return coordinates;
        }

        public double getLatitude() {
            return coordinates.getLatitude();
        }

        public double getLongitude() {
            return coordinates.getLongitude();
        }

        public boolean equals(LocationWrapper location) {
            if (location == this) return true;
            if (location == null) return false;

            if (location.isEmpty()) return false;
            // equality for a set means they have the same size and the same elements
            if (!this.getSsids().equals(location.getSsids())) return false;

            float distance = this.getCoordinates().distanceTo(location.getCoordinates());
            Log.d(TAG, "distance = " + distance);
            // if distance is within the tolerance distance, then we consider the GPS locations to be equal
            if (distance < MAX_TOLERANCE_DISTANCE) {
                return true;
            }
            return false;
        }

        public boolean isEmpty() {
            return ssids.isEmpty() && getLatitude() == NO_LATITUDE && getLongitude() == NO_LONGITUDE;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("SSIDs: [")
                    .append(TextUtils.join(", ", ssids))
                    .append("] ")
                    .append("; ")
                    .append("latitude: ").append(getLatitude()).append(", ")
                    .append("longitude: ").append(getLongitude());
            return builder.toString();
        }
    }
}
