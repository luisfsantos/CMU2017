package ist.meic.cmu.locmess_client.messages.inbox.available;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.Message;
import ist.meic.cmu.locmess_client.messages.OnRecyclerCardClicked;
import ist.meic.cmu.locmess_client.messages.ShowMessageActivity;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.DateUtils;
import ist.meic.cmu.locmess_client.utils.recycler.LocMessRecyclerView;

/**
 * Created by Catarina on 30/03/2017.
 */
public class AvailableTabFragment extends Fragment implements OnRecyclerCardClicked {
    private static final String TAG = "AvailableTabFragment";
    private static List<Message> DUMMY_DATASET;

    AvailableCardAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (DUMMY_DATASET == null) {
            DUMMY_DATASET = createDummyData(12);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            Message[] parcelables = (Message[]) savedInstanceState.getParcelableArray("available_messages");
            if (parcelables != null) {
                DUMMY_DATASET = new LinkedList<>(Arrays.asList(parcelables));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("available_messages", DUMMY_DATASET.toArray(new Message[DUMMY_DATASET.size()]));
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

        mAdapter = new AvailableCardAdapter(DUMMY_DATASET, this);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }


    private List<Message> createDummyData(int size) {
        List<Message> list = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            Message msg = new Message("catarina" + i, "Free pizza",
                    getString(R.string.lorem_ipsum),
                    new Date(),
                    "Arco do Cego"
            );
            list.add(msg);
        }
        return list;
    }

    @Override
    public void onRecyclerCardClicked(View view) {
        int position = (int)view.getTag();
        Message message = DUMMY_DATASET.get(position);

        registerOpenedMessage(message);
        message.read();
        mAdapter.notifyDataSetChanged();

        Intent intent = new Intent(getContext(), ShowMessageActivity.class);
        intent.putExtra("message", message);
        startActivity(intent);

    }

    private void registerOpenedMessage(Message message) {
        /* FIXME figure out later whether we'll keep using the Message class
         * or if the "current location" messages will end up being stored
         * in the database as well
         */

        //only write message to db if it's not been read yet
        if (!message.isRead()) {
            ContentValues values = new ContentValues();
            values.put(LocMessDBContract.OpenedMessages.COLUMN_TITLE, message.title);
            values.put(LocMessDBContract.OpenedMessages.COLUMN_CONTENT, message.text);
            values.put(LocMessDBContract.OpenedMessages.COLUMN_AUTHOR, message.author);
            values.put(LocMessDBContract.OpenedMessages.COLUMN_LOCATION, message.location);
            values.put(LocMessDBContract.OpenedMessages.COLUMN_DATE_POSTED, DateUtils.formatDateTimeLocaleToDb(message.time));
            Uri uri = getContext().getContentResolver().insert(LocMessDBContract.OpenedMessages.CONTENT_URI, values);
            Log.d(TAG, "Opened message registered with URI: " + uri);
        }
    }
}
