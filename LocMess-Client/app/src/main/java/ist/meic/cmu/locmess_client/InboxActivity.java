package ist.meic.cmu.locmess_client;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ist.meic.cmu.locmess_client.data.LocMessage;

public class InboxActivity extends AppCompatActivity {

    private static List<LocMessage> DUMMY_DATASET;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DUMMY_DATASET = createDummyData(12);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.inbox_card_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CardAdapter(DUMMY_DATASET, R.layout.available_msg_card);
        mRecyclerView.setAdapter(mAdapter);

    }

    private List<LocMessage> createDummyData(int size) {
        List<LocMessage> list = new ArrayList<>();
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
