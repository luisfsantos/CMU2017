package ist.meic.cmu.locmess_client.messages.inbox;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.messages.ShowMessageActivity;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.DateUtils;
import ist.meic.cmu.locmess_client.utils.recycler.LocMessRecyclerView;
import ist.meic.cmu.locmess_client.utils.recycler.SimpleCursorRecyclerAdapter;

/**
 * Created by Catarina on 30/03/2017.
 */
public class AvailableTabFragment extends Fragment implements
        SimpleCursorRecyclerAdapter.CursorRecyclerAdapterCallback, LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "AvailableTabFragment";

    private static final int AVAILABLE_MESSAGES_LOADER_ID = R.id.available_messages_loader_id;
    SimpleCursorRecyclerAdapter mAdapter;
    LocMessRecyclerView mRecyclerView;
    TextView mUpdatedTime;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(AVAILABLE_MESSAGES_LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setAdapter(mAdapter);
        getActivity().getSupportLoaderManager().restartLoader(AVAILABLE_MESSAGES_LOADER_ID, null, this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.recyclerview_layout, container, false);
        mRecyclerView = (LocMessRecyclerView) rootView.findViewById(R.id.rv_card_list);
        TextView mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(mEmptyView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        setupAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mUpdatedTime = (TextView) rootView.findViewById(R.id.last_updated);
        return rootView;
    }

    private void setupAdapter() {
        String[] uiBindFrom = {
                LocMessDBContract.AvailableMessages.COLUMN_TITLE,
                LocMessDBContract.AvailableMessages.COLUMN_CONTENT,
                LocMessDBContract.AvailableMessages.COLUMN_LOCATION,
                LocMessDBContract.AvailableMessages.COLUMN_AUTHOR,
                LocMessDBContract.AvailableMessages.COLUMN_DATE_POSTED
        };
        int[] uiBindTo = {
                R.id.post_title,
                R.id.post_text,
                R.id.post_location,
                R.id.post_author,
                R.id.post_time
        };
        mAdapter = new SimpleCursorRecyclerAdapter(R.layout.card_available_msg, null, uiBindFrom, uiBindTo);
        mAdapter.setViewHolderCallback(this);
    }

    @Override
    public void onAttachToViewHolder(View itemView) {
        Cursor cursor = mAdapter.getCursor();
        final int position = cursor.getPosition();
        String dbDate = cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.AvailableMessages.COLUMN_DATE_POSTED));
        ((TextView)itemView.findViewById(R.id.post_time))
                .setText(DateUtils.formatDateTimeDbToLocale(dbDate));
        boolean isRead = cursor.getInt(cursor.getColumnIndexOrThrow(LocMessDBContract.AvailableMessages.COLUMN_READ))
                == LocMessDBContract.AvailableMessages.MESSAGE_READ;
        if (!isRead) {
            ((TextView)itemView.findViewById(R.id.post_author))
                    .setTypeface(null, Typeface.BOLD);
            ((TextView)itemView.findViewById(R.id.post_title))
                    .setTypeface(null, Typeface.BOLD);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecyclerCardClicked(position);
            }
        });
    }

    public void onRecyclerCardClicked(int position) {
        final Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);

        ContentValues values = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(c, values);

        Handler handler = new Handler();
        OpenMessageRunnable runnable = new OpenMessageRunnable(getContext(), values);
        handler.post(runnable);

        ShowMessageActivity.Message message = new ShowMessageActivity.Message(
                values.getAsString(LocMessDBContract.AvailableMessages.COLUMN_AUTHOR),
                values.getAsString(LocMessDBContract.AvailableMessages.COLUMN_TITLE),
                values.getAsString(LocMessDBContract.AvailableMessages.COLUMN_CONTENT),
                DateUtils.formatDateTimeDbToLocale(
                        values.getAsString(LocMessDBContract.AvailableMessages.COLUMN_DATE_POSTED)),
                values.getAsString(LocMessDBContract.AvailableMessages.COLUMN_LOCATION));
        Intent intent = new Intent(getContext(), ShowMessageActivity.class);

        intent.putExtra(ShowMessageActivity.INTENT_MESSAGE, message);
        startActivity(intent);
    }

    private class OpenMessageRunnable implements Runnable {
        private Context mContext;
         ContentValues message;

        public OpenMessageRunnable(Context mContext, ContentValues values) {
            this.mContext = mContext;
            this.message = values;
        }

        @Override
        public void run() {

            int localID = message.getAsInteger(LocMessDBContract.AvailableMessages._ID);
            String title = message.getAsString(LocMessDBContract.AvailableMessages.COLUMN_TITLE);
            String text = message.getAsString(LocMessDBContract.AvailableMessages.COLUMN_CONTENT);
            String author = message.getAsString(LocMessDBContract.AvailableMessages.COLUMN_AUTHOR);
            String datePosted = message.getAsString(LocMessDBContract.AvailableMessages.COLUMN_DATE_POSTED);
            String location = message.getAsString(LocMessDBContract.AvailableMessages.COLUMN_LOCATION);
            boolean isRead = message.getAsInteger(LocMessDBContract.AvailableMessages.COLUMN_READ)
                    == LocMessDBContract.AvailableMessages.MESSAGE_READ;

            if (!isRead) {
                // if message is not read, then we store it in Opened Messages table
                ContentValues values = new ContentValues();
                values.put(LocMessDBContract.OpenedMessages.COLUMN_TITLE, title);
                values.put(LocMessDBContract.OpenedMessages.COLUMN_CONTENT, text);
                values.put(LocMessDBContract.OpenedMessages.COLUMN_AUTHOR, author);
                values.put(LocMessDBContract.OpenedMessages.COLUMN_LOCATION, location);
                values.put(LocMessDBContract.OpenedMessages.COLUMN_DATE_POSTED, datePosted);
                Uri uri = mContext.getContentResolver().insert(LocMessDBContract.OpenedMessages.CONTENT_URI, values);
                Log.d(TAG, "Opened message registered with URI: " + uri);
            }

            // register the selected available message as Read
            boolean isP2p = message.get(LocMessDBContract.AvailableP2pMessages.COLUMN_P2P_ID) != null;
            boolean isCentralized = message.get(LocMessDBContract.AvailableMessages.COLUMN_SERVER_ID) != null;
            ContentValues values = new ContentValues();
            Uri uri = Uri.EMPTY;
            if (isCentralized && !isP2p) {
                uri = ContentUris.withAppendedId(LocMessDBContract.AvailableMessages.CONTENT_URI, localID);
                values.put(LocMessDBContract.AvailableMessages.COLUMN_READ, LocMessDBContract.AvailableMessages.MESSAGE_READ);
            } else if (isP2p && !isCentralized) {
                uri = ContentUris.withAppendedId(LocMessDBContract.AvailableP2pMessages.CONTENT_URI, localID);
                values.put(LocMessDBContract.AvailableP2pMessages.COLUMN_READ, LocMessDBContract.AvailableP2pMessages.MESSAGE_READ);
            }
            mContext.getContentResolver().update(uri, values, null, null);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Loading available messages. Loader ID: " + AVAILABLE_MESSAGES_LOADER_ID);
        return new CursorLoader(
                getContext(),
                LocMessDBContract.AvailableMessages.CONTENT_URI_WITH_P2P,
                null,
                null,
                null,
                LocMessDBContract.AvailableMessages.COLUMN_DATE_POSTED + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
        mRecyclerView.setAdapter(mAdapter);
        SharedPreferences pref = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String date = pref.getString(getString(R.string.pref_time_last_updated_msg), null);
        if (date != null) {
            mUpdatedTime.setText(getString(R.string.time_last_updated_inbox, date));
            mUpdatedTime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
