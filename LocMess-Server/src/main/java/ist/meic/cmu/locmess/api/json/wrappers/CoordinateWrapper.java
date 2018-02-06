package ist.meic.cmu.locmess.api.json.wrappers;

import ist.meic.cmu.locmess.domain.location.*;

import java.util.Collection;
import java.util.Set;

/**
 * Created by lads on 22/04/2017.
 */
public class CoordinateWrapper {
    CoordinateType type;
    Double latitude;
    Double longitude;
    Double radius;
    Collection<String> wifiSSIDs;

    public CoordinateWrapper() {
    }

    public CoordinateWrapper(Coordinate coordinates) {
        type = coordinates.getType();
        System.out.println("ola: " + coordinates.getLatitude() + coordinates.getLongitude() + coordinates.getRadius());
        if (type.equals(CoordinateType.GPS)) {
            latitude = coordinates.getLatitude();
            longitude = coordinates.getLongitude();
            radius = coordinates.getRadius();
        } else if (type.equals(CoordinateType.WIFI)) {
            wifiSSIDs = SSID.toStringCollection(coordinates.getWifiSSIDs());
        }
    }


    public Collection<String> getWifiSSIDs() {
        return wifiSSIDs;
    }

    public void setWifiSSIDs(Set<String> wifiSSIDs) {
        this.wifiSSIDs = wifiSSIDs;
    }

    public CoordinateType getType() {
        return type;
    }

    public void setType(CoordinateType type) {
        this.type = type;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Coordinate createCoordinate() {
        if (type == CoordinateType.GPS)
            return new Coordinate(this.latitude,this.longitude,this.radius);
        else if (type == CoordinateType.WIFI) {
            Coordinate coordinate = new Coordinate();
            coordinate.setWifiSSIDs(SSID.fromStringCollection(this.wifiSSIDs));
            return coordinate;
        }
        return null;
    }

}
