package ist.meic.cmu.locmess_client.data;

import java.util.Date;

/**
 * Created by Catarina on 29/03/2017.
 */

public class LocMessage {
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

    public void read() {
        read = true;
    }

    public boolean isRead() {
        return read;
    }
}
