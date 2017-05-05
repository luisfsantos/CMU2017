package ist.meic.cmu.locmess_client.network.sync;

/**
 * Created by Catarina on 25/04/2017.
 */

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.stream.Stream;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.WebRequest;
import ist.meic.cmu.locmess_client.network.WebRequestResult;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.network.json.serializers.LocationSerializer;
import ist.meic.cmu.locmess_client.network.sync.merge.MergeLocation;

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

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
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
                    push(extras, syncResult);
                    break;
                case SyncUtils.SYNC_PULL:
                    Log.i(TAG, "Performing pull from server...");
                    pull(extras, syncResult);
                    break;
                case SyncUtils.NO_SYNC:
                    Log.i(TAG, "No sync");
            }
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Feed URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating database: " + e.getMessage());
            syncResult.databaseError = true;
        }
        Log.i(TAG, "Network synchronization complete");
    }

    private void push(Bundle extras, SyncResult syncResult) throws IOException {
        String url = extras.getString(SyncUtils.REQUEST_URL);
        @RequestData.RequestMethod int requestMethod = extras.getInt(SyncUtils.REQUEST_METHOD);
        String json = extras.getString(SyncUtils.REQUEST_JSON);
        RequestData request = new RequestData(url, requestMethod, json);
        Uri databaseEntry = Uri.parse(extras.getString(SyncUtils.DB_ENTRY_URI));
        @WebRequestResult.ReturnedObject String returnedObjLabel;
        @SyncUtils.PushWhat int pushWhat = extras.getInt(SyncUtils.PUSH_WHAT, SyncUtils.NO_PUSH);

        SharedPreferences pref = getContext().getSharedPreferences(getContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String jwt = pref.getString(getContext().getString(R.string.pref_jwtAuthenticator), "No jwt");
        WebRequestResult response = new WebRequest(request, jwt).execute();
        switch (pushWhat) {
            case SyncUtils.CREATE_LOCATION:
                returnedObjLabel = WebRequestResult.LOCATION;
                MergeLocation.fillInServerId(mContentResolver,
                        databaseEntry,
                        response.getResult(),
                        returnedObjLabel,
                        syncResult);
                break;
            case SyncUtils.CREATE_MESSAGE:
                returnedObjLabel = WebRequestResult.MESSAGE;
                // TODO: 05/05/2017 fill server id
                break;
            case SyncUtils.CREATE_KEYPAIR:
                returnedObjLabel = null;// FIXME: 04/05/2017 define according to api
                // TODO: 05/05/2017 fill server id
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

    private void pull(Bundle extras, SyncResult syncResult) throws IOException, RemoteException, OperationApplicationException {
        String url = extras.getString(SyncUtils.REQUEST_URL);
        @RequestData.RequestMethod int requestMethod = extras.getInt(SyncUtils.REQUEST_METHOD);
        String json = extras.getString(SyncUtils.REQUEST_JSON);
        @SyncUtils.PullWhat int pullWhat = extras.getInt(SyncUtils.PULL_WHAT, SyncUtils.NO_PULL);

        if (pullWhat == SyncUtils.NO_PULL) {
            Log.i(TAG, "Syncing without expecting a result from the server (pull failed)");
            return;
        }

        RequestData request = new RequestData(url, requestMethod, json);
        SharedPreferences pref = getContext().getSharedPreferences(getContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String jwt = pref.getString(getContext().getString(R.string.pref_jwtAuthenticator), "No jwt");
        WebRequestResult response = new WebRequest(request, jwt).execute();
        @WebRequestResult.ReturnedObject String returnedObjLabel;
        switch (pullWhat) {
            case SyncUtils.PULL_LOCATIONS:
                returnedObjLabel = WebRequestResult.LOCATIONS;
                JsonObjectAPI jresult = new Gson().fromJson(response.getResult(), JsonObjectAPI.class);
                JsonArray jlocations = jresult.getData().getAsJsonArray(returnedObjLabel);
                Log.d(TAG, "Locations: " + jlocations.toString());
                MergeLocation.mergeAll(mContentResolver, jlocations, syncResult);
                break;
            case SyncUtils.PULL_KEYPAIRS:
                // TODO: 04/05/2017
//                returnedObj = WebRequestResult.KEYPAIRS;
                break;
        }
    }
}