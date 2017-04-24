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


	@Override
	public  boolean closeCoordinates( Coordinate c1) {
		if (!(c1 instanceof GPSCoordinate))
			//TODO: custom exception
			throw new NullPointerException("can only compare coordinates of the same type");
		GPSCoordinate coord1 = (GPSCoordinate)c1;
		double distance = distance(this.getLat(),coord1.getLat(),this.getLon(),coord1.getLon(),0.0,0.0);
		
		return distance < coord1.getRadius();
	}
	
	public static double distance(double lat1, double lat2, double lon1,
	        double lon2, double el1, double el2) {

	    final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(lat2 - lat1);
	    double lonDistance = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    double height = el1 - el2;

	    distance = Math.pow(distance, 2) + Math.pow(height, 2);

	    return Math.sqrt(distance);
	}

}
