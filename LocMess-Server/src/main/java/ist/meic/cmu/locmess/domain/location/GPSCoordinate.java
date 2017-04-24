package ist.meic.cmu.locmess.domain.location;

public class GPSCoordinate extends Coordinate{
	
	double lon;
	double lat;
	double radius;
	
	public GPSCoordinate (){
		
	}
	

	public GPSCoordinate (double lon,double lat,double radius){
		this.lon=lon;
		this.lat=lat;
		this.radius=radius;
	}


	public double getLon() {
		return lon;
	}


	public void setLon(double lon) {
		this.lon = lon;
	}


	public double getLat() {
		return lat;
	}


	public void setLat(double lat) {
		this.lat = lat;
	}


	public double getRadius() {
		return radius;
	}


	public void setRadius(float radius) {
		this.radius = radius;
	}

}
