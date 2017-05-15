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

import ist.meic.cmu.locmess_client.data.Location;
import ist.meic.cmu.locmess_client.network.WebRequestResult;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.network.json.deserializers.LocationDeserializer;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.DateUtils;

/**
 * Created by Catarina on 04/05/2017.
 */

public class MergeLocation {
    private static final String TAG = "MergeLocation";
    private MergeLocation() {}

    public static int mergeAll(ContentResolver contentResolver, JsonArray locations, @Nullable SyncResult syncResult) throws RemoteException, OperationApplicationException {
        Log.i(TAG, "Parsing json into Location map");
        SparseArray<Location> remoteLocations =
                new LocationDeserializer().parseAll(locations);
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
            if (syncResult != null) {
                syncResult.stats.numEntries++;
            }
            serverId = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_SERVER_ID));
            dbId = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.Location._ID));
            Location match = remoteLocations.get(serverId);
            if (match != null) {
                // entry exists. remove from remote locations map to prevent insert later
                remoteLocations.remove(serverId);
                // we are not updating anything in the local entry
            } else {
                // entry doesn't exist. remove it from the database
                Uri deleteUri = ContentUris.withAppendedId(LocMessDBContract.Location.CONTENT_URI, dbId);
                Log.i(TAG, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                if (syncResult != null) {
                    syncResult.stats.numDeletes++;
                }
            }
        }
        c.close();

        // add new items
        int newLocationCount = remoteLocations.size();
        for (int i = 0; i < remoteLocations.size(); i++) {
            Location l = remoteLocations.valueAt(i);
            Log.i(TAG, "Scheduling insert: server_id=" + l.getId());
            batch.add(ContentProviderOperation.newInsert(LocMessDBContract.Location.CONTENT_URI)
                    .withValue(LocMessDBContract.Location.COLUMN_NAME, l.getName())
                    .withValue(LocMessDBContract.Location.COLUMN_AUTHOR, l.getAuthor())
                    .withValue(LocMessDBContract.Location.COLUMN_DATE_CREATED,
                            DateUtils.formatDateTimeLocaleToDb(l.getCreationDate()))
                    .withValue(LocMessDBContract.Location.COLUMN_COORDINATES, l.getCoordinatesDbFormat())
                    .withValue(LocMessDBContract.Location.COLUMN_SERVER_ID, l.getId())
                    .build());
            if (syncResult != null) {
                syncResult.stats.numInserts++;
            }
        }

        Log.i(TAG, "Merge solution ready. Applying batch update");
        contentResolver.applyBatch(LocMessDBContract.AUTHORITY, batch);
        contentResolver.notifyChange(
                LocMessDBContract.Location.CONTENT_URI, // URI where data was modified
                null,                                   // no local observer
                false);                                 // IMPORTANT: do not sync do network
        return newLocationCount;
    }

    public static void fillInServerId(ContentResolver contentResolver, Uri databaseEntryUri, String result, @Nullable SyncResult syncResult) {
        @WebRequestResult.ReturnedObject String label = WebRequestResult.LOCATION;
        JsonObjectAPI jresult = new Gson().fromJson(result, JsonObjectAPI.class);
        JsonObject jlocation = jresult.getData().getAsJsonObject(label);
        Location location = new LocationDeserializer().parse(jlocation);
        int serverId = location.getId();

        ContentValues values = new ContentValues();
        values.put(LocMessDBContract.Location.COLUMN_SERVER_ID, serverId);
        contentResolver.update(databaseEntryUri, values, null, null);
        if (syncResult != null) {
            syncResult.stats.numUpdates++;
        }

        Log.i(TAG, "Filled server_id=" + serverId + " of entry " + databaseEntryUri);
    }

}

