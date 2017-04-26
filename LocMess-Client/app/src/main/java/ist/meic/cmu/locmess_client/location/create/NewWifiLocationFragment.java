package ist.meic.cmu.locmess_client.location.create;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by Catarina on 05/04/2017.
 */

public class NewWifiLocationFragment extends Fragment {

    private static final String TAG = "NewWifiLocationFragment";

    LinearLayout mCheckBoxContainer;
    List<String> mSsidsChecked = new ArrayList<>();
    TextView mEmptyView;
    private static List<String> mSSIDS = new ArrayList<>();

    static {
        mSSIDS.add("eduroam");
        mSSIDS.add("2cool4school");
        mSSIDS.add("some wifi");
    }

    public static NewWifiLocationFragment newInstance() {

        Bundle args = new Bundle();
        
        NewWifiLocationFragment fragment = new NewWifiLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!mSSIDS.isEmpty()) {
            outState.putStringArrayList("ssids", (ArrayList<String>) mSSIDS);
        }
        if (!mSsidsChecked.isEmpty()) {
            outState.putStringArrayList("ssids_checked", (ArrayList<String>) mSsidsChecked);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            ArrayList<String> saved_ssids = savedInstanceState.getStringArrayList("ssids");
            if (saved_ssids != null) mSSIDS = saved_ssids;
            ArrayList<String> checked_ssids = savedInstanceState.getStringArrayList("ssids_checked");
            if (checked_ssids != null) mSsidsChecked = checked_ssids;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_new_wifi_location, container, false);
        mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);
        mCheckBoxContainer = (LinearLayout)rootView.findViewById(R.id.checkbox_group_wifi);
        inflateCheckBoxes();
        rootView.findViewById(R.id.refresh).setOnClickListener(new OnRefreshClicked());
        return rootView;
    }

    private void inflateCheckBoxes() {
        if (mSSIDS.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mCheckBoxContainer.setVisibility(View.GONE);
            return;
        }
        mEmptyView.setVisibility(View.GONE);
        mCheckBoxContainer.setVisibility(View.VISIBLE);
        final int horizontal_margin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        final int vertical_margin = getResources().getDimensionPixelSize(R.dimen.small_margin);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(horizontal_margin, vertical_margin, horizontal_margin, 0);
        for (String ssid : mSSIDS) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(ssid);
            checkBox.setMaxLines(1);
            checkBox.setEllipsize(TextUtils.TruncateAt.END);
            checkBox.setOnClickListener(new OnCheckBoxClicked());
            if (mSsidsChecked.contains(ssid)) {
                checkBox.setChecked(true);
            }
            mCheckBoxContainer.addView(checkBox, params);
        }
    }

    private class OnCheckBoxClicked implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                mSsidsChecked.add(((CheckBox) view).getText().toString());
            } else {
                mSsidsChecked.remove(((CheckBox) view).getText().toString());
            }
            Log.d(TAG, TextUtils.join(", ", mSsidsChecked));
        }
    }

    private class OnRefreshClicked implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //TODO
            Log.d(TAG, "refresh clicked");
//        mSsidsChecked.clear();
//        mCheckBoxContainer.removeAllViews();
//        do something to mSSIDS
//        inflateCheckBoxes();
        }
    }
}
