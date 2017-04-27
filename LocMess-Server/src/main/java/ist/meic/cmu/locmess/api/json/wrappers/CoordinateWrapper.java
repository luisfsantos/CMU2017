package ist.meic.cmu.locmess.api.json.wrappers;

import ist.meic.cmu.locmess.domain.location.Coordinate;
import ist.meic.cmu.locmess.domain.location.CoordinateType;
import ist.meic.cmu.locmess.domain.location.GPSCoordinate;
import ist.meic.cmu.locmess.domain.location.WIFICoordinate;

import java.util.Set;

/**
 * Created by lads on 22/04/2017.
 */
public class CoordinateWrapper {
    CoordinateType type;
    Double latitude;
    Double longitude;
    Double radius;
    Set<String> wifiSSIDs;

    public CoordinateWrapper() {
    }

    public CoordinateWrapper(Coordinate coordinates) {
        type = coordinates.getType();
        if (type.equals(CoordinateType.GPS)) {
            latitude = ((GPSCoordinate)coordinates).getLatitude();
            longitude = ((GPSCoordinate)coordinates).getLongitude();
            radius = ((GPSCoordinate)coordinates).getRadius();
        } else if (type.equals(CoordinateType.WIFI)) {
            wifiSSIDs = ((WIFICoordinate)coordinates).getWifiSSIDs();
        }
    }


    public Set<String> getWifiSSIDs() {
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
            return new GPSCoordinate(this.latitude,this.longitude,this.radius);
        else if (type == CoordinateType.WIFI)
            return new WIFICoordinate(this.wifiSSIDs);
        return null;
    }

}
