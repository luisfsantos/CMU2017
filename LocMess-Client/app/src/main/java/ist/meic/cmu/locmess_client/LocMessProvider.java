package ist.meic.cmu.locmess_client;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

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
    static final int AVAILABLE_MESSAGES = 9;
    static final int AVAILABLE_MESSAGES_ID = 10;
    static final int KEYS = 11;
    static final int KEYS_ID = 12;
    static final int AVAILABLE_P2P_MESSAGES = 13;
    static final int AVAILABLE_P2P_MESSAGES_ID = 14;
    static final int AVAILABLE_WITH_P2P = 15;

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
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.AvailableMessages.AVAILABLE_MESSAGES_PATH, AVAILABLE_MESSAGES);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.AvailableMessages.AVAILABLE_MESSAGES_ID_PATH, AVAILABLE_MESSAGES_ID);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.Keys.KEYS_PATH, KEYS);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.Keys.KEYS_ID_PATH, KEYS_ID);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.AvailableP2pMessages.AVAILABLE_P2P_MESSAGES_PATH, AVAILABLE_P2P_MESSAGES);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.AvailableP2pMessages.AVAILABLE_P2P_MESSAGES_ID_PATH, AVAILABLE_P2P_MESSAGES_ID);
        sUriMatcher.addURI(AUTHORITY, LocMessDBContract.AvailableMessages.AVAILABLE_WITH_P2P_PATH, AVAILABLE_WITH_P2P);
    }

    @Override
    public boolean onCreate() {
        LocMessDBSQLiteHelper dbHelper = new LocMessDBSQLiteHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return true;
    }

    private int getUsernameHash() {
        SharedPreferences pref = getContext().getSharedPreferences(
                getContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String username = pref.getString(getContext().getString(R.string.pref_username), "username");
        return username.hashCode();
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)){
            case KEYPAIRS:
                qb.appendWhere(LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " + getUsernameHash());
                qb.setTables(LocMessDBContract.KeyPair.TABLE_NAME);
                break;
            case KEYPAIRS_ID:
                qb.appendWhere(LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " + getUsernameHash());
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
                qb.appendWhere(LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " + getUsernameHash());
                qb.setTables(LocMessDBContract.PostedMessages.TABLE_NAME);
                break;
            case POSTED_MESSAGES_ID:
                qb.appendWhere(LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " + getUsernameHash());
                qb.setTables(LocMessDBContract.PostedMessages.TABLE_NAME);
                qb.appendWhere(LocMessDBContract.PostedMessages._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.PostedMessages.ID_PATH_SEGMENT_INDEX)
                );
                break;
            case OPENED_MESSAGES:
                qb.appendWhere(LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " + getUsernameHash());
                qb.setTables(LocMessDBContract.OpenedMessages.TABLE_NAME);
                break;
            case OPENED_MESSAGES_ID:
                qb.appendWhere(LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " + getUsernameHash());
                qb.setTables(LocMessDBContract.OpenedMessages.TABLE_NAME);
                qb.appendWhere(LocMessDBContract.OpenedMessages._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.OpenedMessages.ID_PATH_SEGMENT_INDEX)
                );
                break;
            case AVAILABLE_MESSAGES:
                qb.appendWhere(LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " + getUsernameHash());
                qb.setTables(LocMessDBContract.AvailableMessages.TABLE_NAME);
                break;
            case AVAILABLE_MESSAGES_ID:
                qb.appendWhere(LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " + getUsernameHash());
                qb.setTables(LocMessDBContract.AvailableMessages.TABLE_NAME);
                qb.appendWhere(LocMessDBContract.AvailableMessages._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.AvailableMessages.ID_PATH_SEGMENT_INDEX)
                );
                break;
            case AVAILABLE_P2P_MESSAGES:
                qb.appendWhere(LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " + getUsernameHash());
                qb.setTables(LocMessDBContract.AvailableP2pMessages.TABLE_NAME);
                break;
            case AVAILABLE_P2P_MESSAGES_ID:
                qb.appendWhere(LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " + getUsernameHash());
                qb.setTables(LocMessDBContract.AvailableP2pMessages.TABLE_NAME);
                qb.appendWhere(LocMessDBContract.AvailableP2pMessages._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.AvailableP2pMessages.ID_PATH_SEGMENT_INDEX)
                );
                break;
            case KEYS:
                qb.setTables(LocMessDBContract.Keys.TABLE_NAME);
                break;
            case KEYS_ID:
                qb.setTables(LocMessDBContract.Keys.TABLE_NAME);
                qb.appendWhere(LocMessDBContract.Keys._ID + " = " +
                        uri.getPathSegments().get(LocMessDBContract.Keys.ID_PATH_SEGMENT_INDEX)
                );
                break;
            case AVAILABLE_WITH_P2P:
                qb.appendWhere(LocMessDBContract.COLUMN_ACCOUNT_HASH + " = " + getUsernameHash());
                qb.setTables(LocMessDBContract.AvailableMessages.VIEW_NAME);
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
                contentValues.put(LocMessDBContract.COLUMN_ACCOUNT_HASH, getUsernameHash());
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
                contentValues.put(LocMessDBContract.COLUMN_ACCOUNT_HASH, getUsernameHash());
                rowId = database.insert(LocMessDBContract.PostedMessages.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(LocMessDBContract.PostedMessages.CONTENT_URI, rowId);
                }
                break;
            case OPENED_MESSAGES:
                contentValues.put(LocMessDBContract.COLUMN_ACCOUNT_HASH, getUsernameHash());
                rowId = database.insert(LocMessDBContract.OpenedMessages.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(LocMessDBContract.OpenedMessages.CONTENT_URI, rowId);
                }
                break;
            case AVAILABLE_MESSAGES:
                contentValues.put(LocMessDBContract.COLUMN_ACCOUNT_HASH, getUsernameHash());
                rowId = database.insert(LocMessDBContract.AvailableMessages.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(LocMessDBContract.AvailableMessages.CONTENT_URI, rowId);
                }
                //notify observers of aggregate view
                getContext().getContentResolver().notifyChange(LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P, null);
                break;
            case AVAILABLE_P2P_MESSAGES:
                contentValues.put(LocMessDBContract.COLUMN_ACCOUNT_HASH, getUsernameHash());
                rowId = database.insert(LocMessDBContract.AvailableP2pMessages.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(LocMessDBContract.AvailableP2pMessages.CONTENT_URI, rowId);
                }
                //notify observers of aggregate view
                getContext().getContentResolver().notifyChange(LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P, null);
                break;
            case KEYS:
                rowId = database.insert(LocMessDBContract.Keys.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(LocMessDBContract.Keys.CONTENT_URI, rowId);
                }
                break;
        }
        getContext().getContentResolver().notifyChange(rowUri, null);
        return rowUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        StringBuilder finalSelection = new StringBuilder();
        switch (sUriMatcher.match(uri)) {
            case KEYPAIRS:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash());
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.delete(LocMessDBContract.KeyPair.TABLE_NAME, finalSelection.toString(), selectionArgs);
                break;
            case KEYPAIRS_ID:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash())
                        .append(" AND ")
                        .append(LocMessDBContract.KeyPair._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.KeyPair.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.delete(LocMessDBContract.KeyPair.TABLE_NAME, finalSelection.toString(), selectionArgs);
                break;
            case LOCATIONS:
                count = database.delete(LocMessDBContract.Location.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATIONS_ID:
                finalSelection
                        .append(LocMessDBContract.Location._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.Location.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.delete(LocMessDBContract.Location.TABLE_NAME, finalSelection.toString(), selectionArgs);
                break;
            case POSTED_MESSAGES:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash());
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.delete(LocMessDBContract.PostedMessages.TABLE_NAME, finalSelection.toString(), selectionArgs);
                break;
            case POSTED_MESSAGES_ID:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash())
                        .append(" AND ")
                        .append(LocMessDBContract.PostedMessages._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.PostedMessages.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.delete(LocMessDBContract.PostedMessages.TABLE_NAME, finalSelection.toString(), selectionArgs);
                break;
            case OPENED_MESSAGES:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash());
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.delete(LocMessDBContract.OpenedMessages.TABLE_NAME, finalSelection.toString(), selectionArgs);
                break;
            case OPENED_MESSAGES_ID:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash())
                        .append(" AND ")
                        .append(LocMessDBContract.OpenedMessages._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.OpenedMessages.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.delete(LocMessDBContract.OpenedMessages.TABLE_NAME, finalSelection.toString(), selectionArgs);
                break;
            case AVAILABLE_MESSAGES:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash());
                count = database.delete(LocMessDBContract.AvailableMessages.TABLE_NAME, finalSelection.toString(), selectionArgs);
                //notify observers of aggregate view
                getContext().getContentResolver().notifyChange(LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P, null);
                break;
            case AVAILABLE_MESSAGES_ID:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash())
                        .append(" AND ")
                        .append(LocMessDBContract.AvailableMessages._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.AvailableMessages.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.delete(LocMessDBContract.AvailableMessages.TABLE_NAME, finalSelection.toString(), selectionArgs);
                //notify observers of aggregate view
                getContext().getContentResolver().notifyChange(LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P, null);
                break;
            case AVAILABLE_P2P_MESSAGES:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash());
                count = database.delete(LocMessDBContract.AvailableP2pMessages.TABLE_NAME, finalSelection.toString(), selectionArgs);
                //notify observers of aggregate view
                getContext().getContentResolver().notifyChange(LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P, null);
                break;
            case AVAILABLE_P2P_MESSAGES_ID:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash())
                        .append(" AND ")
                        .append(LocMessDBContract.AvailableP2pMessages._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.AvailableP2pMessages.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.delete(LocMessDBContract.AvailableP2pMessages.TABLE_NAME, finalSelection.toString(), selectionArgs);
                //notify observers of aggregate view
                getContext().getContentResolver().notifyChange(LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P, null);
                break;
            case KEYS:
                count = database.delete(LocMessDBContract.Keys.TABLE_NAME, selection, selectionArgs);
                break;
            case KEYS_ID:
                finalSelection
                        .append(LocMessDBContract.Keys._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.Keys.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.delete(LocMessDBContract.Keys.TABLE_NAME, finalSelection.toString(), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int count;
        StringBuilder finalSelection = new StringBuilder();
        switch (sUriMatcher.match(uri)) {
            case KEYPAIRS:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash());
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.KeyPair.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                break;
            case KEYPAIRS_ID:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash())
                        .append(" AND ")
                        .append(LocMessDBContract.KeyPair._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.KeyPair.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.KeyPair.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                break;
            case LOCATIONS:
                count = database.update(LocMessDBContract.Location.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case LOCATIONS_ID:
                finalSelection
                        .append(LocMessDBContract.Location._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.Location.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.Location.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                break;
            case POSTED_MESSAGES:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash());
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.PostedMessages.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                break;
            case POSTED_MESSAGES_ID:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash())
                        .append(" AND ")
                        .append(LocMessDBContract.PostedMessages._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.PostedMessages.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.PostedMessages.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                break;
            case OPENED_MESSAGES:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash());
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.OpenedMessages.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                break;
            case OPENED_MESSAGES_ID:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash())
                        .append(" AND ")
                        .append(LocMessDBContract.OpenedMessages._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.OpenedMessages.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.OpenedMessages.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                break;
            case AVAILABLE_MESSAGES:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash());
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.AvailableMessages.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                //notify observers of aggregate view
                getContext().getContentResolver().notifyChange(LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P, null);
                break;
            case AVAILABLE_MESSAGES_ID:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash())
                        .append(" AND ")
                        .append(LocMessDBContract.AvailableMessages._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.AvailableMessages.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.AvailableMessages.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                //notify observers of aggregate view
                getContext().getContentResolver().notifyChange(LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P, null);
                break;
            case AVAILABLE_P2P_MESSAGES:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash());
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.AvailableP2pMessages.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                //notify observers of aggregate view
                getContext().getContentResolver().notifyChange(LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P, null);
                break;
            case AVAILABLE_P2P_MESSAGES_ID:
                finalSelection
                        .append(LocMessDBContract.COLUMN_ACCOUNT_HASH)
                        .append(" = ")
                        .append(getUsernameHash())
                        .append(" AND ")
                        .append(LocMessDBContract.AvailableP2pMessages._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.AvailableP2pMessages.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.AvailableP2pMessages.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                //notify observers of aggregate view
                getContext().getContentResolver().notifyChange(LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P, null);
                break;
            case KEYS:
                count = database.update(LocMessDBContract.Keys.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case KEYS_ID:
                finalSelection
                        .append(LocMessDBContract.Keys._ID)
                        .append(" = ")
                        .append(uri.getPathSegments().get(LocMessDBContract.Keys.ID_PATH_SEGMENT_INDEX));
                if (selection != null) {
                    finalSelection
                            .append(" AND ")
                            .append(selection);
                }
                count = database.update(LocMessDBContract.Keys.TABLE_NAME, contentValues, finalSelection.toString(), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
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
            case AVAILABLE_MESSAGES:
                return LocMessDBContract.AvailableMessages.AVAILABLE_MESSAGES_TYPE;
            case AVAILABLE_MESSAGES_ID:
                return LocMessDBContract.AvailableMessages.AVAILABLE_MESSAGES_ID_TYPE;
            case AVAILABLE_P2P_MESSAGES:
                return LocMessDBContract.AvailableP2pMessages.AVAILABLE_P2P_MESSAGES_TYPE;
            case AVAILABLE_P2P_MESSAGES_ID:
                return LocMessDBContract.AvailableP2pMessages.AVAILABLE_P2P_MESSAGES_ID_TYPE;
            case KEYS:
                return LocMessDBContract.Keys.KEYS_TYPE;
            case KEYS_ID:
                return LocMessDBContract.Keys.KEYS_ID_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
