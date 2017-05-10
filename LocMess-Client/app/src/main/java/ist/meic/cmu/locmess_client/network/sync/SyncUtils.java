package ist.meic.cmu.locmess_client.network.sync;

/**
 * Created by Catarina on 25/04/2017.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;

/**
 * Static helper methods for working with the sync framework.
 */
public class SyncUtils {
    private static final long SYNC_FREQUENCY = 60 * 60;  // 1 hour (in seconds)
    private static final String CONTENT_AUTHORITY = LocMessDBContract.AUTHORITY;
    private static final String PREF_SETUP_COMPLETE = "setup_complete";
    private static String username;

    static final String REQUEST_URL = "url";
    static final String REQUEST_METHOD = "request_method";
    static final String REQUEST_JSON = "request_json";
    static final String DB_ENTRY_URI = "db_entry_uri";

    static final String SYNC_TYPE = "sync_type";
    static final int NO_SYNC = 0;
    static final int SYNC_PULL = 1;
    static final int SYNC_PUSH = 2;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_SYNC, SYNC_PULL, SYNC_PUSH}) public @interface SyncType {}

    static final String PULL_WHAT = "pull_what";
    public static final int NO_PULL = 0;
    public static final int PULL_LOCATIONS = 1;
    public static final int PULL_KEYPAIRS = 2;
    public static final int PULL_KEYS = 3;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_PULL, PULL_LOCATIONS, PULL_KEYPAIRS, PULL_KEYS})
    public @interface PullWhat {}

    static final String PUSH_WHAT = "push_what";
    public static final int NO_PUSH = 0;
    public static final int CREATE_LOCATION = 1;
    public static final int DELETE_LOCATION = 2;
    public static final int CREATE_MESSAGE = 3;
    public static final int DELETE_MESSAGE = 4;
    public static final int CREATE_KEYPAIR = 5;
    public static final int DELETE_KEYPAIR = 6;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_PUSH, CREATE_LOCATION, DELETE_LOCATION, CREATE_MESSAGE, DELETE_MESSAGE, CREATE_KEYPAIR, DELETE_KEYPAIR})
    public @interface PushWhat {}


    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     *
     * @param context Context
     */
    public static void CreateSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);

        SharedPreferences pref = context.getSharedPreferences(
                context.getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        username = pref.getString(context.getResources().getString(R.string.pref_username), "username");
        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = GenericAccountService.GetAccount(username);
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, false);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
//            ContentResolver.addPeriodicSync(
//                    account, CONTENT_AUTHORITY, new Bundle(),SYNC_FREQUENCY);
            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
//            triggerSync();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
        }
    }

    private static void triggerSync() {
        triggerSync(new Bundle());
    }

    /**
     * Helper method to trigger an immediate sync.
     */
    private static void triggerSync(Bundle bundle) {
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(
                GenericAccountService.GetAccount(username),      // Sync account
                CONTENT_AUTHORITY,                       // Content authority
                bundle);                                 // Extras
    }

    public static void push(@PushWhat int what, @NonNull RequestData data, @Nullable Uri databaseEntry) {
        Bundle bundle = new Bundle();
        bundle.putInt(PUSH_WHAT, what);
        bundle.putInt(SYNC_TYPE, SYNC_PUSH);
        bundle.putString(REQUEST_URL, data.getStringUrl());
        bundle.putInt(REQUEST_METHOD, data.getRequestMethod());
        bundle.putString(REQUEST_JSON, data.getJson());
        bundle.putString(DB_ENTRY_URI, databaseEntry == null ? null : databaseEntry.toString());
        triggerSync(bundle);
    }

    public static void pull(@PullWhat int what, @NonNull String url) {
        Bundle bundle = new Bundle();
        bundle.putInt(PULL_WHAT, what);
        bundle.putInt(SYNC_TYPE, SYNC_PULL);
        bundle.putString(REQUEST_URL, url);
        bundle.putInt(REQUEST_METHOD, RequestData.GET);
        triggerSync(bundle);
    }
}

