package ist.meic.cmu.locmess_client.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ist.meic.cmu.locmess_client.LocMessRecyclerView;
import ist.meic.cmu.locmess_client.LoginActivity;
import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.KeyPair;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Catarina on 02/04/2017.
 */

public class ProfileActivity extends BaseNavigationActivity implements KeyPairDialogFragment.KeyPairDialogListener{

    LocMessRecyclerView mRecyclerView;
    List<KeyPair> mKeyPairs = new LinkedList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_profile, frameLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_profile);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("key_pairs", mKeyPairs.toArray(new KeyPair[mKeyPairs.size()]));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            KeyPair[] parcelables = (KeyPair[]) savedInstanceState.getParcelableArray("key_pairs");
            if (parcelables != null) {
                mKeyPairs = new LinkedList<>(Arrays.asList(parcelables));
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mRecyclerView = (LocMessRecyclerView) findViewById(R.id.rv_key_pairs);
        TextView mEmptyView = (TextView)findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(mEmptyView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new KeyPairsAdapter(mKeyPairs);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onLogoutClicked(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK|FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(intent);
        finish();
    }

    public void onNewKeyPairClicked(View view) {
        KeyPairDialogFragment dialogFragment = KeyPairDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void addNewKeyPair(String key, String value) {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        mKeyPairs.add(new KeyPair(key, value));
        adapter.notifyDataSetChanged();
    }
}
