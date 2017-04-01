package ist.meic.cmu.locmess_client.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Catarina on 29/03/2017.
 */

public class LocMessage implements Parcelable {
    public String author;
    public String title;
    public String text;
    public Date time;
    public String location;
    private boolean read;

    public LocMessage(String author, String title, String text, Date time, String location) {
        this.author = author;
        this.title = title;
        this.text = text;
        this.time = time;
        this.location = location;
        this.read = false;
    }

    protected LocMessage(Parcel in) {
        author = in.readString();
        title = in.readString();
        text = in.readString();
        try {
            time = SimpleDateFormat.getInstance().parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        location = in.readString();
        read = in.readByte() != 0;
    }

    public static final Creator<LocMessage> CREATOR = new Creator<LocMessage>() {
        @Override
        public LocMessage createFromParcel(Parcel in) {
            return new LocMessage(in);
        }

        @Override
        public LocMessage[] newArray(int size) {
            return new LocMessage[size];
        }
    };

    public void read() {
        read = true;
    }

    public boolean isRead() {
        return read;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(title);
        parcel.writeString(text);
        parcel.writeString(SimpleDateFormat.getInstance().format(time));
        parcel.writeString(location);
        parcel.writeByte((byte) (read ? 1 : 0));
    }
}
