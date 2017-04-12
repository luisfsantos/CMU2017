package ist.meic.cmu.locmess_client.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;

/**
 * Created by Catarina on 02/04/2017.
 */

public class KeyPairsAdapter extends RecyclerView.Adapter<KeyPairsAdapter.ViewHolder> {

    Context mContext;
    CursorAdapter mCursorAdapter;
    RemoveKeyPairListener mListener;

    public interface RemoveKeyPairListener {
        void removeKeyPair(String key, String value);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton mRemoveBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mRemoveBtn = (ImageButton)itemView.findViewById(R.id.remove_btn);
        }
    }

    public KeyPairsAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursorAdapter = new CursorAdapter(mContext, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                return LayoutInflater.from(context)
                        .inflate(R.layout.item_key_pair, viewGroup, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView key = (TextView) view.findViewById(R.id.key);
                key.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(LocMessDBContract.KeyPair.COLUMN_KEY)
                ));
                TextView value = (TextView) view.findViewById(R.id.value);
                value.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(LocMessDBContract.KeyPair.COLUMN_VALUE)
                ));
            }
        };
        try {
            mListener = (RemoveKeyPairListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RemoveKeyPairListener");
        }
    }

    public void changeCursor(Cursor cursor) {
        mCursorAdapter.changeCursor(cursor);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());

        holder.mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursorAdapter.getCursor().moveToPosition(position);
                showRemoveDialog(mCursorAdapter.getCursor());
            }
        });
    }

    private void showRemoveDialog(Cursor cursor) {
        final String key = cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.KeyPair.COLUMN_KEY));
        final String value = cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.KeyPair.COLUMN_VALUE));
        StringBuilder dialogMessage = new StringBuilder();

        dialogMessage.append(mContext.getString(R.string.remove_dialog_message_start))
                .append(" \"")
                .append(key)
                .append(": ")
                .append(value)
                .append("\"");

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.remove_keypair)
                .setMessage(dialogMessage.toString())
                .setPositiveButton(R.string.remove_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.removeKeyPair(key, value);
                    }
                }).setNegativeButton(R.string.cancel, null);
        builder.show();
    }
}
