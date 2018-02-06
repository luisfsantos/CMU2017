package ist.meic.cmu.locmess.domain.message;

import java.sql.Date;
import java.util.Collection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import ist.meic.cmu.locmess.domain.location.Location;
import ist.meic.cmu.locmess.domain.users.Key;
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
	Date fromDate;
	@DatabaseField()
	Date toDate;
	@DatabaseField(canBeNull = false, foreign = true)
	Location location;
	@ForeignCollectionField(eager = false)
	Collection<Key> keys;

	public Message() {
	}

	public Message(User author, String title, String text, Date fromDate, Date toDate, Location location,
			Collection<Key> keys) {
		this.author = author;
		this.title = title;
		this.text = text;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.location = location;
		this.keys = keys;
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

	public Collection<Key> getKeys() {
		return keys;
	}

	public void setKeys(Collection<Key> keys) {
		this.keys = keys;
	}

	public boolean vasibleTime() {
		long time = System.currentTimeMillis();
		return ((fromDate.getTime() <= time) && (time <= toDate.getTime()));

	}

}