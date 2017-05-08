package ist.meic.cmu.locmess_client.location.create;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.request_builders.create.NewGpsLocationRequestBuilder;
import ist.meic.cmu.locmess_client.network.request_builders.create.NewWifiLocationRequestBuilder;
import ist.meic.cmu.locmess_client.network.sync.SyncUtils;
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

    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.radio_gps:
                showHideFragments(mGpsFragment, mWifiFragment);
                break;
            case R.id.radio_wifi:
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

    private void createLocation() {
        EditText location = (EditText)findViewById(R.id.new_location_name);
        String name = location.getText().toString().trim();
        if (name.isEmpty()) {
            location.setError(getResources().getString(R.string.name_missing));
            location.requestFocus();
            return;
        }
        switch (mCoordinatesChoice.getCheckedRadioButtonId()) {
            case R.id.radio_gps:
                String latitude = mGpsFragment.mLatitude.getText().toString().trim();
                String longitude = mGpsFragment.mLongitude.getText().toString().trim();
                String radius = mGpsFragment.mRadius.getText().toString().trim();
                if (latitude.isEmpty()) {
                    mGpsFragment.mLatitude.setError(getResources().getString(R.string.field_missing));
                    mGpsFragment.mLatitude.requestFocus();
                    return;
                }
                if (longitude.isEmpty()) {
                    mGpsFragment.mLongitude.setError(getResources().getString(R.string.field_missing));
                    mGpsFragment.mLongitude.requestFocus();
                    return;
                }
                if (radius.isEmpty()) {
                    mGpsFragment.mRadius.setError(getResources().getString(R.string.field_missing));
                    mGpsFragment.mRadius.requestFocus();
                    return;
                }
                try {
                    createGpsLocation(name, latitude, longitude, radius);
                } catch (MalformedURLException e) {
                    Log.wtf(TAG, "URL is malformed", e);
                }
                break;

            case R.id.radio_wifi:
                final List<String> ssids = mWifiFragment.getmSsidsChecked();
                if (ssids.isEmpty()) {
                    Toast.makeText(this, getResources().getString(R.string.pick_network), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    createWifiLocation(name, ssids);
                } catch (MalformedURLException e) {
                    Log.wtf(TAG, "URL is malformed", e);
                }
                break;
        }
        setResult(RESULT_OK);
        finish();
    }

    private void createGpsLocation(String name, String latitude, String longitude, String radius) throws MalformedURLException {
        Date now = new Date();
        String dbDate = DateUtils.formatDateTimeLocaleToDb(now);
        String isoDate = DateUtils.formatDateTimeISO8601(now);
        String coordinates = CoordinatesUtils.formatGpsToDb(latitude, longitude, radius);

        RequestData data = new NewGpsLocationRequestBuilder(name, isoDate,
                Double.parseDouble(latitude),
                Double.parseDouble(longitude),
                Double.parseDouble(radius)
        ).build(LocMessURL.NEW_LOCATION, RequestData.POST);

        Uri uri = saveToDb(name, dbDate, coordinates);
        SyncUtils.push(getBaseContext(), SyncUtils.CREATE_LOCATION, data, uri);
    }

    private void createWifiLocation(String name, List<String> ssids) throws MalformedURLException {
        Date now = new Date();
        String dbDate = DateUtils.formatDateTimeLocaleToDb(now);
        String isoDate = DateUtils.formatDateTimeISO8601(now);
        String ssidString = CoordinatesUtils.formatWifiToDb(ssids);

        RequestData data = new NewWifiLocationRequestBuilder(
                name,
                isoDate,
                ssids
        ).build(LocMessURL.NEW_LOCATION, RequestData.POST);

        Uri uri = saveToDb(name, dbDate, ssidString);
        SyncUtils.push(getBaseContext(), SyncUtils.CREATE_LOCATION, data, uri);
    }

    private Uri saveToDb(String name, String date, String coordinates) {
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String author = pref.getString(getString(R.string.pref_username), "No author");
        ContentValues values = new ContentValues();
        values.put(LocMessDBContract.Location.COLUMN_NAME, name);
        values.put(LocMessDBContract.Location.COLUMN_AUTHOR, author);
        values.put(LocMessDBContract.Location.COLUMN_DATE_CREATED, date);
        values.put(LocMessDBContract.Location.COLUMN_COORDINATES, coordinates);
        Uri uri = getContentResolver().insert(LocMessDBContract.Location.CONTENT_URI, values);
        Log.d(TAG, "New row URI is " + uri);
        return uri;
    }
}
