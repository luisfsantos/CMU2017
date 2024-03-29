package ist.meic.cmu.locmess_client.messages.posted;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
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

import java.net.MalformedURLException;
import java.util.Date;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.authentication.AccountService;
import ist.meic.cmu.locmess_client.messages.ShowMessageActivity;
import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.request_builders.GenericDeleteRequestBuilder;
import ist.meic.cmu.locmess_client.network.sync.SyncUtils;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.DateUtils;
import ist.meic.cmu.locmess_client.utils.recycler.LocMessRecyclerView;
import ist.meic.cmu.locmess_client.utils.recycler.SimpleCursorRecyclerAdapter;

/**
 * Created by lads on 05/04/2017.
 */

public class PostedTabFragment extends Fragment implements SimpleCursorRecyclerAdapter.CursorRecyclerAdapterCallback,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "PostedTabFragment";
    private static final String KEY_LOADER_ID = "loader_id";
    private static final String KEY_LOADER_QUERY = "loader_query";
    private int MESSAGES_LOADER_ID;
    private String loaderQuerySelection;
    LocMessRecyclerView mRecyclerView;
    SimpleCursorRecyclerAdapter mAdapter;

    public static PostedTabFragment newInstance(int loaderId, String loaderQuery) {
        PostedTabFragment fragment = new PostedTabFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_LOADER_ID, loaderId);
        args.putString(KEY_LOADER_QUERY, loaderQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            MESSAGES_LOADER_ID = getArguments().getInt(KEY_LOADER_ID);
            loaderQuerySelection = getArguments().getString(KEY_LOADER_QUERY);
        }
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

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(MESSAGES_LOADER_ID, new Bundle(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setAdapter(mAdapter);
        getActivity().getSupportLoaderManager().restartLoader(MESSAGES_LOADER_ID, null, this);
    }

    private void setupAdapter() {
        String[] uiBindFrom = {
                LocMessDBContract.PostedMessages.COLUMN_TITLE,
                LocMessDBContract.PostedMessages.COLUMN_CONTENT,
                LocMessDBContract.PostedMessages.COLUMN_LOCATION,
                LocMessDBContract.PostedMessages.COLUMN_DATE_FROM,
                LocMessDBContract.PostedMessages.COLUMN_DATE_TO
        };
        int[] uiBindTo = {
                R.id.post_title,
                R.id.post_text,
                R.id.post_location,
                R.id.date_from,
                R.id.date_to
        };
        mAdapter = new SimpleCursorRecyclerAdapter(R.layout.card_posted_messages, null, uiBindFrom, uiBindTo);
        mAdapter.setViewHolderCallback(this);
    }

    private void onRecyclerCardClicked(int position) {
        Cursor c = mAdapter.getCursor();
        int prevPosition = c.getPosition();
        c.moveToPosition(position);
        final ContentValues values = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(c, values);

        AccountManager am = AccountManager.get(getActivity().getBaseContext());
        Account account = AccountService.getActiveAccount(am);
        assert account != null;
        String author = account.name;

        String title = values.getAsString(LocMessDBContract.PostedMessages.COLUMN_TITLE);
        String text = values.getAsString(LocMessDBContract.PostedMessages.COLUMN_CONTENT);
        String dateFrom = values.getAsString(LocMessDBContract.PostedMessages.COLUMN_DATE_FROM);
        String dateTo = values.getAsString(LocMessDBContract.PostedMessages.COLUMN_DATE_TO);
        String location = values.getAsString(LocMessDBContract.PostedMessages.COLUMN_LOCATION);

        String timeInterval = getString(R.string.from_date_prompt) + ": " + DateUtils.formatDateTimeDbToLocale(dateFrom)
                + "\n" + getString(R.string.to_date_prompt) + ": " + DateUtils.formatDateTimeDbToLocale(dateTo);

        ShowMessageActivity.Message message = new ShowMessageActivity.Message(
                author, title, text, timeInterval, location);

        Intent intent = new Intent(getContext(), ShowMessageActivity.class);
        intent.putExtra(ShowMessageActivity.INTENT_MESSAGE, message);
        startActivity(intent);

        c.moveToPosition(prevPosition);
    }

    private void removeMessage(final ContentValues values) {
        final int id = values.getAsInteger(LocMessDBContract.PostedMessages._ID);
        Uri uri = ContentUris.withAppendedId(LocMessDBContract.PostedMessages.CONTENT_URI, id);
        int count = getContext().getContentResolver().delete(uri, null, null);
        Log.d(TAG, "Deleted " + count + " row(s)");

        if (values.getAsInteger(LocMessDBContract.PostedMessages.COLUMN_POLICY)
                == LocMessDBContract.PostedMessages.POLICY_CENTRALIZED) {
            final int serverID = values.getAsInteger(LocMessDBContract.PostedMessages.COLUMN_UNIVERSAL_ID);
            try {
                RequestData data = new GenericDeleteRequestBuilder(serverID).build(LocMessURL.DELETE_MESSAGE, RequestData.DELETE);
                SyncUtils.push(getActivity().getBaseContext(), SyncUtils.DELETE_MESSAGE, data, null);
            } catch (MalformedURLException e) {
                Log.wtf(TAG, "Malformed URL: ", e);
            }
        }
    }

    @Override
    public void onAttachToViewHolder(View itemView) {
        Cursor cursor = mAdapter.getCursor();
        final int position = cursor.getPosition();
        final ContentValues values = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, values);

        String dbFrom = values.getAsString(LocMessDBContract.PostedMessages.COLUMN_DATE_FROM);
        String dbTo = values.getAsString(LocMessDBContract.PostedMessages.COLUMN_DATE_TO);
        ((TextView)itemView.findViewById(R.id.date_from))
                .setText(getString(R.string.from_date_prompt) + ": " + DateUtils.formatDateTimeDbToLocale(dbFrom));
        ((TextView)itemView.findViewById(R.id.date_to))
                .setText(getString(R.string.to_date_prompt) + ": " + DateUtils.formatDateTimeDbToLocale(dbTo));
        itemView.findViewById(R.id.remove_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showRemoveDialog(values);
                    }
                });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecyclerCardClicked(position);
            }
        });
    }

    private void showRemoveDialog(final ContentValues values) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.remove_message)
                .setMessage(R.string.unpost_message_info)
                .setPositiveButton(R.string.remove_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeMessage(values);
                    }
                }).setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Loading posted messages. Loader ID: " + MESSAGES_LOADER_ID);
        String[] queryCols = new String[] {
                LocMessDBContract.PostedMessages._ID,
                LocMessDBContract.PostedMessages.COLUMN_TITLE,
                LocMessDBContract.PostedMessages.COLUMN_CONTENT,
                LocMessDBContract.PostedMessages.COLUMN_LOCATION,
                LocMessDBContract.PostedMessages.COLUMN_DATE_FROM,
                LocMessDBContract.PostedMessages.COLUMN_DATE_TO,
                LocMessDBContract.PostedMessages.COLUMN_UNIVERSAL_ID,
                LocMessDBContract.PostedMessages.COLUMN_POLICY
        };
        String[] selectionArgs = { DateUtils.formatDateTimeLocaleToDb(new Date()) };
        return new CursorLoader(getContext(),
                LocMessDBContract.PostedMessages.CONTENT_URI,
                queryCols,                  // the projection fields
                loaderQuerySelection,       // the selection criteria
                selectionArgs,              // the selection args
                null                        // the sort order
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
