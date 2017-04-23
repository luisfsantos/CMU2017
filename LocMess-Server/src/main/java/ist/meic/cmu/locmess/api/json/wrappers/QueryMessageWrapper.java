package ist.meic.cmu.locmess.api.json.wrappers;

import ist.meic.cmu.locmess.domain.location.Location;
import ist.meic.cmu.locmess.domain.users.User;

public class QueryMessageWrapper {
	User user;
	Location location;

	public QueryMessageWrapper() {
	}

	public QueryMessageWrapper(User user, Location location) {
		this.user=user;
		this.location=location;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
