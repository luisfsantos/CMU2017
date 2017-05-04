package ist.meic.cmu.locmess_client.network.sync.merge;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.HashMap;

import ist.meic.cmu.locmess_client.network.json.serializers.LocationSerializer;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.DateUtils;

/**
 * Created by Catarina on 04/05/2017.
 */

public class MergeLocation {
    private static final String TAG = "MergeLocation";
    private MergeLocation() {}

    public static void mergeAll(ContentResolver contentResolver, JsonArray locations, SyncResult syncResult) throws RemoteException, OperationApplicationException {
        Log.i(TAG, "Parsing json into Location map");
        HashMap<Integer, LocationSerializer.Location> remoteLocations =
                new LocationSerializer().parseAll(locations);
        Log.i(TAG, "Parsing complete. Found " + remoteLocations.size() + " remote entries");

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        Log.i(TAG, "Fetching local entries for merge");
        Uri uri = LocMessDBContract.Location.CONTENT_URI;
        Cursor c = contentResolver.query(uri,
                LocMessDBContract.Location.DEFAULT_PROJECTION,
                null, null, null);
        assert c != null;
        Log.i(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");

        // find stale data
        int serverId;
        int dbId;
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;
            serverId = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.COLUMN_SERVER_ID));
            dbId = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.Location._ID));
            LocationSerializer.Location match = remoteLocations.get(serverId);
            if (match != null) {
                // entry exists. remove from remote locations map to prevent insert later
                remoteLocations.remove(serverId);
                // we are not updating anything in the local entry
            } else {
                // entry doesn't exist. remove it from the database
                Uri deleteUri = ContentUris.withAppendedId(LocMessDBContract.Location.CONTENT_URI, dbId);
                Log.i(TAG, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // add new items
        for (LocationSerializer.Location l : remoteLocations.values()) {
            Log.i(TAG, "Scheduling insert: server_id=" + l.getId());
            batch.add(ContentProviderOperation.newInsert(LocMessDBContract.Location.CONTENT_URI)
                    .withValue(LocMessDBContract.Location.COLUMN_NAME, l.getName())
                    .withValue(LocMessDBContract.Location.COLUMN_AUTHOR, l.getAuthor())
                    .withValue(LocMessDBContract.Location.COLUMN_DATE_CREATED,
                            DateUtils.formatDateTimeLocaleToDb(l.getCreationDate()))
                    .withValue(LocMessDBContract.Location.COLUMN_COORDINATES, l.getCoordinatesDbFormat())
                    .withValue(LocMessDBContract.COLUMN_SERVER_ID, l.getId())
                    .build());
            syncResult.stats.numInserts++;
        }

        Log.i(TAG, "Merge solution ready. Applying batch update");
        contentResolver.applyBatch(LocMessDBContract.AUTHORITY, batch);
        contentResolver.notifyChange(
                LocMessDBContract.Location.CONTENT_URI, // URI where data was modified
                null,                                   // no local observer
                false);                                 // IMPORTANT: do not sync do network
    }
}

