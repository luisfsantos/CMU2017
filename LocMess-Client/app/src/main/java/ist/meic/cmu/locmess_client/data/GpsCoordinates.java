package ist.meic.cmu.locmess_client.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by Catarina on 03/04/2017.
 */

public class GpsCoordinates extends Coordinates implements Parcelable {

    double latitude;
    double longitude;
    float radius;

    public GpsCoordinates(double latitude, double longitude, float radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    protected GpsCoordinates(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        radius = in.readFloat();
    }

    public static final Creator<GpsCoordinates> CREATOR = new Creator<GpsCoordinates>() {
        @Override
        public GpsCoordinates createFromParcel(Parcel in) {
            return new GpsCoordinates(in);
        }

        @Override
        public GpsCoordinates[] newArray(int size) {
            return new GpsCoordinates[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeFloat(radius);
    }

    @Override
    public String toString(Context context) {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

        return context.getString(R.string.latitude_abrev) +
                ": " +
                df.format(latitude) +
                ", " +
                context.getString(R.string.longitude_abrev) +
                ": " +
                df.format(longitude) +
                ", " +
                context.getString(R.string.radius) +
                ": " +
                df.format(radius) +
                context.getString(R.string.distance_unit_abrev);
    }
}
