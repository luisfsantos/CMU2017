package ist.meic.cmu.locmess_client;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ist.meic.cmu.locmess_client.data.LocMessage;

/**
 * Created by Catarina on 30/03/2017.
 */

public class TabFragment extends Fragment {
    private static List<LocMessage> DUMMY_DATASET;

    public TabFragment() {
        DUMMY_DATASET = createDummyData(12);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.recyclerview_layout, container, false);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_card_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);

        CardAdapter mAdapter = new CardAdapter(DUMMY_DATASET, R.layout.available_msg_card);
        rv.setAdapter(mAdapter);

        return rootView;
    }

    private List<LocMessage> createDummyData(int size) {
        List<LocMessage> list = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            LocMessage msg = new LocMessage("catarina" + i, "Free pizza",
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                            "tempor incididunt ut labore et dolore magna aliqua.",
                    new Date(),
                    "Arco do Cego"
            );
            if (i % 2 == 0) {
                msg.read();
            }
            list.add(msg);
        }
        return list;
    }
}
