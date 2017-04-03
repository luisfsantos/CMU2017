package ist.meic.cmu.locmess_client.location;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


    private final List<Location> mLocations;

    public LocationsAdapter(List<Location> locations) {
        mLocations = locations;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_location, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Location location = mLocations.get(position);
        holder.mName.setText(location.name);

        if (location.coordinates instanceof GpsCoordinates) {
            holder.mCoordinatesIcon.setImageResource(R.drawable.ic_gps);
        } else if (location.coordinates instanceof WifiCoordinates) {
            holder.mCoordinatesIcon.setImageResource(R.drawable.ic_wifi);
        }
        holder.mCoordinates.setText(location.coordinates.toString(holder.mCoordinates.getContext()));
        holder.mAuthor.setText(location.author);
        holder.mDate.setText(location.getFormattedDate());
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

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView)itemView.findViewById(R.id.location_name);
            mCoordinates = (TextView)itemView.findViewById(R.id.location_coordinates);
            mAuthor = (TextView)itemView.findViewById(R.id.location_author);
            mDate = (TextView)itemView.findViewById(R.id.location_create_date);
            mCoordinatesIcon = (ImageView)itemView.findViewById(R.id.coordinates_ic);
        }
    }
}
