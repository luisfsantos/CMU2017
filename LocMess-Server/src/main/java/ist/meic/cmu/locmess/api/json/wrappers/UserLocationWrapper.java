package ist.meic.cmu.locmess.api.json.wrappers;

public class UserLocationWrapper {
	// {"coordinates": {"latitude":45.43 , "longitude":"22.423}, "ssid": ["eduroam", "home"]}
	GPSCoordinateWapper coordinates;
	WIFICoordinateWrapper ssid;

	public UserLocationWrapper() {
		
	}
	public UserLocationWrapper(GPSCoordinateWapper coordinates,WIFICoordinateWrapper ssid) {
		this.coordinates=coordinates;
		this.ssid=ssid;
	
	}
	public GPSCoordinateWapper getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(GPSCoordinateWapper coordinates) {
		this.coordinates = coordinates;
	}
	public WIFICoordinateWrapper getSsid() {
		return ssid;
	}
	public void setSsid(WIFICoordinateWrapper ssid) {
		this.ssid = ssid;
	}

}
