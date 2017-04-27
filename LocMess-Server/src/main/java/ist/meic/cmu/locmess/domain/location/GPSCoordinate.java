package ist.meic.cmu.locmess.domain.location;

import com.j256.ormlite.field.DatabaseField;

public class GPSCoordinate extends Coordinate{
	@DatabaseField
	double longitude;
	@DatabaseField
	double latitude;
	@DatabaseField
	double radius;
	
	public GPSCoordinate (){
		this.type = CoordinateType.GPS;
	}
	

	public GPSCoordinate (double longitude, double latitude, double radius){
		this();
		this.longitude = longitude;
		this.latitude = latitude;
		this.radius=radius;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
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
		double distance = distance(this.getLatitude(),coord1.getLatitude(),this.getLongitude(),coord1.getLongitude(),0.0,0.0);
		
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
