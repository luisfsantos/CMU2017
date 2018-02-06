package ist.meic.cmu.locmess.api.json.wrappers;

import java.util.Set;

public class UserLocationWrapper {
	// {"coordinates": {"latitude":45.43 , "longitude":"22.423}, "ssid": ["eduroam", "home"]}
	Set<CoordinateWrapper> coordinates;

	public Set<CoordinateWrapper> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Set<CoordinateWrapper> coordinates) {
		this.coordinates = coordinates;
	}

}
