package ist.meic.cmu.locmess_client.location;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ist.meic.cmu.locmess_client.LocMessRecyclerView;
import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.Location;
import ist.meic.cmu.locmess_client.location.create.NewLocationActivity;
import ist.meic.cmu.locmess_client.messages.create.NewMessageActivity;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.sql.LocMessDBSQLiteHelper;

public class LocationsActivity extends BaseNavigationActivity implements LocationsAdapter.LocationCardListener{

    static final int NEW_LOCATION_REQUEST = 1;
    LocMessRecyclerView mRecyclerView;
    LocMessDBSQLiteHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_locations, frameLayout);
        mDbHelper = new LocMessDBSQLiteHelper(this);

        mRecyclerView = (LocMessRecyclerView)findViewById(R.id.rv_card_list);
        TextView mEmptyView = (TextView)findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(mEmptyView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new LocationsAdapter(this, readLocationsFromDB());
        mRecyclerView.setAdapter(mAdapter);
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
                LocationsAdapter adapter = (LocationsAdapter)mRecyclerView.getAdapter();
                adapter.changeCursor(readLocationsFromDB());
            }
        }
    }

    private Cursor readLocationsFromDB() {
        String[] queryCols = new String[] {"_id",
                LocMessDBContract.Location.COLUMN_NAME,
                LocMessDBContract.Location.COLUMN_AUTHOR,
                LocMessDBContract.Location.COLUMN_DATE_CREATED,
                LocMessDBContract.Location.COLUMN_COORDINATES
        };

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        return database.query(
                LocMessDBContract.Location.TABLE_NAME,       // The table to query
                queryCols,                                  // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null                                        // don't sort
        );
    }

    @Override
    public void postToLocation(Location location) {
        //FIXME pass location name/id/anything in the intent
        Intent intent = new Intent(this, NewMessageActivity.class);
        intent.putExtra(NewMessageActivity.INTENT_LOCATION, location.name);
        startActivity(intent);
    }

    @Override
    public void removeLocation(Location location) {

    }
}
