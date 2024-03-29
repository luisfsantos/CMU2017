package ist.meic.cmu.locmess_client.network.sync;

/**
 * Created by Catarina on 25/04/2017.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.authentication.AccountService;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.WebRequest;
import ist.meic.cmu.locmess_client.network.WebRequestResult;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.network.sync.merge.MergeKey;
import ist.meic.cmu.locmess_client.network.sync.merge.MergeKeypair;
import ist.meic.cmu.locmess_client.network.sync.merge.MergeLocation;
import ist.meic.cmu.locmess_client.network.sync.merge.MergeMessage;

/**
 * Define a sync adapter for the app.
 *
 * <p>This class is instantiated in {@link SyncService}, which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 *
 * <p>The system calls onPerformSync() via an RPC call through the IBinder object supplied by
 * SyncService.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;
    private final AccountManager am;


    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        am = AccountManager.get(context);
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        am = AccountManager.get(context);
    }

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, parse it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run <em>in situ</em>, and you don't have to set up a separate thread for them.
     .
     *
     * <p>This is where we actually perform any work required to perform a sync.
     * {@link AbstractThreadedSyncAdapter} guarantees that this will be called on a non-UI thread,
     * so it is safe to peform blocking I/O here.
     *
     * <p>The syncResult argument allows you to pass information back to the method that triggered
     * the sync.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        Log.i(TAG, "Beginning network synchronization");
        try {
            @SyncUtils.SyncType int syncType = extras.getInt(SyncUtils.SYNC_TYPE, SyncUtils.NO_SYNC);
            switch (syncType) {
                case SyncUtils.SYNC_PUSH:
                    Log.i(TAG, "Performing push to server...");
                    push(account, extras, syncResult);
                    break;
                case SyncUtils.SYNC_PULL:
                    Log.i(TAG, "Performing pull from server...");
                    pull(account, extras, syncResult);
                    break;
                case SyncUtils.NO_SYNC:
                    Log.i(TAG, "No sync");
            }
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating database: " + e.getMessage());
            syncResult.databaseError = true;
        } catch (AuthenticatorException | OperationCanceledException e) {
            Log.e(TAG, "Error renewing JWT token: " + e.getMessage());
        }
        Log.i(TAG, "Network synchronization complete");
    }

    private void push(Account account, Bundle extras, SyncResult syncResult)
            throws IOException, AuthenticatorException, OperationCanceledException {
        String url = extras.getString(SyncUtils.REQUEST_URL);
        @RequestData.RequestMethod int requestMethod = extras.getInt(SyncUtils.REQUEST_METHOD);
        String json = extras.getString(SyncUtils.REQUEST_JSON);
        RequestData request = new RequestData(url, requestMethod, json);
        Uri databaseEntry = Uri.parse(extras.getString(SyncUtils.DB_ENTRY_URI, Uri.EMPTY.toString()));
        @SyncUtils.PushWhat int pushWhat = extras.getInt(SyncUtils.PUSH_WHAT, SyncUtils.NO_PUSH);

        String jwt = am.blockingGetAuthToken(account, AccountService.AUTH_TOKEN_TYPE, false);
        WebRequestResult response = new WebRequest(request, jwt).execute();
        try {
            response.assertValidJwtToken();
        } catch (WebRequestResult.JwtExpiredException e) {
            Log.e(TAG, e.getMessage());
            jwt = AccountService.refreshAuthToken(getContext(), account,
                    AccountService.AUTH_TOKEN_TYPE, jwt);
            response = new WebRequest(request, jwt).execute();
        }

        if (response.getError() != null) {
            throw new IOException(response.getErrorMessages());
        }
        switch (pushWhat) {
            case SyncUtils.CREATE_LOCATION:
                MergeLocation.fillInServerId(mContentResolver,
                        databaseEntry,
                        response.getResult(),
                        syncResult);
                break;
            case SyncUtils.CREATE_MESSAGE:
                MergeMessage.fillInServerId(mContentResolver,
                        databaseEntry,
                        response.getResult(),
                        syncResult);
                break;
            case SyncUtils.CREATE_KEYPAIR:
                MergeKeypair.fillInServerId(mContentResolver,
                        databaseEntry,
                        response.getResult(),
                        syncResult);
                break;
            case SyncUtils.DELETE_MESSAGE:
            case SyncUtils.DELETE_KEYPAIR:
            case SyncUtils.DELETE_LOCATION:
            case SyncUtils.NO_PUSH:
            default:
                Log.i(TAG, "Synced without expecting a result from the server.");
                break;
        }
    }


    private void pull(Account account, Bundle extras, SyncResult syncResult)
            throws IOException, RemoteException, OperationApplicationException,
            AuthenticatorException, OperationCanceledException {

        String url = extras.getString(SyncUtils.REQUEST_URL);
        @RequestData.RequestMethod int requestMethod = extras.getInt(SyncUtils.REQUEST_METHOD);
        String json = extras.getString(SyncUtils.REQUEST_JSON);
        @SyncUtils.PullWhat int pullWhat = extras.getInt(SyncUtils.PULL_WHAT, SyncUtils.NO_PULL);

        if (pullWhat == SyncUtils.NO_PULL) {
            Log.i(TAG, "Sync request without PULL_WHAT specified. Pull returned without contacting the server.");
            return;
        }

        RequestData request = new RequestData(url, requestMethod, json);
        String jwt = am.blockingGetAuthToken(account, AccountService.AUTH_TOKEN_TYPE, false);
        WebRequestResult response = new WebRequest(request, jwt).execute();
        try {
            response.assertValidJwtToken();
        } catch (WebRequestResult.JwtExpiredException e) {
            Log.e(TAG, e.getMessage());
            jwt = AccountService.refreshAuthToken(getContext(), account, AccountService.AUTH_TOKEN_TYPE, jwt);
            response = new WebRequest(request, jwt).execute();
        }
        if (response.getError() != null) {
            throw new IOException(response.getErrorMessages());
        }
        JsonObjectAPI jresult = new Gson().fromJson(response.getResult(), JsonObjectAPI.class);
        switch (pullWhat) {
            case SyncUtils.PULL_LOCATIONS:
                JsonArray jlocations = jresult.getData().getAsJsonArray(WebRequestResult.LOCATIONS);
                Log.d(TAG, "Locations: " + jlocations.toString());
                MergeLocation.mergeAll(mContentResolver, jlocations, syncResult);
                break;
            case SyncUtils.PULL_KEYPAIRS:
                JsonArray jkeypairs = jresult.getData().getAsJsonArray(WebRequestResult.KEYPAIRS);
                Log.d(TAG, "Keypairs: " + jkeypairs.toString());
                MergeKeypair.mergeAll(mContentResolver, jkeypairs, syncResult);
                break;
            case SyncUtils.PULL_KEYS:
                JsonArray jkeys = jresult.getData().getAsJsonArray(WebRequestResult.KEYS);
                Log.d(TAG, "Keys: " + jkeys.toString());
                MergeKey.mergeAll(mContentResolver, jkeys, syncResult);
                break;
        }
    }
}