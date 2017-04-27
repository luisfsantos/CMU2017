package ist.meic.cmu.locmess.api.json.wrappers;

import ist.meic.cmu.locmess.domain.location.Location;

import java.util.Date;

/**
 * Created by lads on 22/04/2017.
 */
public class LocationWrapper {
    long id;
    String author;
    String name;
    CoordinateWrapper coordinates;
    Date date;

    public LocationWrapper(String name, CoordinateWrapper coordinates, Date date) {
        this.name = name;
        this.coordinates = coordinates;
        this.date=date;
    }

    public LocationWrapper(Location location) {
        id = location.getId();
        author = location.getAuthor().getUsername();
        name = location.getName();
        coordinates = new CoordinateWrapper(location.getCoordinates());
        date = location.getDate();
    }

    public LocationWrapper() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CoordinateWrapper getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinateWrapper coordinates) {
        this.coordinates = coordinates;
    }

    public Location createLocation(String username) {
        return new Location(this.name, username, this.coordinates.createCoordinate(), this.date);
    }

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
