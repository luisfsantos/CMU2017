package ist.meic.cmu.locmess.api.json.wrappers;

import ist.meic.cmu.locmess.domain.location.CoordinateType;
import ist.meic.cmu.locmess.domain.location.Location;

/**
 * Created by lads on 22/04/2017.
 */
public class LocationWrapper {
    String name;
    CoordinateWrapper coordinates;


    public LocationWrapper(String name, CoordinateWrapper coordinates) {
        this.name = name;
        this.coordinates = coordinates;
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
        //TODO return an actual location
        return new Location(name);
    }
}
