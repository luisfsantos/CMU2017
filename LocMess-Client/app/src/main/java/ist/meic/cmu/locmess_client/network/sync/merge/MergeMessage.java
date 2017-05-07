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

import ist.meic.cmu.locmess_client.network.WebRequestResult;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.network.json.deserializers.KeypairDeserializer;
import ist.meic.cmu.locmess_client.network.json.deserializers.MessageDeserializer;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.DateUtils;

/**
 * Created by Catarina on 05/05/2017.
 */

public class MergeMessage {
    private static final String TAG = "MergeKeypair";
    private MergeMessage() {}

    public static void mergeAllAvailable(ContentResolver contentResolver, JsonArray messages, @Nullable SyncResult syncResult) throws RemoteException, OperationApplicationException {
        Log.i(TAG, "Parsing json into Message map");
        SparseArray<MessageDeserializer.Message> remoteMessages =
                new MessageDeserializer().parseAll(messages);
        Log.i(TAG, "Parsing complete. Found " + remoteMessages.size() + " remote entries");

        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        Log.i(TAG, "Fetching local entries for merge");
        Uri uri = LocMessDBContract.AvailableMessages.CONTENT_URI;
        Cursor c = contentResolver.query(uri,
                LocMessDBContract.AvailableMessages.DEFAULT_PROJECTION,
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
            serverId = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.COLUMN_SERVER_ID));
            dbId = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.AvailableMessages._ID));
            MessageDeserializer.Message match = remoteMessages.get(serverId);
            if (match != null) {
                // entry exists. remove from remote messages map to prevent insert later
                remoteMessages.remove(serverId);
                // we are not updating anything in the local entry
            } else {
                // entry doesn't exist. remove it from the database
                Uri deleteUri = ContentUris.withAppendedId(LocMessDBContract.AvailableMessages.CONTENT_URI, dbId);
                Log.i(TAG, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                if (syncResult != null) {
                    syncResult.stats.numDeletes++;
                }
            }
        }
        c.close();

        // add new items
        for (int i = 0; i < remoteMessages.size(); i++) {
            MessageDeserializer.Message m = remoteMessages.valueAt(i);
            Log.i(TAG, "Scheduling insert: server_id=" + m.getId());
            Log.d(TAG, String.format("title=%s text=%s author=%s date=%s location=%s read=%d",
                    m.getTitle(), m.getText(), m.getAuthor(),
                    DateUtils.formatDateTimeLocaleToDb(m.getFromDate()),
                    m.getLocation().getName(),
                    LocMessDBContract.AvailableMessages.MESSAGE_NOT_READ));
            batch.add(ContentProviderOperation.newInsert(LocMessDBContract.AvailableMessages.CONTENT_URI)
                    .withValue(LocMessDBContract.AvailableMessages.COLUMN_TITLE, m.getTitle())
                    .withValue(LocMessDBContract.AvailableMessages.COLUMN_CONTENT, m.getText())
                    .withValue(LocMessDBContract.AvailableMessages.COLUMN_AUTHOR, m.getAuthor())
                    .withValue(LocMessDBContract.AvailableMessages.COLUMN_DATE_POSTED, DateUtils.formatDateTimeLocaleToDb(m.getFromDate()))
                    .withValue(LocMessDBContract.AvailableMessages.COLUMN_LOCATION, m.getLocation().getName())
                    .withValue(LocMessDBContract.AvailableMessages.COLUMN_READ, LocMessDBContract.AvailableMessages.MESSAGE_NOT_READ)
                    .withValue(LocMessDBContract.COLUMN_SERVER_ID, m.getId())
                    .build());
            if (syncResult != null) {
                syncResult.stats.numInserts++;
            }
        }

        Log.i(TAG, "Merge solution ready. Applying batch update");
        contentResolver.applyBatch(LocMessDBContract.AUTHORITY, batch);
        contentResolver.notifyChange(
                LocMessDBContract.AvailableMessages.CONTENT_URI, // URI where data was modified
                null,                                   // no local observer
                false);                                 // IMPORTANT: do not sync do network
    }

    public static void fillInServerId(ContentResolver contentResolver, Uri databaseEntryUri, String result, @Nullable SyncResult syncResult) {
        @WebRequestResult.ReturnedObject String label = WebRequestResult.MESSAGE;
        JsonObjectAPI jresult = new Gson().fromJson(result, JsonObjectAPI.class);
        JsonObject jmessage = jresult.getData().getAsJsonObject(label);
        MessageDeserializer.Message me = new MessageDeserializer().parse(jmessage);
        int serverId = me.getId();

        ContentValues values = new ContentValues();
        values.put(LocMessDBContract.COLUMN_SERVER_ID, serverId);
        contentResolver.update(databaseEntryUri, values, null, null);
        if (syncResult != null) {
            syncResult.stats.numUpdates++;
        }

        Log.i(TAG, "Filled server_id=" + serverId + " of entry " + databaseEntryUri);
    }
}
