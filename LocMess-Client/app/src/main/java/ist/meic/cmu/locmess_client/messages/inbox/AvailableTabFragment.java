package ist.meic.cmu.locmess_client.messages.inbox;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(AVAILABLE_MESSAGES_LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setAdapter(mAdapter);
        getActivity().getSupportLoaderManager().initLoader(AVAILABLE_MESSAGES_LOADER_ID, null, this);
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
        final Cursor cursor = mAdapter.getCursor();
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
                onRecyclerCardClicked(cursor.getPosition());
            }
        });
    }

    public void onRecyclerCardClicked(int position) {
        final Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);

        int localID = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.AvailableMessages._ID));
        String title = c.getString(c.getColumnIndexOrThrow(LocMessDBContract.AvailableMessages.COLUMN_TITLE));
        String text = c.getString(c.getColumnIndexOrThrow(LocMessDBContract.AvailableMessages.COLUMN_CONTENT));
        String author = c.getString(c.getColumnIndexOrThrow(LocMessDBContract.AvailableMessages.COLUMN_AUTHOR));
        String datePosted = c.getString(c.getColumnIndexOrThrow(LocMessDBContract.AvailableMessages.COLUMN_DATE_POSTED));
        String location = c.getString(c.getColumnIndexOrThrow(LocMessDBContract.AvailableMessages.COLUMN_LOCATION));
        boolean isRead = c.getInt(c.getColumnIndexOrThrow(LocMessDBContract.AvailableMessages.COLUMN_READ))
                == LocMessDBContract.AvailableMessages.MESSAGE_READ;

        Handler handler = new Handler();
        OpenMessageRunnable runnable = new OpenMessageRunnable(getContext(),
                localID, title, text, author, datePosted, location, isRead);
        handler.post(runnable);

        ShowMessageActivity.Message message = new ShowMessageActivity.Message(
                author, title, text, DateUtils.formatDateTimeDbToLocale(datePosted), location);
        Intent intent = new Intent(getContext(), ShowMessageActivity.class);

        intent.putExtra(ShowMessageActivity.INTENT_MESSAGE, message);
        startActivity(intent);
    }

    private class OpenMessageRunnable implements Runnable {
        private Context mContext;
        final int localID;
        final String title;
        final String text;
        final String author;
        final String datePosted;
        final String location;
        final boolean isRead;

        public OpenMessageRunnable(Context mContext, int localID, String title, String text,
                                   String author, String datePosted, String location, boolean isRead) {
            this.mContext = mContext;
            this.localID = localID;
            this.title = title;
            this.text = text;
            this.author = author;
            this.datePosted = datePosted;
            this.location = location;
            this.isRead = isRead;
        }

        @Override
        public void run() {

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
            Uri uri = ContentUris.withAppendedId(LocMessDBContract.AvailableMessages.CONTENT_URI, localID);
            ContentValues values = new ContentValues();
            values.put(LocMessDBContract.AvailableMessages.COLUMN_READ, LocMessDBContract.AvailableMessages.MESSAGE_READ);
            mContext.getContentResolver().update(uri, values, null, null);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Loading available messages. Loader ID: " + AVAILABLE_MESSAGES_LOADER_ID);
        String[] queryCols = new String[] {
                LocMessDBContract.AvailableMessages._ID,
                LocMessDBContract.AvailableMessages.COLUMN_TITLE,
                LocMessDBContract.AvailableMessages.COLUMN_CONTENT,
                LocMessDBContract.AvailableMessages.COLUMN_LOCATION,
                LocMessDBContract.AvailableMessages.COLUMN_AUTHOR,
                LocMessDBContract.AvailableMessages.COLUMN_DATE_POSTED,
                LocMessDBContract.AvailableMessages.COLUMN_READ,
                LocMessDBContract.COLUMN_SERVER_ID
        };
        return new CursorLoader(getContext(),
                LocMessDBContract.AvailableMessages.CONTENT_URI,
                queryCols,                  // the projection fields
                null,                       // the selection criteria
                null,                       // the selection args
                LocMessDBContract.AvailableMessages.COLUMN_DATE_POSTED + " DESC"   // the sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
