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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import ist.meic.cmu.locmess_client.utils.DateUtils;
import ist.meic.cmu.locmess_client.utils.recycler.LocMessRecyclerView;
import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.utils.recycler.SimpleCursorRecyclerAdapter;
import ist.meic.cmu.locmess_client.location.create.NewLocationActivity;
import ist.meic.cmu.locmess_client.messages.create.NewMessageActivity;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.CoordinatesUtils;

public class LocationsActivity extends BaseNavigationActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, SimpleCursorRecyclerAdapter.CursorRecyclerAdapterCallback{

    private static final String TAG = "LocationsActivity";
    static final int NEW_LOCATION_REQUEST = 1;
    private static final int LOCATIONS_LOADER_ID = R.id.locations_loader_id;

    SimpleCursorRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_locations, frameLayout);

        LocMessRecyclerView mRecyclerView = (LocMessRecyclerView)findViewById(R.id.rv_card_list);
        TextView mEmptyView = (TextView)findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(mEmptyView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        setupAdapter();
        mRecyclerView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(LOCATIONS_LOADER_ID, new Bundle(), this);

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

    public void removeLocation(int id) {
        Uri uri = ContentUris.withAppendedId(LocMessDBContract.Location.CONTENT_URI, id);
        int count = getContentResolver().delete(uri, null, null);
        Log.d(TAG, "Deleted " + count + " row(s)");
        //TODO notify server
    }

    @Override
    public void onAttachToViewHolder(View itemView) {
        Cursor cursor = mAdapter.getCursor();
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(LocMessDBContract.Location._ID));
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
                showRemoveDialog(name, id);
            }
        });

    }

    private void showRemoveDialog(final String name, final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.remove_location)
                .setMessage(getString(R.string.remove_dialog_message_start) +
                        " \"" + name + "\"")
                .setPositiveButton(R.string.remove_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeLocation(id);
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
            ((TextView)view.findViewById(R.id.location_coordinates))
                    .setText(coordinates.toString());
            d = VectorDrawableCompat.create(getResources(), R.drawable.ic_gps, null);
        }
        d = DrawableCompat.wrap(d);
        DrawableCompat.setTint(d, ContextCompat.getColor(this, R.color.icon_tint_dark));
        coordinatesIcon.setImageDrawable(d);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] queryCols = new String[] {
                LocMessDBContract.Location._ID,
                LocMessDBContract.Location.COLUMN_NAME,
                LocMessDBContract.Location.COLUMN_AUTHOR,
                LocMessDBContract.Location.COLUMN_DATE_CREATED,
                LocMessDBContract.Location.COLUMN_COORDINATES
        };
        return new CursorLoader(LocationsActivity.this,
                LocMessDBContract.Location.CONTENT_URI,
                queryCols,          // the projection fields
                null,               // the selection criteria
                null,               // the selection args
                null                // the sort order
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }
}
