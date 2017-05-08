package ist.meic.cmu.locmess_client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ist.meic.cmu.locmess_client.authentication.GenericAccountService;
import ist.meic.cmu.locmess_client.messages.inbox.InboxActivity;
import ist.meic.cmu.locmess_client.network.location_update.AlarmReceiver;
import ist.meic.cmu.locmess_client.network.location_update.LocationUpdateService;

/**
 * Created by Catarina on 08/05/2017.
 */

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";

    private BroadcastReceiver mPermissionReceiver = null;

    private AccountManager am;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash_screen);
        am = AccountManager.get(getBaseContext());
        Account[] accounts = am.getAccountsByType(GenericAccountService.ACCOUNT_TYPE);
        Log.d(TAG, "Accounts.length="+accounts.length);
        if (accounts.length < 1) {
//            Intent intent = new Intent(SplashScreen.this, AuthenticatorActivity.class);
//            intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, GenericAccountService.ACCOUNT_TYPE);
//            intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
//            startActivityForResult(intent, 42);
            am.addAccount(GenericAccountService.ACCOUNT_TYPE,
                    GenericAccountService.AUTH_TOKEN_TYPE,
                    null, null, this, callback, null);
        } else {
            setupLocationUpdateService();
            finish();
        }
    }

    private void setupLocationUpdateService() {
        mPermissionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bnd = intent.getExtras();
                if (bnd.getBoolean(LocationUpdateService.KEY_RESULT, false)) {
                    setupLocationUpdatesAlarm(); // FIXME activate this later!!
                    Intent i = new Intent(SplashScreen.this, InboxActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.PERMISSION_GRANTED_ACTION));
        registerReceiver(mPermissionReceiver, filter);

        Intent serviceIntent = new Intent(SplashScreen.this, LocationUpdateService.class);
        startService(serviceIntent);

    }

    private void setupLocationUpdatesAlarm() {
        Context context = getBaseContext();
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);

        // inexact repeating to reduce battery drain
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, //trigger in 5 seconds (forced by Android 5+)
                AlarmReceiver.REPEAT_INTERVAL, pendingIntent);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.pref_currentAlarmInterval), AlarmReceiver.REPEAT_INTERVAL);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        if (mPermissionReceiver != null) {
            unregisterReceiver(mPermissionReceiver);
        }
        super.onDestroy();
    }

    AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {

        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            if (future.isDone() && !future.isCancelled()) {
                setupLocationUpdateService();
            }
            finish();
        }
    };
}
