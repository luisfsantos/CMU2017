package ist.meic.cmu.locmess_client;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;

import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.sql.LocMessDBSQLiteHelper;

/**
 * Created by Catarina on 13/04/2017.
 */

public class LocMessProvider extends ContentProvider {

    private SQLiteDatabase database;
    private static final String AUTHORITY = "ist.meic.cmu.locmess_client.LocMessProvider";
    static final int KEYPAIRS = 1;
    static final int KEYPAIRS_ID = 2;
    static final int LOCATIONS = 3;
    static final int LOCATIONS_ID = 4;

    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.KeyPair.KEYPAIRS_PATH, KEYPAIRS);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.KeyPair.KEYPAIRS_ID_PATH, KEYPAIRS_ID);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.Location.LOCATIONS_PATH, LOCATIONS);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.Location.LOCATIONS_ID_PATH, LOCATIONS_ID);
    }

    private static HashMap sKeypairsProjectionMap;
    private static HashMap sLocationsProjectionMap;

    {
        sKeypairsProjectionMap = new HashMap();
        for(int i=0; i < LocMessDBContract.KeyPair.DEFAULT_PROJECTION.length; i++) {
            sKeypairsProjectionMap.put(
                    LocMessDBContract.KeyPair.DEFAULT_PROJECTION[i],
                    LocMessDBContract.KeyPair.DEFAULT_PROJECTION[i]);
        }
        sLocationsProjectionMap = new HashMap();
        for(int i=0; i < LocMessDBContract.Location.DEFAULT_PROJECTION.length; i++) {
            sLocationsProjectionMap.put(
                    LocMessDBContract.Location.DEFAULT_PROJECTION[i],
                    LocMessDBContract.Location.DEFAULT_PROJECTION[i]);
        }
    }

    @Override
    public boolean onCreate() {
        LocMessDBSQLiteHelper dbHelper = new LocMessDBSQLiteHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)){
            case KEYPAIRS:
                qb.setTables(LocMessDBContract.KeyPair.TABLE_NAME);
                qb.setProjectionMap(sKeypairsProjectionMap);
                break;
            case KEYPAIRS_ID:
                qb.setTables(LocMessDBContract.KeyPair.TABLE_NAME);
                qb.setProjectionMap(sKeypairsProjectionMap);
                qb.appendWhere(LocMessDBContract.KeyPair._ID +
                        " = " + uri.getPathSegments().get(1)
                );
                break;
            case LOCATIONS:
                qb.setTables(LocMessDBContract.Location.TABLE_NAME);
                qb.setProjectionMap(sLocationsProjectionMap);
                break;
            case LOCATIONS_ID:
                qb.setTables(LocMessDBContract.Location.TABLE_NAME);
                qb.setProjectionMap(sLocationsProjectionMap);
                qb.appendWhere(LocMessDBContract.Location._ID +
                        " = " + uri.getPathSegments().get(1)
                );
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Cursor cursor = qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        Uri rowUri = Uri.EMPTY;
        long rowId;
        switch (sUriMatcher.match(uri)) {
            case KEYPAIRS:
                rowId = database.insert(LocMessDBContract.KeyPair.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(LocMessDBContract.KeyPair.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                }
                break;
            case LOCATIONS:
                rowId = database.insert(LocMessDBContract.Location.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(LocMessDBContract.Location.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                }
                break;
        }
        return rowUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        String finalSelection;
        switch (sUriMatcher.match(uri)) {
            case KEYPAIRS:
                count = database.delete(LocMessDBContract.KeyPair.TABLE_NAME, selection, selectionArgs);
                break;
            case KEYPAIRS_ID:
                finalSelection = LocMessDBContract.KeyPair._ID + " = " + uri.getPathSegments().get(1);
                if (selection != null) {
                    finalSelection += " AND " + selection;
                }
                count = database.delete(LocMessDBContract.KeyPair.TABLE_NAME, finalSelection, selectionArgs);
                break;
            case LOCATIONS:
                count = database.delete(LocMessDBContract.Location.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATIONS_ID:
                finalSelection = LocMessDBContract.Location._ID + " = " + uri.getPathSegments().get(1);
                if (selection != null) {
                    finalSelection += " AND " + selection;
                }
                count = database.delete(LocMessDBContract.Location.TABLE_NAME, finalSelection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case KEYPAIRS:
                return LocMessDBContract.KeyPair.KEYPAIRS_TYPE;
            case KEYPAIRS_ID:
                return LocMessDBContract.KeyPair.KEYPAIRS_ID_TYPE;
            case LOCATIONS:
                return LocMessDBContract.Location.LOCATIONS_TYPE;
            case LOCATIONS_ID:
                return LocMessDBContract.Location.LOCATIONS_ID_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
