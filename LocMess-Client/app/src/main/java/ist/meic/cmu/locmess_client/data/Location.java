package ist.meic.cmu.locmess_client.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Catarina on 03/04/2017.
 */

public class Location implements Parcelable{

    public String name;
    public String author;
    public Date dateCreated;
    // FIXME: 03/04/2017 fix coordinates
    public Coordinates coordinates;

    public Location(String name, Coordinates coordinates, String author, Date dateCreated) {
        this.name = name;
        this.author = author;
        this.dateCreated = dateCreated;
        this.coordinates = coordinates;
    }

    protected Location(Parcel in) {
        name = in.readString();
        author = in.readString();
        try {
            dateCreated = SimpleDateFormat.getInstance().parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        coordinates = in.readParcelable(Coordinates.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(SimpleDateFormat.getInstance().format(dateCreated));
        dest.writeParcelable(coordinates, flags);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
