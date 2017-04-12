package ist.meic.cmu.locmess_client.sql;

import android.provider.BaseColumns;

/**
 * Created by Catarina on 12/04/2017.
 */

public class LocMessDBContract {
    private LocMessDBContract(){

    }
    public static class KeyPair implements BaseColumns {
        public static final String TABLE_NAME = "keypair";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_VALUE = "value";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_KEY + " TEXT, " +
                COLUMN_VALUE + " TEXT " + ")";
    }
}
