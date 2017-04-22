package ist.meic.cmu.locmess.api.json.wrappers;

import java.sql.Date;

import com.j256.ormlite.field.DatabaseField;

import ist.meic.cmu.locmess.domain.location.Location;
import ist.meic.cmu.locmess.domain.message.Message;
import ist.meic.cmu.locmess.domain.users.User;

public class MessageWrapper {
	User author;
	String title;
	String text;
	Date time;
	Location location;

	public MessageWrapper() {
	}

	public MessageWrapper(User author, String title, String text, Date time, Location location) {
		this.author = author;
		this.title = title;
		this.text = text;
		this.time = time;
		this.location = location;
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

	public Message createMessage() {
		// TODO return an actual location
		return new Message(this.author, this.title, this.text, this.time, this.location);
	}

}
