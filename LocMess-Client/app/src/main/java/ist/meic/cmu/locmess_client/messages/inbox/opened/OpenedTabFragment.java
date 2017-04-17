package ist.meic.cmu.locmess_client.messages.inbox.opened;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.messages.OnRecyclerCardClicked;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.DateUtils;
import ist.meic.cmu.locmess_client.utils.recycler.LocMessRecyclerView;
import ist.meic.cmu.locmess_client.utils.recycler.SimpleCursorRecyclerAdapter;

import static android.content.ContentValues.TAG;

/**
 * Created by Catarina on 30/03/2017.
 */

public class OpenedTabFragment extends Fragment implements OnRecyclerCardClicked,
    SimpleCursorRecyclerAdapter.CursorRecyclerAdapterCallback, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int OPENED_MESSAGES_LOADER_ID = R.id.opened_messages_loader_id;
    LocMessRecyclerView mRecyclerView;
    SimpleCursorRecyclerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(OPENED_MESSAGES_LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setAdapter(mAdapter);
        getActivity().getSupportLoaderManager().restartLoader(OPENED_MESSAGES_LOADER_ID, null, this);
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
                LocMessDBContract.OpenedMessages.COLUMN_TITLE,
                LocMessDBContract.OpenedMessages.COLUMN_CONTENT,
                LocMessDBContract.OpenedMessages.COLUMN_LOCATION,
                LocMessDBContract.OpenedMessages.COLUMN_AUTHOR,
                LocMessDBContract.OpenedMessages.COLUMN_DATE_POSTED
        };
        int[] uiBindTo = {
                R.id.post_title,
                R.id.post_text,
                R.id.post_location,
                R.id.post_author,
                R.id.post_time
        };
        mAdapter = new SimpleCursorRecyclerAdapter(R.layout.card_opened_msg, null, uiBindFrom, uiBindTo);
        mAdapter.setViewHolderCallback(this);
    }

    @Override
    public void onAttachToViewHolder(View itemView) {
        Cursor cursor = mAdapter.getCursor();
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(LocMessDBContract.OpenedMessages._ID));
        String dbDate = cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.OpenedMessages.COLUMN_DATE_POSTED));
        ((TextView)itemView.findViewById(R.id.post_time))
                .setText(DateUtils.formatDateTimeDbToLocale(dbDate));
        itemView.findViewById(R.id.remove_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showRemoveDialog(id);
                    }
                });
    }

    private void showRemoveDialog(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.remove_message)
                .setPositiveButton(R.string.remove_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeMessage(id);
                    }
                }).setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void removeMessage(int id) {
        Uri uri = ContentUris.withAppendedId(LocMessDBContract.OpenedMessages.CONTENT_URI, id);
        int count = getContext().getContentResolver().delete(uri, null, null);
        Log.d(TAG, "Removed " + count + " row(s)");
    }

    @Override
    public void onRecyclerCardClicked(View view) {
//        Intent intent = new Intent(getContext(), ShowMessageActivity.class);
//        intent.putExtra("message", message);
//        startActivity(intent);
        // TODO: 17/04/2017
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Loading opened messages. Loader ID: " + OPENED_MESSAGES_LOADER_ID);
        String[] queryCols = new String[] {
                LocMessDBContract.OpenedMessages._ID,
                LocMessDBContract.OpenedMessages.COLUMN_TITLE,
                LocMessDBContract.OpenedMessages.COLUMN_CONTENT,
                LocMessDBContract.OpenedMessages.COLUMN_LOCATION,
                LocMessDBContract.OpenedMessages.COLUMN_AUTHOR,
                LocMessDBContract.OpenedMessages.COLUMN_DATE_POSTED
        };
        return new CursorLoader(getContext(),
                LocMessDBContract.OpenedMessages.CONTENT_URI,
                queryCols,                  // the projection fields
                null,                       // the selection criteria
                null,                       // the selection args
                LocMessDBContract.OpenedMessages.COLUMN_DATE_POSTED + " DESC"   // the sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.changeCursor(null);
    }
}
