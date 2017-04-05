package ist.meic.cmu.locmess_client.location.create;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ist.meic.cmu.locmess_client.R;

public class NewLocationActivity extends AppCompatActivity {

    private static final String TAG = "NewLocationActivity";
    NewWifiLocationFragment mWifiFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable close = ContextCompat.getDrawable(this, R.drawable.ic_close);
        close.setColorFilter(ContextCompat.getColor(this, R.color.light_text), PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(close);


        mWifiFragment = NewWifiLocationFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.coordinates_view, mWifiFragment);
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
                createLocation();
                Log.d(TAG, "Save clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createLocation() {
        //TODO
        final int checked_id = mWifiFragment.mRadioGroup.getCheckedRadioButtonId();
        RadioButton button = (RadioButton)mWifiFragment.mRadioGroup.findViewById(checked_id);
        Log.d(TAG, button.getText().toString());
        finish();
    }

    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.radio_gps:
                Log.d(TAG, "radio gps clicked");
                //TODO
                break;
            case R.id.radio_wifi:
                Log.d(TAG, "radio wifi clicked");
                showHideFragment(mWifiFragment);
                break;
        }
    }
    public void showHideFragment(final Fragment fragment){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);

        if (fragment.isHidden()) {
            ft.show(fragment);
        } else {
            ft.hide(fragment);
        }
        ft.commit();
    }

    public void refreshNetworks(View view) {
        //TODO
        Log.d(TAG, "refresh clicked");
    }
}
