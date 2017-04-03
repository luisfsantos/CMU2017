package ist.meic.cmu.locmess_client.data;

import android.os.Parcel;
import android.os.Parcelable;

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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lat: ")
            .append(latitude)
            .append(", Long: ")
            .append(longitude);
        return sb.toString();
    }

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
}
