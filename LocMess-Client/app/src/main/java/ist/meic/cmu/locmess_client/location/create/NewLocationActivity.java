package ist.meic.cmu.locmess_client.location.create;

import android.content.ContentValues;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.CoordinatesUtils;
import ist.meic.cmu.locmess_client.utils.DateUtils;

public class NewLocationActivity extends AppCompatActivity {

    private static final String TAG = "NewLocationActivity";
    NewWifiLocationFragment mWifiFragment;
    NewGpsLocationFragment mGpsFragment;
    private RadioGroup mCoordinatesChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable close = AppCompatResources.getDrawable(this, R.drawable.ic_close);
        close.setColorFilter(ContextCompat.getColor(this, R.color.light_text), PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(close);

        mCoordinatesChoice = (RadioGroup)findViewById(R.id.coordinates_choice);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "gps_fragment", mGpsFragment);
        getSupportFragmentManager().putFragment(outState, "wifi_fragment", mWifiFragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mGpsFragment = (NewGpsLocationFragment) getSupportFragmentManager().getFragment(savedInstanceState, "gps_fragment");
            mWifiFragment = (NewWifiLocationFragment) getSupportFragmentManager().getFragment(savedInstanceState, "wifi_fragment");
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mWifiFragment == null) {
            mWifiFragment = NewWifiLocationFragment.newInstance();
            ft.add(R.id.coordinates_view, mWifiFragment);
            ft.hide(mWifiFragment);
        }
        if (mGpsFragment == null) {
            mGpsFragment = NewGpsLocationFragment.newInstance();
            ft.add(R.id.coordinates_view, mGpsFragment);
        }
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_location_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Log.d(TAG, "Save clicked");
                createLocation();
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createLocation() {
        EditText location = (EditText)findViewById(R.id.new_location_name);
        String name = location.getText().toString().trim();
        if (name.isEmpty()) {
            location.setError(getResources().getString(R.string.name_missing));
            return;
        }
        switch (mCoordinatesChoice.getCheckedRadioButtonId()) {
            case R.id.radio_gps:
                String latitude = mGpsFragment.mLatitude.getText().toString().trim();
                String longitude = mGpsFragment.mLongitude.getText().toString().trim();
                String radius = mGpsFragment.mRadius.getText().toString().trim();
                if (latitude.isEmpty()) {
                    mGpsFragment.mLatitude.setError(getResources().getString(R.string.field_missing));
                    return;
                }
                if (longitude.isEmpty()) {
                    mGpsFragment.mLongitude.setError(getResources().getString(R.string.field_missing));
                    return;
                }
                if (radius.isEmpty()) {
                    mGpsFragment.mRadius.setError(getResources().getString(R.string.field_missing));
                    return;
                }
                createGpsLocation(name, latitude, longitude, radius);
                break;

            case R.id.radio_wifi:
                List<String> ssidsList = mWifiFragment.mSsidsChecked;
                if (!ssidsList.isEmpty()) {
                    String[] ssids = ssidsList.toArray(new String[ssidsList.size()]);
                    Log.d(TAG, Arrays.toString(ssids));
                    createWifiLocation(name, ssids);
                }
                break;
        }
        setResult(RESULT_OK);
        finish();
    }

    private void createGpsLocation(String name, String latitude, String longitude, String radius) {
        String coordinates = CoordinatesUtils.formatGpsToDb(latitude, longitude, radius);
        //FIXME replace with username
        String author = "username";
        String date = DateUtils.formatDateTimeLocaleToDb(new Date());

        ContentValues values = new ContentValues();
        values.put(LocMessDBContract.Location.COLUMN_NAME, name);
        values.put(LocMessDBContract.Location.COLUMN_AUTHOR, author);
        values.put(LocMessDBContract.Location.COLUMN_DATE_CREATED, date);
        values.put(LocMessDBContract.Location.COLUMN_COORDINATES, coordinates);
        Uri uri = getContentResolver().insert(LocMessDBContract.Location.CONTENT_URI, values);
        Log.d(TAG, "New row URI is " + uri);
        //TODO post location to server
    }

    private void createWifiLocation(String name, String[] ssids) {
        //FIXME replace with username
        String author = "username";
        String date = DateUtils.formatDateTimeLocaleToDb(new Date());
        String ssidString = CoordinatesUtils.formatWifiToDb(ssids);

        ContentValues values = new ContentValues();
        values.put(LocMessDBContract.Location.COLUMN_NAME, name);
        values.put(LocMessDBContract.Location.COLUMN_AUTHOR, author);
        values.put(LocMessDBContract.Location.COLUMN_DATE_CREATED, date);
        values.put(LocMessDBContract.Location.COLUMN_COORDINATES, ssidString);
        Uri uri = getContentResolver().insert(LocMessDBContract.Location.CONTENT_URI, values);
        Log.d(TAG, "New row URI is " + uri);
        //TODO post location to server
    }

    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.radio_gps:
                Log.d(TAG, "radio gps clicked");
                showHideFragments(mGpsFragment, mWifiFragment);
                break;
            case R.id.radio_wifi:
                Log.d(TAG, "radio wifi clicked");
                showHideFragments(mWifiFragment, mGpsFragment);
                break;
        }
    }

    private void showHideFragments(final Fragment toShow, final Fragment toHide){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(toShow);
        ft.hide(toHide);
        ft.commit();
    }
}
