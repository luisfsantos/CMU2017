package ist.meic.cmu.locmess_client.network.p2p;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ist.meic.cmu.locmess_client.authentication.GenericAccountService;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.sql.LocMessDBSQLiteHelper;
import ist.meic.cmu.locmess_client.utils.CoordinatesUtils;

/**
 * Created by Catarina on 17/05/2017.
 *
 * A service that scans {@link ist.meic.cmu.locmess_client.sql.LocMessDBContract.AvailableP2pMessages}
 * table and deletes P2P messages that don't match the current location.
 */
public class P2pDatabaseCleanupService extends IntentService {

    private static final String TAG = "P2pDatabaseCleanupServ";
    public static final String INTENT_LATITUDE = "latitude";
    public static final String INTENT_LONGITUDE = "longitude";
    public static final String INTENT_SSIDS = "ssids";

    public P2pDatabaseCleanupService() {
        super(null);
    }
    public P2pDatabaseCleanupService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent == null) {
            Log.i(TAG, "Received null intent.");
            return;
        }
        Bundle bundle = intent.getExtras();
        double latitude = bundle.getDouble(INTENT_LATITUDE);
        double longitude = bundle.getDouble(INTENT_LONGITUDE);
        List<String> ssids = bundle.getStringArrayList(INTENT_SSIDS);
        if (latitude == 0.0 || longitude == 0.0 || ssids == null) {
            Log.e(TAG, "Caller did not provide all needed values");
            return;
        }

        Log.i(TAG, "Cleaning up AvailableP2pMessages");
        LocMessDBSQLiteHelper helper = new LocMessDBSQLiteHelper(getBaseContext());
        SQLiteDatabase database = helper.getReadableDatabase();
        String selection =
                LocMessDBContract.Location.TABLE_NAME + "." + LocMessDBContract.Location.COLUMN_SERVER_ID +
                        " = " + LocMessDBContract.AvailableP2pMessages.TABLE_NAME + "." +
                        LocMessDBContract.AvailableP2pMessages.COLUMN_LOCATION_SERVER_ID +
                        " AND " + LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " +
                        GenericAccountService.getActiveAccountHash(getBaseContext());
        Cursor cursor = database.query(false,
                LocMessDBContract.Location.TABLE_NAME + ", " + LocMessDBContract.AvailableP2pMessages.TABLE_NAME,
                new String[] { "DISTINCT " + LocMessDBContract.Location.TABLE_NAME + "." +
                        LocMessDBContract.Location.COLUMN_SERVER_ID,
                        LocMessDBContract.Location.COLUMN_COORDINATES },
                selection, null, LocMessDBContract.Location.TABLE_NAME + "." + LocMessDBContract.Location.COLUMN_SERVER_ID, null, null, null
        );


        List<String> locationIDs = new ArrayList<>();
        while (cursor.moveToNext()) {
            ContentValues values = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, values);
            CoordinatesUtils.Coordinates msgCoordinates =
                    new CoordinatesUtils(getBaseContext(), values.getAsString(LocMessDBContract.Location.COLUMN_COORDINATES)).parse();
            if (!locationsMatch(latitude, longitude, ssids, msgCoordinates)) {
                locationIDs.add(values.getAsString(LocMessDBContract.Location.COLUMN_SERVER_ID));
            }
        }

        String selection2 = LocMessDBContract.AvailableP2pMessages.COLUMN_LOCATION_SERVER_ID +
                " IN (" + makePlaceholders(locationIDs.size()) + ")";
        String[] selectionArgs = locationIDs.toArray(new String[locationIDs.size()]);
        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(LocMessDBContract.AvailableP2pMessages.CONTENT_URI,
                selection2, selectionArgs);
    }

    private boolean locationsMatch(double latitude, double longitude, List<String> ssids, CoordinatesUtils.Coordinates msgCoordinates) {
        if (msgCoordinates instanceof CoordinatesUtils.WifiCoordinates) {
            List<String> ssidIntersection = new ArrayList<>(((CoordinatesUtils.WifiCoordinates) msgCoordinates).ssids);
            ssidIntersection.retainAll(ssids);
            return !ssidIntersection.isEmpty();
        } else if (msgCoordinates instanceof CoordinatesUtils.GpsCoordinates) {
            Location current = new Location("");
            current.setLatitude(latitude);
            current.setLongitude(longitude);
            Location message = new Location("");
            message.setLatitude(((CoordinatesUtils.GpsCoordinates) msgCoordinates).latitude);
            message.setLongitude(((CoordinatesUtils.GpsCoordinates) msgCoordinates).longitude);

            double distance = current.distanceTo(message);
            return distance <= ((CoordinatesUtils.GpsCoordinates) msgCoordinates).radius;
        }
        return false;
    }

    private String makePlaceholders(int length) {
        StringBuilder builder = new StringBuilder();
        builder.append("?");
        for (int i = 1; i < length; i++) {
            builder.append(", ?");
        }
        return builder.toString();
    }
}
