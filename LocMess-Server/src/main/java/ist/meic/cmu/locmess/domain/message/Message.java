package ist.meic.cmu.locmess.domain.message;

import java.sql.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import ist.meic.cmu.locmess.domain.location.Location;
import ist.meic.cmu.locmess.domain.users.User;

/**
 * Created by pauloanjos on 22/04/2017.
 */
@DatabaseTable(tableName = "messages")
public class Message {

	 @DatabaseField(generatedId = true)
	    int id;
	 
	 @DatabaseField(canBeNull = false, foreign = true)
	 User author;
	 @DatabaseField()
     String title;
	 @DatabaseField()
     String text;
	 @DatabaseField()
     Date time;
	 @DatabaseField(canBeNull = false, foreign = true)
     Location location;
	 public Message() {
		}
	 
	public Message(User author,String title,String text,Date time,Location location) {
		this.author=author;
		this.title=title;
		this.text=text;
		this.time=time;
		this.location=location;
	}
	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
}
