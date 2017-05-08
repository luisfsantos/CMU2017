package ist.meic.cmu.locmess_client.network.sync;

/**
 * Created by Catarina on 25/04/2017.
 */

import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ist.meic.cmu.locmess_client.authentication.GenericAccountService;
import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;

/**
 * Static helper methods for working with the sync framework.
 */
public class SyncUtils {
    private static final long SYNC_FREQUENCY = 60 * 60;  // 1 hour (in seconds)
    private static final String CONTENT_AUTHORITY = LocMessDBContract.AUTHORITY;

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
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_PULL, PULL_LOCATIONS, PULL_KEYPAIRS})
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
     * Helper method to trigger an immediate sync ("refresh").
     *
     * <p>This should only be used when we need to preempt the normal sync schedule. Typically, this
     * means the user has pressed the "refresh" button.
     *
     * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
     * preserve battery life. If you know new data is available (perhaps via a GCM notification),
     * but the user is not actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */
    private static void triggerSync(Context context, Bundle bundle) {
        AccountManager am = AccountManager.get(context);
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(
                GenericAccountService.GetActiveAccount(am), // Sync account
                CONTENT_AUTHORITY,                       // Content authority
                bundle);                                 // Extras
    }

    public static void push(@NonNull Context context, @PushWhat int what, RequestData data, Uri databaseEntry) {
        Bundle bundle = new Bundle();
        bundle.putInt(PUSH_WHAT, what);
        bundle.putInt(SYNC_TYPE, SYNC_PUSH);
        bundle.putString(REQUEST_URL, data.getStringUrl());
        bundle.putInt(REQUEST_METHOD, data.getRequestMethod());
        bundle.putString(REQUEST_JSON, data.getJson());
        bundle.putString(DB_ENTRY_URI, databaseEntry == null ? null : databaseEntry.toString());
        triggerSync(context, bundle);
    }

    public static void pull(@NonNull Context context, @PullWhat int what) {
        Bundle bundle = new Bundle();
        bundle.putInt(PULL_WHAT, what);
        bundle.putInt(SYNC_TYPE, SYNC_PULL);
        bundle.putString(REQUEST_URL, LocMessURL.LIST_LOCATIONS);
        bundle.putInt(REQUEST_METHOD, RequestData.GET);
        triggerSync(context, bundle);
    }
}

