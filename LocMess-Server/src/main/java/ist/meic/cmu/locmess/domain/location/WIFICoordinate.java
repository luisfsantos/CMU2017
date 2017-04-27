package ist.meic.cmu.locmess.domain.location;

import java.util.Set;

public class WIFICoordinate extends Coordinate{

	 Set<String> wifiSSIDs;
	public WIFICoordinate(){
		this.type = CoordinateType.WIFI;
	}
	
	public WIFICoordinate( Set<String> wifiSSIDs){
		this();
		this.wifiSSIDs= wifiSSIDs;
	}
	public Set<String> getWifiSSIDs() {
		return wifiSSIDs;
	}
	public void setWifiSSIDs(Set<String> wifiSSIDs) {
		this.wifiSSIDs = wifiSSIDs;
	}
	@Override
	public boolean closeCoordinates(Coordinate c1) {
		if (!(c1 instanceof WIFICoordinate))
			//TODO: custom exception
			throw new NullPointerException("can only compare coordinates of the same type");
		WIFICoordinate coord1= (WIFICoordinate)c1;
		for(String s : coord1.getWifiSSIDs()){
			if(this.getWifiSSIDs().contains(s))
				return true;
			continue;
		}
		return false;
	}
	
}
