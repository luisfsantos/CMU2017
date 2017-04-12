package ist.meic.cmu.locmess_client.location;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.Location;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.CoordinatesUtils;

/**
 * Created by Catarina on 03/04/2017.
 */

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    Context mContext;
    CursorAdapter mCursorAdapter;
    private LocationCardListener mListener;

    public interface LocationCardListener {
        void postToLocation(Location location);
        void removeLocation(Location location);
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
                ((TextView)view.findViewById(R.id.location_name))
                        .setText(cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_NAME)));
                ((TextView)view.findViewById(R.id.location_author))
                        .setText(cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_AUTHOR)));
                ((TextView)view.findViewById(R.id.location_create_date))
                        .setText(cursor.getString(cursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_DATE_CREATED)));
                bindCoordinates(view, context, cursor);
            }

            private void bindCoordinates(View view, Context context, Cursor cursor) {
                String dbCoordinates = cursor.getString(
                        cursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_COORDINATES));
                CoordinatesUtils.Coordinates coordinates = new CoordinatesUtils(context, dbCoordinates).parse();
                ImageView coordinatesIcon = (ImageView)view.findViewById(R.id.coordinates_ic);
                if (coordinates instanceof CoordinatesUtils.GpsCoordinates) {
                    coordinatesIcon.setImageResource(R.drawable.ic_gps);
                } else if (coordinates instanceof CoordinatesUtils.WifiCoordinates) {
                    coordinatesIcon.setImageResource(R.drawable.ic_wifi);
                }
                coordinatesIcon.setColorFilter(ContextCompat.getColor(context, R.color.icon_tint_dark));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    coordinatesIcon.setImageTintMode(PorterDuff.Mode.SRC_IN);
                }
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
//        holder.mNewMessageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mListener.postToLocation(location);
//            }
//        });
//        holder.mRemoveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = holder.getAdapterPosition();
////                showRemoveDialog(holder, position);
//            }
//        });
    }

//    private void showRemoveDialog(ViewHolder holder, final int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//        builder.setTitle(R.string.remove_location)
//                .setMessage(holder.itemView.getContext().getString(R.string.remove_dialog_message_start) +
//                        " \"" +
//                        holder.mName.getText() +
//                        "\"")
//                .setPositiveButton(R.string.remove_ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        mLocations.remove(position);
//                        notifyItemRemoved(position);
//                    }
//                }).setNegativeButton(R.string.cancel, null);
//        builder.show();
//    }

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
