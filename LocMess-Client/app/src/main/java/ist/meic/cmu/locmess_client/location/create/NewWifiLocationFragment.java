package ist.meic.cmu.locmess_client.location.create;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
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
import java.util.Collections;
import java.util.List;

import ist.meic.cmu.locmess_client.R;
import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;

/**
 * Created by Catarina on 05/04/2017.
 */

public class NewWifiLocationFragment extends Fragment implements SimWifiP2pManager.PeerListListener{

    private static final String TAG = "NewWifiLocationFragment";

    private LinearLayout mCheckBoxContainer;
    private List<String> mSsidsChecked = new ArrayList<>();
    TextView mEmptyView;
    private List<String> mSSIDS = new ArrayList<>();

    public List<String> getmSsidsChecked() {
        return Collections.unmodifiableList(mSsidsChecked);
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
            checkBox.setOnClickListener(mOnCheckBoxClicked);
            if (mSsidsChecked.contains(ssid)) {
                checkBox.setChecked(true);
            }
            mCheckBoxContainer.addView(checkBox, params);
        }
    }

    private View.OnClickListener mOnCheckBoxClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (((CheckBox) view).isChecked()) {
                mSsidsChecked.add(((CheckBox) view).getText().toString());
            } else {
                mSsidsChecked.remove(((CheckBox) view).getText().toString());
            }
            Log.d(TAG, "ssids_checked: " + TextUtils.join(", ", mSsidsChecked));
        }
    };

    /*
    * TERMITE SETUP AND CALLBACKS
    * */

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(getActivity().getApplication(), getContext().getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Binding to WifiP2pService");
        Intent intent = new Intent(getContext(), SimWifiP2pService.class);
        getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        getContext().registerReceiver(mOnRefreshPeers, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBound) {
            Log.d(TAG, "Unbinding from WifiP2pService");
            getContext().unbindService(mConnection);
            mBound = false;
        }
        getContext().unregisterReceiver(mOnRefreshPeers);
    }

    private BroadcastReceiver mOnRefreshPeers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                if (mBound) {
                    mManager.requestPeers(mChannel, NewWifiLocationFragment.this);
                } else {
                    Log.i(TAG, "Service not bound");
                }
            }
        }
    };

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        // issue: if we "move" two devices together in the Termite-Cli (cmd) before the service is bound, peer list is empty
        // ^ this is the expected behaviour !!
        Log.d(TAG, "#peers=" + peers.getDeviceList().size());
        mSSIDS.clear();
        mCheckBoxContainer.removeAllViews();
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            mSSIDS.add(device.deviceName);
        }
        mSsidsChecked.retainAll(mSSIDS);
        inflateCheckBoxes();
    }
}
