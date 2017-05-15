package ist.meic.cmu.locmess_client.profile;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.authentication.LoginActivity;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;
import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.request_builders.GenericDeleteRequestBuilder;
import ist.meic.cmu.locmess_client.network.request_builders.create.NewKeypairRequestBuilder;
import ist.meic.cmu.locmess_client.network.sync.SyncUtils;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.recycler.LocMessRecyclerView;
import ist.meic.cmu.locmess_client.utils.recycler.SimpleCursorRecyclerAdapter;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Catarina on 02/04/2017.
 */

public class ProfileActivity extends BaseNavigationActivity
        implements KeyPairDialogFragment.InsertKeyPairListener, LoaderManager.LoaderCallbacks<Cursor>,
        SimpleCursorRecyclerAdapter.CursorRecyclerAdapterCallback {

    private static final String TAG = "ProfileActivity";
    private static final int KEYPAIRS_LOADER_ID = R.id.keypairs_loader_id;
    SimpleCursorRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_profile, frameLayout);

        // create account if necessary
        SyncUtils.CreateSyncAccount(this);

        SharedPreferences pref = getSharedPreferences(getResources().getString(R.string.preference_file_key), MODE_PRIVATE);
        String username = pref.getString(getResources().getString(R.string.pref_username), "username");
        ((TextView)findViewById(R.id.login_username)).setText(username);

        LocMessRecyclerView mRecyclerView = (LocMessRecyclerView) findViewById(R.id.rv_key_pairs);
        TextView mEmptyView = (TextView)findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(mEmptyView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        setupAdapter();
        mRecyclerView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(KEYPAIRS_LOADER_ID, new Bundle(), this);
    }

    private void setupAdapter() {
        String[] uiBindFrom = { LocMessDBContract.KeyPair.COLUMN_KEY,
                LocMessDBContract.KeyPair.COLUMN_VALUE };
        int[] uiBindTo = {R.id.key, R.id.value};
        mAdapter = new SimpleCursorRecyclerAdapter(R.layout.item_key_pair, null, uiBindFrom, uiBindTo);
        mAdapter.setViewHolderCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_profile);
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
        ContentValues values = new ContentValues();
        values.put(LocMessDBContract.KeyPair.COLUMN_KEY, key);
        values.put(LocMessDBContract.KeyPair.COLUMN_VALUE, value);
        Uri uri = getContentResolver().insert(LocMessDBContract.KeyPair.CONTENT_URI, values);
        Log.d(TAG, "New row URI is " + uri);

        try {
            RequestData data = new NewKeypairRequestBuilder(key, value).build(LocMessURL.NEW_KEYPAIR, RequestData.POST);
            SyncUtils.push(SyncUtils.CREATE_KEYPAIR, data, uri);
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "URL is malformed", e);
        }
    }

    private void removeKeyPair(int id, int serverID) {
        Uri uri = ContentUris.withAppendedId(LocMessDBContract.KeyPair.CONTENT_URI, id);
        int count = getContentResolver().delete(uri, null, null);
        Log.d(TAG, "Deleted " + count + " row(s)");
        try {
            RequestData data = new GenericDeleteRequestBuilder(serverID).build(LocMessURL.DELETE_KEYPAIR, RequestData.DELETE);
            SyncUtils.push(SyncUtils.DELETE_KEYPAIR, data, null);
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Malformed URL: ", e);
        }
    }

    @Override
    public void onAttachToViewHolder(View itemView) {
        final Cursor cursor = mAdapter.getCursor();
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(LocMessDBContract.KeyPair._ID));
        final int serverID = cursor.getInt(cursor.getColumnIndexOrThrow(LocMessDBContract.KeyPair.COLUMN_SERVER_ID));
        final String key = cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.KeyPair.COLUMN_KEY));
        final String value = cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.KeyPair.COLUMN_VALUE));
        ImageButton removeBtn = (ImageButton)itemView.findViewById(R.id.remove_btn);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveDialog(key, value, id, serverID);
            }
        });
    }

    private void showRemoveDialog(final String key, final String value, final int id, final int serverID) {
        String dialogMessage = getString(R.string.remove_dialog_message_start, key + ": " + value);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.remove_keypair)
                .setMessage(dialogMessage)
                .setPositiveButton(R.string.remove_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeKeyPair(id, serverID);
                    }
                }).setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] queryCols = new String[] { LocMessDBContract.KeyPair._ID,
                LocMessDBContract.KeyPair.COLUMN_KEY,
                LocMessDBContract.KeyPair.COLUMN_VALUE,
                LocMessDBContract.KeyPair.COLUMN_SERVER_ID};
        return new CursorLoader(ProfileActivity.this,
                LocMessDBContract.KeyPair.CONTENT_URI,
                queryCols,          // the projection fields
                null,               // the selection criteria
                null,               // the selection args
                null                // the sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
