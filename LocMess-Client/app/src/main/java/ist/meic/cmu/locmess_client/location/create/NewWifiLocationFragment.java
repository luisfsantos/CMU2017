package ist.meic.cmu.locmess_client.location.create;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by Catarina on 05/04/2017.
 */

public class NewWifiLocationFragment extends Fragment {

    RadioGroup mRadioGroup;
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
        outState.putStringArrayList("ssids", (ArrayList<String>)mSSIDS);
        int index = mRadioGroup.indexOfChild(mRadioGroup.findViewById(mRadioGroup.getCheckedRadioButtonId()));
        outState.putInt("checked_ssid", index);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            ArrayList<String> saved = savedInstanceState.getStringArrayList("ssids");
            if (saved != null) {
                mSSIDS = saved;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_new_wifi_location, container, false);
        mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);
        mRadioGroup = (RadioGroup)rootView.findViewById(R.id.radio_group_wifi);
        fillRadioGroup();
        if (savedInstanceState != null) {
            int last_checked_index = savedInstanceState.getInt("checked_ssid");
            int last_checked_id = mRadioGroup.getChildAt(last_checked_index).getId();
            mRadioGroup.check(last_checked_id);
        }
        return rootView;
    }

    private void fillRadioGroup() {
        if (mSSIDS.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRadioGroup.setVisibility(View.GONE);
            return;
        }
        mEmptyView.setVisibility(View.GONE);
        mRadioGroup.setVisibility(View.VISIBLE);
        final int horizontal_margin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        final int vertical_margin = getResources().getDimensionPixelSize(R.dimen.small_margin);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(getContext(), null);
        params.setMargins(horizontal_margin, vertical_margin, horizontal_margin, 0);
        boolean first = true;
        for (String ssid : mSSIDS) {
            RadioButton button = new RadioButton(getContext());
            button.setText(ssid);
            button.setLayoutParams(params);
            mRadioGroup.addView(button);
            if (first) {
                mRadioGroup.check(button.getId());
                first = false;
            }
        }
    }
}