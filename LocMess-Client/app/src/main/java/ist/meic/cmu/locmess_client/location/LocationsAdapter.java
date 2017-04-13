package ist.meic.cmu.locmess_client.location;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.CoordinatesUtils;

/**
 * Created by Catarina on 03/04/2017.
 */

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    static final int TAG_NAME = R.id.tag_name;
    static final int TAG_ID = R.id.tag_id;

    Context mContext;
    CursorAdapter mCursorAdapter;
    private LocationCardListener mListener;

    public interface LocationCardListener {
        void postToLocation(int id);
        void removeLocation(int id);
    }

    public LocationsAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursorAdapter = new CursorAdapter(context, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context)
                        .inflate(R.layout.card_location, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_NAME));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                ((TextView)view.findViewById(R.id.location_name))
                        .setText(name);
                ((TextView)view.findViewById(R.id.location_author))
                        .setText(cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_AUTHOR)));
                ((TextView)view.findViewById(R.id.location_create_date))
                        .setText(cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_DATE_CREATED)));
                bindCoordinates(view, context, cursor);

                ImageButton removeBtn = (ImageButton)view.findViewById(R.id.remove_btn);
                removeBtn.setTag(TAG_NAME, name);
                removeBtn.setTag(TAG_ID, id);
                ImageButton postToBtn = (ImageButton)view.findViewById(R.id.new_msg_btn);
                postToBtn.setTag(TAG_ID, id);

            }

            private void bindCoordinates(View view, Context context, Cursor cursor) {
                String dbCoordinates = cursor.getString(
                        cursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_COORDINATES));
                CoordinatesUtils.Coordinates coordinates = new CoordinatesUtils(context, dbCoordinates).parse();
                ImageView coordinatesIcon = (ImageView)view.findViewById(R.id.coordinates_ic);

                Drawable d = null;
                if (coordinates instanceof CoordinatesUtils.WifiCoordinates) {
                    d = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_wifi, null);
                } else if (coordinates instanceof CoordinatesUtils.GpsCoordinates) {
                    d = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_gps, null);
                }
                d = DrawableCompat.wrap(d);
                DrawableCompat.setTint(d, ContextCompat.getColor(context, R.color.icon_tint_dark));
                coordinatesIcon.setImageDrawable(d);

                ((TextView)view.findViewById(R.id.location_coordinates))
                        .setText(coordinates.toString());
            }
        };
        try {
            mListener = (LocationCardListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LocationCardListener");
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());
        holder.mNewMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.postToLocation((int)view.getTag(TAG_ID));
            }
        });
        holder.mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveDialog(view);
            }
        });
    }
    private void showRemoveDialog(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.remove_location)
                .setMessage(mContext.getString(R.string.remove_dialog_message_start) +
                        " \"" + view.getTag(TAG_NAME) + "\"")
                .setPositiveButton(R.string.remove_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.removeLocation((int)view.getTag(TAG_ID));
                    }
                }).setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mCoordinates;
        private TextView mAuthor;
        private TextView mDate;
        private ImageView mCoordinatesIcon;
        private ImageButton mNewMessageBtn;
        private ImageButton mRemoveBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView)itemView.findViewById(R.id.location_name);
            mCoordinates = (TextView)itemView.findViewById(R.id.location_coordinates);
            mAuthor = (TextView)itemView.findViewById(R.id.location_author);
            mDate = (TextView)itemView.findViewById(R.id.location_create_date);
            mCoordinatesIcon = (ImageView)itemView.findViewById(R.id.coordinates_ic);
            mNewMessageBtn = (ImageButton)itemView.findViewById(R.id.new_msg_btn);
            mRemoveBtn = (ImageButton)itemView.findViewById(R.id.remove_btn);
        }
    }
}
