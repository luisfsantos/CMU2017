package ist.meic.cmu.locmess.api.json.wrappers;

import java.sql.Date;

import com.j256.ormlite.field.DatabaseField;

import ist.meic.cmu.locmess.domain.location.Coordinate;
import ist.meic.cmu.locmess.domain.location.CoordinateType;
import ist.meic.cmu.locmess.domain.location.Location;

/**
 * Created by lads on 22/04/2017.
 */
public class LocationWrapper {
    String name;
    CoordinateWrapper coordinates;
    Date date;
    String username;

    public LocationWrapper(String name,String authorUsername ,CoordinateWrapper coordinates,Date date) {
        this.name = name;
        this.coordinates = coordinates;
        this.date=date;
        this.username=authorUsername;
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

    public Location createLocation() {
       
        return new Location(this.name,this.username,this.coordinates.createCoordinate(),this.date);
    }

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
