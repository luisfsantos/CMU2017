package ist.meic.cmu.locmess_client.network.location_update;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by Catarina on 27/04/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    public static int REPEAT_INTERVAL = 60 * 1000; // 1 minute
    private static int MAX_REPEAT_INTERVAL = 5 * 60 * 1000; // 5 minutes

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Alarm received");

        Object currentLocation = new Object(); //FIXME Stub! get current location from google location services and Termite
        Object previousLocation = new Object(); //FIXME Stub! (get from shared pref)
        boolean networkOn = isNetworkOn(context);

        //double alarm interval if no network, or user has not changed position since the last alarm
        if (!networkOn || !locationHasChanged(previousLocation, currentLocation)) {
            int nextInterval = getCurrentAlarmInterval(context) * 2;
            rescheduleAlarm(context, nextInterval);
        } else {
            // set timer back to base repeat interval
            if (getCurrentAlarmInterval(context) != REPEAT_INTERVAL) {
                rescheduleAlarm(context, REPEAT_INTERVAL);
            }
            // TODO: 27/04/2017 start LocationUpdateService
        }
    }
    private boolean locationHasChanged(Object previousLocation, Object currentLocation) {
        // TODO: 27/04/2017 compare locations (coordinates) using Haversine function
        // TODO: 27/04/2017 compare list of ssids
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
