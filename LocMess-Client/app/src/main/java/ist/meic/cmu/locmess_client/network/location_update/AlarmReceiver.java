package ist.meic.cmu.locmess_client.network.location_update;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.request_builders.UpdateLocationRequestBuilder;

/**
 * Created by Catarina on 27/04/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    public static int REPEAT_INTERVAL = 60 * 1000; // 1 minute
    private static int MAX_REPEAT_INTERVAL = 5 * 60 * 1000; // 5 minutes
    private static int MAX_TOLERANCE_DISTANCE = 10; // 10 meters? FIXME

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Alarm received");

        Object currentLocation = new Object(); //FIXME Stub! get current location from google location services and Termite
        Object previousLocation = new Object(); //FIXME Stub! (get from shared pref)
        boolean networkOn = isNetworkOn(context);

        // double alarm interval if no network, or user has not changed location since the last alarm
        if (!networkOn || !locationHasChanged(previousLocation, currentLocation)) {
            int currentInterval = getCurrentAlarmInterval(context);
            // we only call the method that reschedules alarm if interval is not already max
            if (currentInterval != MAX_REPEAT_INTERVAL) {
                int nextInterval = currentInterval * 2;
                rescheduleAlarm(context, nextInterval);
            }
        } else {
            // if network is on and location has changed, we are back to "regular state"
            // set alarm back to base repeat interval, but only if not already done before
            if (getCurrentAlarmInterval(context) != REPEAT_INTERVAL) {
                rescheduleAlarm(context, REPEAT_INTERVAL);
            }
        }
        if (networkOn) {
            // FIXME: (DISCUSS) 28/04/2017 i think for the case where the location has not changed but network is on, we should still fetch the messages
            Intent serviceIntent = new Intent(context, LocationUpdateService.class);
            Bundle bundle = new Bundle();
            try {
                bundle.putSerializable(LocationUpdateService.INTENT_REQUEST,
                        new UpdateLocationRequestBuilder().build(LocMessURL.UPDATE_LOCATION, RequestData.POST));
            } catch (MalformedURLException e) {
                Log.wtf(TAG, "Malformed URL: " + LocMessURL.UPDATE_LOCATION);
                return;
            }
            intent.putExtra(LocationUpdateService.INTENT_BUNDLE, bundle);
            context.startService(serviceIntent);
        }
    }

    /**
     * 1. Compares list of SSIDs from both locations. If they're different, then the location has changed.
     * 2. Compares coordinates of both locations, using the usual trigonometry rule. There is no need
     * to use the Haversine method, since we're comparing the distance between two locations, but this
     * distance is constrained by time (it cannot have changed that much since the last time we
     * calculated it - at most MAX_REPEAT_INTERVAL minutes ago), and so the Earth's curvature is irrelevant.
     * If the distance is between the two locations is larger than MAX_TOLERANCE_DISTANCE, then the
     * location has changed.
     *
     * @param previousLocation the previous detected location
     * @param currentLocation the current detected location
     * @return whether the location has changed
     */
    private boolean locationHasChanged(Object previousLocation, Object currentLocation) {
        // TODO: 27/04/2017 compare list of ssids
        // TODO: 27/04/2017 compare coordinates using regular distance between two points (not Haversine function!!)
        return true;
    }

    private int getCurrentAlarmInterval(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return pref.getInt(context.getString(R.string.pref_currentAlarmInterval), REPEAT_INTERVAL);
    }

    private void saveCurrentAlarmInterval(Context context, int intervalMillis) {
        SharedPreferences pref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(context.getString(R.string.pref_currentAlarmInterval), intervalMillis);
        editor.apply();
    }

    private void rescheduleAlarm(Context context, int intervalMillis) {
        if (intervalMillis > MAX_REPEAT_INTERVAL) {
            intervalMillis = MAX_REPEAT_INTERVAL;
        }
        Log.i(TAG, "Setting alarm interval to " + intervalMillis);
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getString(R.string.ALARM_ACTION));
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.cancel(alarmIntent);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + intervalMillis, intervalMillis, alarmIntent);
        saveCurrentAlarmInterval(context, intervalMillis);
    }

    private boolean isNetworkOn(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() &&
                (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }
}
