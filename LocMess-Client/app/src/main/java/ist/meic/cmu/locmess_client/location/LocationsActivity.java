package ist.meic.cmu.locmess_client.location;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ist.meic.cmu.locmess_client.LocMessRecyclerView;
import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.GpsCoordinates;
import ist.meic.cmu.locmess_client.data.Location;
import ist.meic.cmu.locmess_client.data.WifiCoordinates;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;

public class LocationsActivity extends BaseNavigationActivity implements LocationsAdapter.LocationCardListener{

    List<Location> mLocations = new LinkedList<>();
    LocMessRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_locations, frameLayout);
        if (savedInstanceState == null) createDummyData(5);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_locations);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("locations", mLocations.toArray(new Location[mLocations.size()]));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            Location[] parcelables = (Location[]) savedInstanceState.getParcelableArray("locations");
            if (parcelables != null) {
                mLocations = new LinkedList<>(Arrays.asList(parcelables));
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mRecyclerView = (LocMessRecyclerView)findViewById(R.id.rv_card_list);
        TextView mEmptyView = (TextView)findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(mEmptyView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new LocationsAdapter(mLocations, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onNewLocationClicked(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    public void createDummyData(int size) {
        Log.d("createDummyData", "creating dummy locations");
        for (int i = 0; i < size; i++) {

            mLocations.add(new Location("Arco do Cego",
                    i % 2 == 0 ? new WifiCoordinates("eduroam") : new GpsCoordinates(34.113262, 45.126263, 20),
                    "catarina",
                    new Date()));
        }
    }

    @Override
    public void postToLocation(Location location) {
        //TODO: start new message activity - pass location name/id/anything in the intent
        Snackbar.make(mRecyclerView, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }
}
