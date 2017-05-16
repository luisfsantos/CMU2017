package ist.meic.cmu.locmess_client.network.p2p;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Catarina on 12/05/2017.
 */

public class P2pDeliveryAlarmReceiver extends BroadcastReceiver {

    private static final int REPEAT_INTERVAL = /*6 **/ 60 * 1000; // every 6 minutes fixme

    public static void unscheduleAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, P2pDeliveryAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    public static void scheduleAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, P2pDeliveryAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);

        // inexact repeating to reduce battery drain
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, //trigger in 5 seconds (forced by Android 5+)
                REPEAT_INTERVAL, pendingIntent);
    }
    private Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("P2pDeliveryAlarmRcv", "Alarm received!");
        mContext = context;
        Intent scannerIntent = new Intent(mContext, P2pMessageScannerService.class);
        Intent receiverIntent = new Intent(mContext, P2pMessageReceiverService.class);
        if (isNetworkOn()) {
            mContext.startService(scannerIntent);
            mContext.startService(receiverIntent);
        } else {
            mContext.stopService(scannerIntent);
            mContext.stopService(receiverIntent);
        }
    }

    private boolean isNetworkOn() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() &&
                (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }
}
