package ist.meic.cmu.locmess_client.sql;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Catarina on 12/04/2017.
 */

public class LocMessDBContract {
    public static final String AUTHORITY = "ist.meic.cmu.locmess_client.LocMessProvider";
    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String COLUMN_ACCOUNT_HASH = "account_hash";
//    public static final String COLUMN_SERVER_ID = "server_id";
    private LocMessDBContract(){

    }
    public static class KeyPair implements BaseColumns {
        public static final String KEYPAIRS_PATH = "keypairs";
        public static final String KEYPAIRS_ID_PATH = "keypairs/#";
        public static final String KEYPAIRS_TYPE = "vnd.android.cursor.dir/vnd.locmess.provider.keypairs";
        public static final String KEYPAIRS_ID_TYPE = "vnd.android.cursor.item/vnd.locmess.provider.keypairs";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+KEYPAIRS_PATH);
        public static final int ID_PATH_SEGMENT_INDEX = 1;

        public static final String TABLE_NAME = "keypair";
        public static final String COLUMN_KEY = TABLE_NAME + "_key";
        public static final String COLUMN_VALUE = TABLE_NAME + "_value";
        public static final String COLUMN_SERVER_ID = TABLE_NAME + "_server_id";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                LocMessDBContract.KeyPair._ID,
                LocMessDBContract.KeyPair.COLUMN_KEY,
                LocMessDBContract.KeyPair.COLUMN_VALUE,
                LocMessDBContract.KeyPair.COLUMN_SERVER_ID
        };

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_KEY + " TEXT, " +
                COLUMN_VALUE + " TEXT, " +
                COLUMN_SERVER_ID + " INTEGER, " +
                COLUMN_ACCOUNT_HASH + " INTEGER " + ")";
    }

    public static class Location implements BaseColumns {
        public static final String LOCATIONS_PATH = "locations";
        public static final String LOCATIONS_ID_PATH = "locations/#";
        public static final String LOCATIONS_TYPE = "vnd.android.cursor.dir/vnd.locmess.provider.locations";
        public static final String LOCATIONS_ID_TYPE = "vnd.android.cursor.item/vnd.locmess.provider.locations";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+LOCATIONS_PATH);
        public static final int ID_PATH_SEGMENT_INDEX = 1;

        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME = TABLE_NAME + "_name";
        public static final String COLUMN_AUTHOR = TABLE_NAME  + "_author";
        public static final String COLUMN_DATE_CREATED = TABLE_NAME + "_date_created";
        public static final String COLUMN_COORDINATES = TABLE_NAME + "_coordinates";
        public static final String COLUMN_SERVER_ID = TABLE_NAME + "_server_id";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_DATE_CREATED + " TEXT, " +
                COLUMN_COORDINATES + " TEXT, " +
                COLUMN_SERVER_ID + " INTEGER UNIQUE " + ")";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                LocMessDBContract.Location._ID,
                LocMessDBContract.Location.COLUMN_NAME,
                LocMessDBContract.Location.COLUMN_AUTHOR,
                LocMessDBContract.Location.COLUMN_DATE_CREATED,
                LocMessDBContract.Location.COLUMN_COORDINATES,
                LocMessDBContract.Location.COLUMN_SERVER_ID
        };
    }

    public static class PostedMessages implements BaseColumns {
        public static final String POSTED_MESSAGES_PATH = "messages/posted";
        public static final String POSTED_MESSAGES_ID_PATH = "messages/posted/#";
        public static final String POSTED_MESSAGES_TYPE = "vnd.android.cursor.dir/vnd.locmess.provider.messages.posted";
        public static final String POSTED_MESSAGES_ID_TYPE = "vnd.android.cursor.item/vnd.locmess.provider.messages.posted";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+POSTED_MESSAGES_PATH);
        public static final int ID_PATH_SEGMENT_INDEX = 2;

        public static final String TABLE_NAME = "posted";
        public static final String COLUMN_TITLE = TABLE_NAME + "_title";
        public static final String COLUMN_CONTENT = TABLE_NAME + "_content";
        public static final String COLUMN_LOCATION = TABLE_NAME + "_location";
        public static final String COLUMN_DATE_FROM = TABLE_NAME + "_date_from";
        public static final String COLUMN_DATE_TO = TABLE_NAME + "_date_to";
        public static final String COLUMN_SERVER_ID = TABLE_NAME + "_server_id";
        public static final String COLUMN_LOCATION_SERVER_ID = TABLE_NAME + "_location_server_id";
        public static final String COLUMN_POLICY = TABLE_NAME + "_policy";
        public static final String COLUMN_WHITELIST = TABLE_NAME + "_whitelist";
        public static final String COLUMN_BLACKLIST = TABLE_NAME + "_blacklist";

        public static final int POLICY_P2P = 1;
        public static final int POLICY_CENTRALIZED = 2;

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_DATE_FROM + " TEXT, " +
                COLUMN_DATE_TO + " TEXT, " +
                COLUMN_SERVER_ID + " INTEGER, " +
                COLUMN_ACCOUNT_HASH + " INTEGER, " +
                COLUMN_LOCATION_SERVER_ID + " INTEGER, " +
                COLUMN_POLICY + " INTEGER NOT NULL CHECK (" +
                    COLUMN_POLICY + " IN (" + POLICY_P2P + "," + POLICY_CENTRALIZED + ")), " +
                COLUMN_WHITELIST + " TEXT, " +
                COLUMN_BLACKLIST + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_LOCATION_SERVER_ID + ") REFERENCES "
                    + Location.TABLE_NAME + "(" + Location.COLUMN_SERVER_ID + ") ON DELETE CASCADE" + ")";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                LocMessDBContract.PostedMessages._ID,
                LocMessDBContract.PostedMessages.COLUMN_TITLE,
                LocMessDBContract.PostedMessages.COLUMN_CONTENT,
                LocMessDBContract.PostedMessages.COLUMN_LOCATION,
                LocMessDBContract.PostedMessages.COLUMN_DATE_FROM,
                LocMessDBContract.PostedMessages.COLUMN_DATE_TO,
                LocMessDBContract.PostedMessages.COLUMN_SERVER_ID
        };
    }

    public static class OpenedMessages implements BaseColumns {
        public static final String OPENED_MESSAGES_PATH = "messages/opened";
        public static final String OPENED_MESSAGES_ID_PATH = "messages/opened/#";
        public static final String OPENED_MESSAGES_TYPE = "vnd.android.cursor.dir/vnd.locmess.provider.messages.opened";
        public static final String OPENED_MESSAGES_ID_TYPE = "vnd.android.cursor.item/vnd.locmess.provider.messages.opened";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+OPENED_MESSAGES_PATH);
        public static final int ID_PATH_SEGMENT_INDEX = 2;

        public static final String TABLE_NAME = "opened";
        public static final String COLUMN_TITLE = TABLE_NAME + "_title";
        public static final String COLUMN_CONTENT = TABLE_NAME + "_content";
        public static final String COLUMN_LOCATION = TABLE_NAME + "_location";
        public static final String COLUMN_AUTHOR = TABLE_NAME + "_author";
        public static final String COLUMN_DATE_POSTED = TABLE_NAME + "_date_posted";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_DATE_POSTED + " TEXT, " +
                COLUMN_ACCOUNT_HASH + " INTEGER " + ")";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                LocMessDBContract.OpenedMessages._ID,
                LocMessDBContract.OpenedMessages.COLUMN_TITLE,
                LocMessDBContract.OpenedMessages.COLUMN_CONTENT,
                LocMessDBContract.OpenedMessages.COLUMN_LOCATION,
                LocMessDBContract.OpenedMessages.COLUMN_AUTHOR,
                LocMessDBContract.OpenedMessages.COLUMN_DATE_POSTED
        };
    }

    public static class AvailableMessages implements BaseColumns {
        public static final String AVAILABLE_MESSAGES_PATH = "messages/available";
        public static final String AVAILABLE_MESSAGES_ID_PATH = "messages/available/#";
        public static final String AVAILABLE_MESSAGES_TYPE = "vnd.android.cursor.dir/vnd.locmess.provider.messages.available";
        public static final String AVAILABLE_MESSAGES_ID_TYPE = "vnd.android.cursor.item/vnd.locmess.provider.messages.available";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+AVAILABLE_MESSAGES_PATH);
        public static final int ID_PATH_SEGMENT_INDEX = 2;

        public static final String TABLE_NAME = "available";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_DATE_POSTED = "date_posted";
        public static final String COLUMN_READ = "read";
        public static final String COLUMN_SERVER_ID = "server_id";
        public static final int MESSAGE_READ = 1;

        public static final int MESSAGE_NOT_READ = 0;
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_DATE_POSTED + " TEXT, " +
                COLUMN_READ + " BOOLEAN NOT NULL CHECK (" +
                    COLUMN_READ + " IN (" + MESSAGE_READ + "," + MESSAGE_NOT_READ + ")), " +
                COLUMN_SERVER_ID + " INTEGER, " +
                COLUMN_ACCOUNT_HASH + " INTEGER " + ")";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                LocMessDBContract.AvailableMessages._ID,
                LocMessDBContract.AvailableMessages.COLUMN_TITLE,
                LocMessDBContract.AvailableMessages.COLUMN_CONTENT,
                LocMessDBContract.AvailableMessages.COLUMN_LOCATION,
                LocMessDBContract.AvailableMessages.COLUMN_AUTHOR,
                LocMessDBContract.AvailableMessages.COLUMN_DATE_POSTED,
                LocMessDBContract.AvailableMessages.COLUMN_READ,
                LocMessDBContract.AvailableMessages.COLUMN_SERVER_ID
        };
        /* for the view */
        public static final String AVAILABLE_WITH_P2P_PATH = "messages/join_available";
        public static final Uri CONTENT_URI_WITH_P2P = Uri.parse("content://" + AUTHORITY + "/" + AVAILABLE_WITH_P2P_PATH);
        private static String select1 = "SELECT " +
                AvailableMessages._ID +
                ", " + AvailableMessages.COLUMN_TITLE +
                ", " + AvailableMessages.COLUMN_CONTENT +
                ", " + AvailableMessages.COLUMN_AUTHOR +
                ", " + AvailableMessages.COLUMN_LOCATION +
                ", " + AvailableMessages.COLUMN_DATE_POSTED +
                ", " + AvailableMessages.COLUMN_READ +
                ", " + AvailableMessages.COLUMN_SERVER_ID +
                ", null AS " + LocMessDBContract.AvailableP2pMessages.COLUMN_P2P_ID +
                ", " + COLUMN_ACCOUNT_HASH +
                " FROM " + LocMessDBContract.AvailableMessages.TABLE_NAME;
        private static String select2 = "SELECT " +
                AvailableP2pMessages._ID +
                ", " + AvailableP2pMessages.COLUMN_TITLE +
                ", " + AvailableP2pMessages.COLUMN_CONTENT +
                ", " + AvailableP2pMessages.COLUMN_AUTHOR +
                ", " + AvailableP2pMessages.COLUMN_LOCATION +
                ", " + AvailableP2pMessages.COLUMN_DATE_POSTED +
                ", " + AvailableP2pMessages.COLUMN_READ +
                ", null AS " + AvailableMessages.COLUMN_SERVER_ID +
                ", " + AvailableP2pMessages.COLUMN_P2P_ID +
                ", " + COLUMN_ACCOUNT_HASH +
                " FROM " + LocMessDBContract.AvailableP2pMessages.TABLE_NAME;
        private static String query = select1 + " UNION ALL " + select2 + ";";
        public static final String VIEW_NAME = "all_available_messages";
        public static final String CREATE_VIEW = "CREATE VIEW IF NOT EXISTS " + VIEW_NAME +
                " AS " + query;
    }

    public static class AvailableP2pMessages implements BaseColumns {
        public static final String AVAILABLE_P2P_MESSAGES_PATH = "messages/available_p2p";
        public static final String AVAILABLE_P2P_MESSAGES_ID_PATH = "messages/available_p2p/#";
        public static final String AVAILABLE_P2P_MESSAGES_TYPE = "vnd.android.cursor.dir/vnd.locmess.provider.messages.available_p2p";
        public static final String AVAILABLE_P2P_MESSAGES_ID_TYPE = "vnd.android.cursor.item/vnd.locmess.provider.messages.available_p2p";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+AVAILABLE_P2P_MESSAGES_PATH);
        public static final int ID_PATH_SEGMENT_INDEX = 2;

        public static final String TABLE_NAME = "available_p2p";
        public static final String COLUMN_TITLE = AvailableMessages.COLUMN_TITLE;
        public static final String COLUMN_CONTENT = AvailableMessages.COLUMN_CONTENT;
        public static final String COLUMN_LOCATION = AvailableMessages.COLUMN_LOCATION;
        public static final String COLUMN_AUTHOR = AvailableMessages.COLUMN_AUTHOR;
        public static final String COLUMN_DATE_POSTED = AvailableMessages.COLUMN_DATE_POSTED;
        public static final String COLUMN_READ = AvailableMessages.COLUMN_READ;
        public static final String COLUMN_P2P_ID = "p2p_id";

        public static final int MESSAGE_READ = 1;
        public static final int MESSAGE_NOT_READ = 0;

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_DATE_POSTED + " TEXT, " +
                COLUMN_READ + " BOOLEAN NOT NULL CHECK (" +
                COLUMN_READ + " IN (" + MESSAGE_READ + "," + MESSAGE_NOT_READ + ")), " +
                COLUMN_P2P_ID + " TEXT, " +
                COLUMN_ACCOUNT_HASH + " INTEGER " + ")";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                LocMessDBContract.AvailableP2pMessages._ID,
                LocMessDBContract.AvailableP2pMessages.COLUMN_TITLE,
                LocMessDBContract.AvailableP2pMessages.COLUMN_CONTENT,
                LocMessDBContract.AvailableP2pMessages.COLUMN_LOCATION,
                LocMessDBContract.AvailableP2pMessages.COLUMN_AUTHOR,
                LocMessDBContract.AvailableP2pMessages.COLUMN_DATE_POSTED,
                LocMessDBContract.AvailableP2pMessages.COLUMN_READ,
                LocMessDBContract.AvailableP2pMessages.COLUMN_P2P_ID
        };
    }

    public static class Keys implements BaseColumns {
        public static final String KEYS_PATH = "keys";
        public static final String KEYS_ID_PATH = "keys/#";
        public static final String KEYS_TYPE = "vnd.android.cursor.dir/vnd.locmess.provider.keys";
        public static final String KEYS_ID_TYPE = "vnd.android.cursor.item/vnd.locmess.provider.keys";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+KEYS_PATH);
        public static final int ID_PATH_SEGMENT_INDEX = 1;

        public static final String TABLE_NAME = "keys";
        public static final String COLUMN_NAME = TABLE_NAME + "_name";
        public static final String COLUMN_SERVER_ID = TABLE_NAME + "_server_id";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_SERVER_ID + " INTEGER, " +
                COLUMN_ACCOUNT_HASH + " INTEGER " + ")";

        public static final String[] DEFAULT_PROJECTION = new String[] {
                LocMessDBContract.Keys._ID,
                LocMessDBContract.Keys.COLUMN_NAME,
                LocMessDBContract.Keys.COLUMN_SERVER_ID
        };
    }
}
