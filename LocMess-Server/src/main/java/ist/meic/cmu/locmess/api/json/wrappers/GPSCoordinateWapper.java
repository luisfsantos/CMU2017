package ist.meic.cmu.locmess.api.json.wrappers;

import ist.meic.cmu.locmess.domain.location.Coordinate;
import ist.meic.cmu.locmess.domain.location.GPSCoordinate;

/**
 * Created by lads on 22/04/2017.
 */
public class GPSCoordinateWapper extends CoordinateWrapper {
    double latitude;
    double longitude;
    double radius;

    public GPSCoordinateWapper(double latitude, double longitude, double radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public GPSCoordinateWapper() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

	@Override
	public Coordinate createCoordinate() {
		return new GPSCoordinate(this.latitude,this.longitude,this.radius);
	}
}
