package ist.meic.cmu.locmess.api.json.wrappers;

import java.sql.Date;

import ist.meic.cmu.locmess.domain.location.Location;
import ist.meic.cmu.locmess.domain.message.Message;
import ist.meic.cmu.locmess.domain.users.User;

public class MessageWrapper {
	User author;
	String title;
	String text;
	Date fromDate;
	Date toDate;
	Location location;

	public MessageWrapper() {
	}

	public MessageWrapper(User author, String title, String text, Date fromDate, Date toDate, Location location) {
		this.author = author;
		this.title = title;
		this.text = text;
		this.fromDate = fromDate;
		this.toDate = toDate;
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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Message createMessage() {
		// TODO return an actual location
		return new Message(this.author, this.title, this.text, this.fromDate, this.toDate, this.location, null);
	}

}
