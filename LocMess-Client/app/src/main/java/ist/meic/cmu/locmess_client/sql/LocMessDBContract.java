package ist.meic.cmu.locmess_client.sql;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Catarina on 12/04/2017.
 */

public class LocMessDBContract {
    private static final String AUTHORITY = "ist.meic.cmu.locmess_client.LocMessProvider";

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
}
