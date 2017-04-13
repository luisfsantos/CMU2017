package ist.meic.cmu.locmess_client.profile;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ist.meic.cmu.locmess_client.LocMessRecyclerView;
import ist.meic.cmu.locmess_client.LoginActivity;
import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.sql.LocMessDBSQLiteHelper;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Catarina on 02/04/2017.
 */

public class ProfileActivity extends BaseNavigationActivity
        implements KeyPairDialogFragment.InsertKeyPairListener, KeyPairsAdapter.RemoveKeyPairListener{

    private static final String TAG = "ProfileActivity";
    LocMessRecyclerView mRecyclerView;
    LocMessDBSQLiteHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_profile, frameLayout);
        mDbHelper = new LocMessDBSQLiteHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_profile);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mRecyclerView = (LocMessRecyclerView) findViewById(R.id.rv_key_pairs);
        TextView mEmptyView = (TextView)findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(mEmptyView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new KeyPairsAdapter(this, readKeyPairsFromDB());
        mRecyclerView.setAdapter(mAdapter);
    }

    private Cursor readKeyPairsFromDB() {
        Log.i(TAG, "Reading all key pairs from database");
        String[] queryCols = new String[] {"_id", LocMessDBContract.KeyPair.COLUMN_KEY, LocMessDBContract.KeyPair.COLUMN_VALUE};

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        return database.query(
                LocMessDBContract.KeyPair.TABLE_NAME,       // The table to query
                queryCols,                                  // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null                                        // don't sort
        );
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    public void onLogoutClicked(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK|FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(intent);
        finish();
    }

    public void onNewKeyPairClicked(View view) {
        KeyPairDialogFragment dialogFragment = KeyPairDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void addNewKeyPair(String key, String value) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocMessDBContract.KeyPair.COLUMN_KEY, key);
        values.put(LocMessDBContract.KeyPair.COLUMN_VALUE, value);
        long newRowId = database.insert(LocMessDBContract.KeyPair.TABLE_NAME, null, values);
        Log.d(TAG, "New row id is " + newRowId);

        KeyPairsAdapter adapter = (KeyPairsAdapter) mRecyclerView.getAdapter();
        adapter.changeCursor(readKeyPairsFromDB());
        // TODO notify server
    }

    @Override
    public void removeKeyPair(String key, String value) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        String selection = LocMessDBContract.KeyPair.COLUMN_KEY + " LIKE ? AND " +
                LocMessDBContract.KeyPair.COLUMN_VALUE + " LIKE ?";
        String[] selectionArgs = { key, value };
        long numRowsDeleted = database.delete(LocMessDBContract.KeyPair.TABLE_NAME, selection, selectionArgs);
        Log.d(TAG, "Deleted " + numRowsDeleted + " row(s)");

        KeyPairsAdapter adapter = (KeyPairsAdapter) mRecyclerView.getAdapter();
        adapter.changeCursor(readKeyPairsFromDB());
        //TODO notify server
    }
}
