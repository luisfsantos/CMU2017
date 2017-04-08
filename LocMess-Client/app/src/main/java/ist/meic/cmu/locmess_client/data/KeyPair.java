package ist.meic.cmu.locmess_client.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Catarina on 02/04/2017.
 */

public class KeyPair implements Parcelable{
    public String key;
    public String value;

    public KeyPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    protected KeyPair(Parcel in) {
        key = in.readString();
        value = in.readString();
    }

    public static final Creator<KeyPair> CREATOR = new Creator<KeyPair>() {
        @Override
        public KeyPair createFromParcel(Parcel in) {
            return new KeyPair(in);
        }

        @Override
        public KeyPair[] newArray(int size) {
            return new KeyPair[size];
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

    @Override
    public String toString() {
        return key + ":" + value;
    }
}
