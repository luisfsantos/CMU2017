package ist.meic.cmu.locmess_client.data;

import android.content.Context;
import android.os.Parcel;

/**
 * Created by Catarina on 03/04/2017.
 */

public class WifiCoordinates extends Coordinates {
    String ssid;

    public WifiCoordinates(String id) {
        ssid = id;
    }

    protected WifiCoordinates(Parcel in) {
        ssid = in.readString();
    }

    public static final Creator<WifiCoordinates> CREATOR = new Creator<WifiCoordinates>() {
        @Override
        public WifiCoordinates createFromParcel(Parcel in) {
            return new WifiCoordinates(in);
        }

        @Override
        public WifiCoordinates[] newArray(int size) {
            return new WifiCoordinates[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ssid);
    }

    @Override
    public String toString(Context context) {
        return ssid;
    }
}
