package ist.meic.cmu.locmess_client.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by Catarina on 12/04/2017.
 */

public class LocMessDBSQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "locmess_database";

    public LocMessDBSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(LocMessDBContract.KeyPair.CREATE_TABLE);
        sqLiteDatabase.execSQL(LocMessDBContract.Location.CREATE_TABLE);
        sqLiteDatabase.execSQL(LocMessDBContract.PostedMessages.CREATE_TABLE);
        sqLiteDatabase.execSQL(LocMessDBContract.OpenedMessages.CREATE_TABLE);
        sqLiteDatabase.execSQL(LocMessDBContract.AvailableMessages.CREATE_TABLE);
        sqLiteDatabase.execSQL(LocMessDBContract.Keys.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // this shouldn't happen during production of a real app lol
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocMessDBContract.KeyPair.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocMessDBContract.Location.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocMessDBContract.PostedMessages.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocMessDBContract.OpenedMessages.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocMessDBContract.AvailableMessages.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocMessDBContract.Keys.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            if (!db.isReadOnly()) {
                // Enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }
    }
}
