package ist.meic.cmu.locmess_client.network.location_update;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Catarina on 01/05/2017.
 */

public class LocationPermissionsActivity extends AppCompatActivity {
    private static final String TAG = "LocationPermissionActiv";
    private static final int REQUEST_CODE_LOCATION = 1;

    ResultReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = getIntent().getParcelableExtra(LocationUpdateService.KEY_RECEIVER);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Bundle resultData = new Bundle();
        switch (requestCode) {
            case REQUEST_CODE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    resultData.putInt(LocationUpdateService.KEY_RESULT, PackageManager.PERMISSION_GRANTED);
                } else {
                    resultData.putInt(LocationUpdateService.KEY_RESULT, PackageManager.PERMISSION_DENIED);
                }
        }
        receiver.send(RESULT_OK, resultData);
        finish();
    }
}
