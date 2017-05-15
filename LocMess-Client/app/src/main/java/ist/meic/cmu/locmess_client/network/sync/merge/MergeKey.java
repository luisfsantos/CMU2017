package ist.meic.cmu.locmess_client.network.sync.merge;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.JsonArray;

import java.util.ArrayList;

import ist.meic.cmu.locmess_client.network.json.deserializers.KeyDeserializer;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;

/**
 * Created by Catarina on 09/05/2017.
 */

public class MergeKey {

    private static final String TAG = "MergeKey";
    private MergeKey() {}

    public static int mergeAll(ContentResolver contentResolver, JsonArray keys, @Nullable SyncResult syncResult) throws RemoteException, OperationApplicationException {
        Log.i(TAG, "Parsing json into Keypair map");
        SparseArray<KeyDeserializer.Key> remoteKeys =
                new KeyDeserializer().parseAll(keys);
        Log.i(TAG, "Parsing complete. Found " + remoteKeys.size() + " remote entries");

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        Log.i(TAG, "Fetching local entries for merge");
        Uri uri = LocMessDBContract.Keys.CONTENT_URI;
        Cursor c = contentResolver.query(uri,
                LocMessDBContract.Keys.DEFAULT_PROJECTION,
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
            serverId = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.Keys.COLUMN_SERVER_ID));
            dbId = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.Keys._ID));
            KeyDeserializer.Key match = remoteKeys.get(serverId);
            if (match != null) {
                // entry exists. remove from remote keys map to prevent insert later
                remoteKeys.remove(serverId);
                // we are not updating anything in the local entry
            } else {
                // entry doesn't exist. remove it from the database
                Uri deleteUri = ContentUris.withAppendedId(LocMessDBContract.Keys.CONTENT_URI, dbId);
                Log.i(TAG, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                if (syncResult != null) {
                    syncResult.stats.numDeletes++;
                }
            }
        }
        c.close();

        // add new items
        int newKeysCount = remoteKeys.size();
        for (int i = 0; i < remoteKeys.size(); i++) {
            KeyDeserializer.Key k = remoteKeys.valueAt(i);
            Log.i(TAG, "Scheduling insert: server_id=" + k.getId());
            batch.add(ContentProviderOperation.newInsert(LocMessDBContract.Keys.CONTENT_URI)
                    .withValue(LocMessDBContract.Keys.COLUMN_NAME, k.getName())
                    .withValue(LocMessDBContract.Keys.COLUMN_SERVER_ID, k.getId())
                    .build());
            if (syncResult != null) {
                syncResult.stats.numInserts++;
            }
        }

        Log.i(TAG, "Merge solution ready. Applying batch update");
        contentResolver.applyBatch(LocMessDBContract.AUTHORITY, batch);
        contentResolver.notifyChange(
                LocMessDBContract.Keys.CONTENT_URI, // URI where data was modified
                null,                                   // no local observer
                false);                                 // IMPORTANT: do not sync do network
        return newKeysCount;
    }
}
