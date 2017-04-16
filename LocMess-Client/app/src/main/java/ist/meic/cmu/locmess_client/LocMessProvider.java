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
    static final int POSTED_MESSAGES = 5;
    static final int POSTED_MESSAGES_ID = 6;
    static final int OPENED_MESSAGES = 7;
    static final int OPENED_MESSAGES_ID = 8;

    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.KeyPair.KEYPAIRS_PATH, KEYPAIRS);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.KeyPair.KEYPAIRS_ID_PATH, KEYPAIRS_ID);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.Location.LOCATIONS_PATH, LOCATIONS);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.Location.LOCATIONS_ID_PATH, LOCATIONS_ID);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.PostedMessages.POSTED_MESSAGES_PATH, POSTED_MESSAGES);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.PostedMessages.POSTED_MESSAGES_ID_PATH, POSTED_MESSAGES_ID);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.OpenedMessages.OPENED_MESSAGES_PATH, OPENED_MESSAGES);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.OpenedMessages.OPENED_MESSAGES_ID_PATH, OPENED_MESSAGES_ID);
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
                break;
            case KEYPAIRS_ID:
                qb.setTables(LocMessDBContract.KeyPair.TABLE_NAME);
                qb.appendWhere(LocMessDBContract.KeyPair._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.KeyPair.ID_PATH_SEGMENT_INDEX)
                );
                break;
            case LOCATIONS:
                qb.setTables(LocMessDBContract.Location.TABLE_NAME);
                break;
            case LOCATIONS_ID:
                qb.setTables(LocMessDBContract.Location.TABLE_NAME);
                qb.appendWhere(LocMessDBContract.Location._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.Location.ID_PATH_SEGMENT_INDEX)
                );
                break;
            case POSTED_MESSAGES:
                qb.setTables(LocMessDBContract.PostedMessages.TABLE_NAME);
                break;
            case POSTED_MESSAGES_ID:
                qb.setTables(LocMessDBContract.PostedMessages.TABLE_NAME);
                qb.appendWhere(LocMessDBContract.PostedMessages._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.PostedMessages.ID_PATH_SEGMENT_INDEX)
                );
                break;
            case OPENED_MESSAGES:
                qb.setTables(LocMessDBContract.OpenedMessages.TABLE_NAME);
                break;
            case OPENED_MESSAGES_ID:
                qb.setTables(LocMessDBContract.OpenedMessages.TABLE_NAME);
                qb.appendWhere(LocMessDBContract.OpenedMessages._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.OpenedMessages.ID_PATH_SEGMENT_INDEX)
                );
                break;
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
                }
                break;
            case LOCATIONS:
                rowId = database.insert(LocMessDBContract.Location.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(LocMessDBContract.Location.CONTENT_URI, rowId);
                }
                break;
            case POSTED_MESSAGES:
                rowId = database.insert(LocMessDBContract.PostedMessages.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(LocMessDBContract.PostedMessages.CONTENT_URI, rowId);
                }
                break;
            case OPENED_MESSAGES:
                rowId = database.insert(LocMessDBContract.OpenedMessages.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(LocMessDBContract.OpenedMessages.CONTENT_URI, rowId);
                }
                break;
        }
        getContext().getContentResolver().notifyChange(rowUri, null);
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
                finalSelection = LocMessDBContract.KeyPair._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.KeyPair.ID_PATH_SEGMENT_INDEX);
                if (selection != null) {
                    finalSelection += " AND " + selection;
                }
                count = database.delete(LocMessDBContract.KeyPair.TABLE_NAME, finalSelection, selectionArgs);
                break;
            case LOCATIONS:
                count = database.delete(LocMessDBContract.Location.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATIONS_ID:
                finalSelection = LocMessDBContract.Location._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.Location.ID_PATH_SEGMENT_INDEX);
                if (selection != null) {
                    finalSelection += " AND " + selection;
                }
                count = database.delete(LocMessDBContract.Location.TABLE_NAME, finalSelection, selectionArgs);
                break;
            case POSTED_MESSAGES:
                count = database.delete(LocMessDBContract.PostedMessages.TABLE_NAME, selection, selectionArgs);
                break;
            case POSTED_MESSAGES_ID:
                finalSelection = LocMessDBContract.PostedMessages._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.PostedMessages.ID_PATH_SEGMENT_INDEX);
                if (selection != null) {
                    finalSelection += " AND " + selection;
                }
                count = database.delete(LocMessDBContract.PostedMessages.TABLE_NAME, finalSelection, selectionArgs);
                break;
            case OPENED_MESSAGES:
                count = database.delete(LocMessDBContract.OpenedMessages.TABLE_NAME, selection, selectionArgs);
                break;
            case OPENED_MESSAGES_ID:
                finalSelection = LocMessDBContract.OpenedMessages._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.OpenedMessages.ID_PATH_SEGMENT_INDEX);
                if (selection != null) {
                    finalSelection += " AND " + selection;
                }
                count = database.delete(LocMessDBContract.OpenedMessages.TABLE_NAME, finalSelection, selectionArgs);
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
            case POSTED_MESSAGES:
                return LocMessDBContract.PostedMessages.POSTED_MESSAGES_TYPE;
            case POSTED_MESSAGES_ID:
                return LocMessDBContract.PostedMessages.POSTED_MESSAGES_ID_TYPE;
            case OPENED_MESSAGES:
                return LocMessDBContract.OpenedMessages.OPENED_MESSAGES_TYPE;
            case OPENED_MESSAGES_ID:
                return LocMessDBContract.OpenedMessages.OPENED_MESSAGES_ID_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
