package ist.meic.cmu.locmess_client.location;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.GpsCoordinates;
import ist.meic.cmu.locmess_client.data.Location;
import ist.meic.cmu.locmess_client.data.WifiCoordinates;

/**
 * Created by Catarina on 03/04/2017.
 */

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {


    private List<Location> mLocations;
    private LocationCardListener mListener;

    public interface LocationCardListener {
        void postToLocation(Location location);
    }

    public LocationsAdapter(List<Location> locations, LocationCardListener listener) {
        mLocations = locations;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_location, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Location location = mLocations.get(position);
        holder.mName.setText(location.name);

        if (location.coordinates instanceof GpsCoordinates) {
            holder.mCoordinatesIcon.setImageResource(R.drawable.ic_gps);
        } else if (location.coordinates instanceof WifiCoordinates) {
            holder.mCoordinatesIcon.setImageResource(R.drawable.ic_wifi);
        }
        holder.mCoordinatesIcon.setColorFilter(ContextCompat.getColor(holder.mCoordinates.getContext(), R.color.icon_tint_dark));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.mCoordinatesIcon.setImageTintMode(PorterDuff.Mode.SRC_IN);
        }
        holder.mCoordinates.setText(location.coordinates.toString(holder.mCoordinates.getContext()));
        holder.mAuthor.setText(location.author);
        holder.mDate.setText(location.getFormattedDate());
        holder.mNewMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.postToLocation(location);
            }
        });
        holder.mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                showRemoveDialog(holder, position);
            }
        });
    }

    private void showRemoveDialog(ViewHolder holder, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle(R.string.remove_location)
                .setMessage(holder.itemView.getContext().getString(R.string.remove_dialog_message_start) +
                        " \"" +
                        holder.mName.getText() +
                        "\"")
                .setPositiveButton(R.string.remove_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mLocations.remove(position);
                        notifyItemRemoved(position);
                    }
                }).setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return mLocations.size();
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
