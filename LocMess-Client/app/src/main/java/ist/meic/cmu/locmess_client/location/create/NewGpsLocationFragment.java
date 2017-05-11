package ist.meic.cmu.locmess_client.location.create;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by Catarina on 05/04/2017.
 */

public class NewGpsLocationFragment extends Fragment {

    EditText mLatitude;
    EditText mLongitude;
    EditText mRadius;


    public static NewGpsLocationFragment newInstance() {

        Bundle args = new Bundle();
        
        NewGpsLocationFragment fragment = new NewGpsLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_new_gps_location, container, false);
        mLatitude = (EditText) rootView.findViewById(R.id.gps_latitude);
        mLongitude = (EditText) rootView.findViewById(R.id.gps_longitude);
        mRadius = (EditText) rootView.findViewById(R.id.gps_radius);

        return rootView;
    }
}
