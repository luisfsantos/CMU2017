package ist.meic.cmu.locmess_client.sql;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Catarina on 12/04/2017.
 */

public class LocMessDBContract {
    private static final String AUTHORITY = "ist.meic.cmu.locmess_client.LocMessProvider";
    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private LocMessDBContract(){

    }
    public static class KeyPair implements BaseColumns {
        public static final String KEYPAIRS_PATH = "keypairs";
        public static final String KEYPAIRS_ID_PATH = "keypairs/#";
        public static final String KEYPAIRS_TYPE = "vnd.android.cursor.dir/vnd.locmess.provider.keypairs";
        public static final String KEYPAIRS_ID_TYPE = "vnd.android.cursor.item/vnd.locmess.provider.keypairs";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+KEYPAIRS_PATH);

        public static final String TABLE_NAME = "keypair";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_VALUE = "value";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                LocMessDBContract.KeyPair._ID,
                LocMessDBContract.KeyPair.COLUMN_KEY,
                LocMessDBContract.KeyPair.COLUMN_VALUE
        };

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_KEY + " TEXT, " +
                COLUMN_VALUE + " TEXT " + ")";
    }

    public static class Location implements BaseColumns {
        public static final String LOCATIONS_PATH = "locations";
        public static final String LOCATIONS_ID_PATH = "locations/#";
        public static final String LOCATIONS_TYPE = "vnd.android.cursor.dir/vnd.locmess.provider.locations";
        public static final String LOCATIONS_ID_TYPE = "vnd.android.cursor.item/vnd.locmess.provider.locations";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+LOCATIONS_PATH);

        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_DATE_CREATED = "date_created";
        public static final String COLUMN_COORDINATES = "coordinates";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_DATE_CREATED + " TEXT, " +
                COLUMN_COORDINATES + " TEXT " + ")";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                LocMessDBContract.Location._ID,
                LocMessDBContract.Location.COLUMN_NAME,
                LocMessDBContract.Location.COLUMN_AUTHOR,
                LocMessDBContract.Location.COLUMN_DATE_CREATED,
                LocMessDBContract.Location.COLUMN_COORDINATES
        };
    }

    public static class PostedMessages implements BaseColumns {
        public static final String POSTED_MESSAGES_PATH = "posted_messages";
        public static final String POSTED_MESSAGES_ID_PATH = "posted_messages/#";
        public static final String POSTED_MESSAGES_TYPE = "vnd.android.cursor.dir/vnd.locmess.provider.posted_messages";
        public static final String POSTED_MESSAGES_ID_TYPE = "vnd.android.cursor.item/vnd.locmess.provider.posted_messages";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+POSTED_MESSAGES_PATH);

        public static final String TABLE_NAME = "posted_messages";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_DATE_FROM = "date_from";
        public static final String COLUMN_DATE_TO = "date_to";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " + // FIXME: 14/04/2017 decide whether it'll be just the name of the location or a foreign key
                // FIXME: 14/04/2017 note that if it is a foreign key then there will be problems in retrieving info if location is deleted
                COLUMN_DATE_FROM + " TEXT, " +
                COLUMN_DATE_TO + " TEXT " + ")";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                LocMessDBContract.PostedMessages._ID,
                LocMessDBContract.PostedMessages.COLUMN_TITLE,
                LocMessDBContract.PostedMessages.COLUMN_CONTENT,
                LocMessDBContract.PostedMessages.COLUMN_LOCATION,
                LocMessDBContract.PostedMessages.COLUMN_DATE_FROM,
                LocMessDBContract.PostedMessages.COLUMN_DATE_TO
        };
    }

}
