package ist.meic.cmu.locmess_client.data;

import android.content.ContentValues;

import java.util.Date;

import ist.meic.cmu.locmess_client.sql.LocMessDBContract;

/**
 * Created by Catarina on 15/05/2017.
 */

public class Message {
    Integer id;
    String title;
    String text;
    Date fromDate;
    Date toDate;
    Location location;
    String author;

    public Message() {}

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }


    public String getAuthor() {
        return author;
    }

    public Location getLocation() {
        return location;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
