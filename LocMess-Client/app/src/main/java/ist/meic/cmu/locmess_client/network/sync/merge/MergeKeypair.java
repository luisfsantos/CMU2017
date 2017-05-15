package ist.meic.cmu.locmess_client.network.sync.merge;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import ist.meic.cmu.locmess_client.data.KeyPair;
import ist.meic.cmu.locmess_client.network.WebRequestResult;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.network.json.deserializers.KeypairDeserializer;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;

/**
 * Created by Catarina on 05/05/2017.
 */

public class MergeKeypair {
    private static final String TAG = "MergeKeypair";
    private MergeKeypair() {}

    public static int mergeAll(ContentResolver contentResolver, JsonArray keypairs, @Nullable SyncResult syncResult) throws RemoteException, OperationApplicationException {
        Log.i(TAG, "Parsing json into Keypair map");
        SparseArray<KeyPair> remoteKeypairs =
                new KeypairDeserializer().parseAll(keypairs);
        Log.i(TAG, "Parsing complete. Found " + remoteKeypairs.size() + " remote entries");

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        Log.i(TAG, "Fetching local entries for merge");
        Uri uri = LocMessDBContract.KeyPair.CONTENT_URI;
        Cursor c = contentResolver.query(uri,
                LocMessDBContract.KeyPair.DEFAULT_PROJECTION,
                null, null, null);
        assert c != null;
        Log.i(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");

        // find stale data
        int serverId;
        int dbId;
        while (c.moveToNext()) {
            if (syncResult != null) {
                syncResult.stats.numEntries++;
            }
            serverId = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.KeyPair.COLUMN_SERVER_ID));
            dbId = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.KeyPair._ID));
            KeyPair match = remoteKeypairs.get(serverId);
            if (match != null) {
                // entry exists. remove from remote keypairs map to prevent insert later
                remoteKeypairs.remove(serverId);
                // we are not updating anything in the local entry
            } else {
                // entry doesn't exist. remove it from the database
                Uri deleteUri = ContentUris.withAppendedId(LocMessDBContract.KeyPair.CONTENT_URI, dbId);
                Log.i(TAG, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                if (syncResult != null) {
                    syncResult.stats.numDeletes++;
                }
            }
        }
        c.close();

        // add new items
        int newKeypairsCount = remoteKeypairs.size();
        for (int i = 0; i < remoteKeypairs.size(); i++) {
            KeyPair k = remoteKeypairs.valueAt(i);
            Log.i(TAG, "Scheduling insert: server_id=" + k.getId());
            batch.add(ContentProviderOperation.newInsert(LocMessDBContract.KeyPair.CONTENT_URI)
                    .withValue(LocMessDBContract.KeyPair.COLUMN_KEY, k.getKey())
                    .withValue(LocMessDBContract.KeyPair.COLUMN_VALUE, k.getValue())
                    .withValue(LocMessDBContract.KeyPair.COLUMN_SERVER_ID, k.getId())
                    .build());
            if (syncResult != null) {
                syncResult.stats.numInserts++;
            }
        }

        Log.i(TAG, "Merge solution ready. Applying batch update");
        contentResolver.applyBatch(LocMessDBContract.AUTHORITY, batch);
        contentResolver.notifyChange(
                LocMessDBContract.KeyPair.CONTENT_URI, // URI where data was modified
                null,                                   // no local observer
                false);                                 // IMPORTANT: do not sync do network
        return newKeypairsCount;
    }

    public static void fillInServerId(ContentResolver contentResolver, Uri databaseEntryUri, String result, @Nullable SyncResult syncResult) {
        @WebRequestResult.ReturnedObject String label = WebRequestResult.KEYPAIR;
        JsonObjectAPI jresult = new Gson().fromJson(result, JsonObjectAPI.class);
        JsonObject jkeypair = jresult.getData().getAsJsonObject(label);
        KeyPair keypair = new KeypairDeserializer().parse(jkeypair);
        int serverId = keypair.getId();

        ContentValues values = new ContentValues();
        values.put(LocMessDBContract.KeyPair.COLUMN_SERVER_ID, serverId);
        contentResolver.update(databaseEntryUri, values, null, null);
        if (syncResult != null) {
            syncResult.stats.numUpdates++;
        }

        Log.i(TAG, "Filled server_id=" + serverId + " of entry " + databaseEntryUri);
    }
}
