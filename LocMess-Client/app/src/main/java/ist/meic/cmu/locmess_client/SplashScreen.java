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
import ist.meic.cmu.locmess_client.network.location_update.LocationUpdateService;
import ist.meic.cmu.locmess_client.network.location_update.UpdateLocationAlarmReceiver;
import ist.meic.cmu.locmess_client.network.p2p.P2pDeliveryAlarmReceiver;
import ist.meic.cmu.locmess_client.network.sync.SyncUtils;

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
        am = AccountManager.get(getBaseContext());
        Account[] accounts = am.getAccountsByType(GenericAccountService.ACCOUNT_TYPE);
        Log.d(TAG, "Accounts.length="+accounts.length);
        if (accounts.length < 1) {
            am.addAccount(GenericAccountService.ACCOUNT_TYPE,
                    GenericAccountService.AUTH_TOKEN_TYPE,
                    null, null, this, callback, null);
        } else {
            setupLocationUpdateService();
        }
    }

    private void setupLocationUpdateService() {
        mPermissionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bnd = intent.getExtras();
                if (bnd.getBoolean(LocationUpdateService.KEY_RESULT, false)) {
                    setupLocationUpdatesAlarm();
                    P2pDeliveryAlarmReceiver.scheduleAlarm(getApplicationContext());
                    SyncUtils.initialSync(getBaseContext());
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
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        int currentAlarmInterval = UpdateLocationAlarmReceiver.scheduleAlarm(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getString(R.string.pref_currentAlarmInterval), currentAlarmInterval);
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
            } else {
                finish();
            }
        }
    };
}
