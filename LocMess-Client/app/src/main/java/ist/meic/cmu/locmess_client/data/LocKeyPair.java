package ist.meic.cmu.locmess_client.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Catarina on 02/04/2017.
 */

public class LocKeyPair implements Parcelable{
    public String key;
    public String value;

    public LocKeyPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    protected LocKeyPair(Parcel in) {
        key = in.readString();
        value = in.readString();
    }

    public static final Creator<LocKeyPair> CREATOR = new Creator<LocKeyPair>() {
        @Override
        public LocKeyPair createFromParcel(Parcel in) {
            return new LocKeyPair(in);
        }

        @Override
        public LocKeyPair[] newArray(int size) {
            return new LocKeyPair[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(value);
    }
}
