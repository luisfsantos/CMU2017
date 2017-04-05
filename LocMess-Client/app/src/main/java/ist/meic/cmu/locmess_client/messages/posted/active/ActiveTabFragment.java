package ist.meic.cmu.locmess_client.messages.posted.active;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ist.meic.cmu.locmess_client.LocMessRecyclerView;
import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.DummyData;
import ist.meic.cmu.locmess_client.data.Message;
import ist.meic.cmu.locmess_client.messages.OnRecyclerCardClicked;
import ist.meic.cmu.locmess_client.messages.ShowMessageActivity;

/**
 * Created by lads on 05/04/2017.
 */

public class ActiveTabFragment extends Fragment implements OnRecyclerCardClicked {

    private static List<Message> DUMMY_DATASET;

    ActiveCardAdapter mAdapter;

    public ActiveTabFragment() {
        DUMMY_DATASET = new LinkedList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            Message[] parcelables = (Message[]) savedInstanceState.getParcelableArray("posted_messages");
            if (parcelables != null) {
                DUMMY_DATASET = new LinkedList<>(Arrays.asList(parcelables));
            }
        } else {
            DUMMY_DATASET = DummyData.createDummyMessages(4, getContext());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(getClass().getName(), "saving posted messages");
        outState.putParcelableArray("posted_messages", DUMMY_DATASET.toArray(new Message[DUMMY_DATASET.size()]));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.recyclerview_layout, container, false);
        LocMessRecyclerView mRecyclerView = (LocMessRecyclerView) rootView.findViewById(R.id.rv_card_list);
        TextView mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(mEmptyView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ActiveCardAdapter(DUMMY_DATASET, this);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onRecyclerCardClicked(View view) {
        int position = (int)view.getTag();
        Message message = DUMMY_DATASET.get(position);
        Intent intent = new Intent(getContext(), ShowMessageActivity.class);
        intent.putExtra("message", message);
        startActivity(intent);
    }

}
