package ist.meic.cmu.locmess.domain.location;

public class GPSCoordinate extends Coordinate{
	
	double lon;
	double lat;
	
	public GPSCoordinate (){
		
	}
	

	public GPSCoordinate (double lon,double lat){
		this.lon=lon;
		this.lat=lat;
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

}
