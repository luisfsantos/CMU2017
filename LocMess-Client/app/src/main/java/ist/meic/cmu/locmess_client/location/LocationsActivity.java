package ist.meic.cmu.locmess_client.location;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.location.create.NewLocationActivity;
import ist.meic.cmu.locmess_client.messages.create.NewMessageActivity;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.request_builders.delete.DeleteLocationRequestBuilder;
import ist.meic.cmu.locmess_client.network.sync.SyncUtils;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.CoordinatesUtils;
import ist.meic.cmu.locmess_client.utils.DateUtils;
import ist.meic.cmu.locmess_client.utils.recycler.LocMessRecyclerView;
import ist.meic.cmu.locmess_client.utils.recycler.SimpleCursorRecyclerAdapter;

public class LocationsActivity extends BaseNavigationActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, SimpleCursorRecyclerAdapter.CursorRecyclerAdapterCallback{

    private static final String TAG = "LocationsActivity";
    static final int NEW_LOCATION_REQUEST = 1;
    private static final int LOCATIONS_LOADER_ID = R.id.locations_loader_id;

    SimpleCursorRecyclerAdapter mAdapter;
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_locations, frameLayout);

        // create account if necessary
        SyncUtils.CreateSyncAccount(this);

        LocMessRecyclerView mRecyclerView = (LocMessRecyclerView)findViewById(R.id.rv_card_list);
        TextView mEmptyView = (TextView)findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(mEmptyView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        setupAdapter();
        mRecyclerView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(LOCATIONS_LOADER_ID, new Bundle(), this);

        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Refreshing Locations");
                SyncUtils.pull(SyncUtils.PULL_LOCATIONS);
            }
        });

    }

    private void setupAdapter() {
        String[] uiBindFrom = { LocMessDBContract.Location.COLUMN_NAME,
                LocMessDBContract.Location.COLUMN_COORDINATES,
                LocMessDBContract.Location.COLUMN_AUTHOR,
                LocMessDBContract.Location.COLUMN_DATE_CREATED };
        int[] uiBindTo = {R.id.location_name,
                R.id.location_coordinates,
                R.id.location_author,
                R.id.location_create_date };
        mAdapter = new SimpleCursorRecyclerAdapter(R.layout.card_location, null, uiBindFrom, uiBindTo);
        mAdapter.setViewHolderCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_locations);
    }

    public void onNewLocationClicked(View view) {
        startActivityForResult(new Intent(this, NewLocationActivity.class), NEW_LOCATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "New location created");
            }
        }
    }

    public void postToLocation(int id) {
        Intent intent = new Intent(this, NewMessageActivity.class);
        intent.putExtra(NewMessageActivity.INTENT_LOCATION_ID, id);
        startActivity(intent);
    }

    public void removeLocation(int id, int serverID) {
        Uri uri = ContentUris.withAppendedId(LocMessDBContract.Location.CONTENT_URI, id);
        int count = getContentResolver().delete(uri, null, null);
        Log.d(TAG, "Deleted " + count + " row(s)");
        //TODO notify server
        RequestData data = null;
        try {
            data = new DeleteLocationRequestBuilder(serverID).build(null, RequestData.DELETE);
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Malformed URL: ", e);
        }
        SyncUtils.push(SyncUtils.DELETE_LOCATION, data, null);
    }

    @Override
    public void onAttachToViewHolder(View itemView) {
        Cursor cursor = mAdapter.getCursor();
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(LocMessDBContract.Location._ID));
        final int serverID = cursor.getInt(cursor.getColumnIndexOrThrow(LocMessDBContract.COLUMN_SERVER_ID));
        final String name = cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_NAME));
        final String dbDate = cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_DATE_CREATED));
        bindCoordinates(itemView, cursor);

        TextView dateCreated = (TextView)itemView.findViewById(R.id.location_create_date);
        dateCreated.setText(DateUtils.formatDateDbToLocale(dbDate));

        ImageButton newMessageBtn = (ImageButton)itemView.findViewById(R.id.new_msg_btn);
        newMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postToLocation(id);
            }
        });

        ImageButton removeBtn = (ImageButton)itemView.findViewById(R.id.remove_btn);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveDialog(name, id, serverID);
            }
        });

    }

    private void showRemoveDialog(final String name, final int id, final int serverID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.remove_location)
                .setMessage(getString(R.string.remove_dialog_message_start) +
                        " \"" + name + "\"")
                .setPositiveButton(R.string.remove_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeLocation(id, serverID);
                    }
                }).setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void bindCoordinates(View view, Cursor cursor) {
        String dbCoordinates = cursor.getString(
                cursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_COORDINATES));
        CoordinatesUtils.Coordinates coordinates = new CoordinatesUtils(this, dbCoordinates).parse();
        ImageView coordinatesIcon = (ImageView)view.findViewById(R.id.coordinates_ic);

        Drawable d = null;
        if (coordinates instanceof CoordinatesUtils.WifiCoordinates) {
            d = VectorDrawableCompat.create(getResources(), R.drawable.ic_wifi, null);

        } else if (coordinates instanceof CoordinatesUtils.GpsCoordinates) {
            d = VectorDrawableCompat.create(getResources(), R.drawable.ic_gps, null);
        }
        d = DrawableCompat.wrap(d);
        DrawableCompat.setTint(d, ContextCompat.getColor(this, R.color.icon_tint_dark));
        coordinatesIcon.setImageDrawable(d);
        ((TextView)view.findViewById(R.id.location_coordinates))
                .setText(coordinates.toString());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] queryCols = new String[] {
                LocMessDBContract.Location._ID,
                LocMessDBContract.Location.COLUMN_NAME,
                LocMessDBContract.Location.COLUMN_AUTHOR,
                LocMessDBContract.Location.COLUMN_DATE_CREATED,
                LocMessDBContract.Location.COLUMN_COORDINATES,
                LocMessDBContract.COLUMN_SERVER_ID
        };
        return new CursorLoader(LocationsActivity.this,
                LocMessDBContract.Location.CONTENT_URI,
                queryCols,          // the projection fields
                null,               // the selection criteria
                null,               // the selection args
                LocMessDBContract.Location.COLUMN_DATE_CREATED + " asc"                // the sort order
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.changeCursor(null);
    }
}
