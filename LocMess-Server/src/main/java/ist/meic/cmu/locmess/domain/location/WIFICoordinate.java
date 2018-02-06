package ist.meic.cmu.locmess.domain.location;

import java.util.Collection;
import java.util.Set;

public class WIFICoordinate extends Coordinate{
	Set<SSID> wifiSSIDs;

	public WIFICoordinate(){
		this.type = CoordinateType.WIFI;
	}
	
	public WIFICoordinate(Set<SSID> wifiSSIDs){
		this();
		this.wifiSSIDs= wifiSSIDs;
	}
	public Collection<SSID> getWifiSSIDs() {
		return wifiSSIDs;
	}
	public void setWifiSSIDs(Set<SSID> wifiSSIDs) {
		this.wifiSSIDs = wifiSSIDs;
	}

	public boolean closeCoordinates(Coordinate c1) {
		if (!(c1 instanceof WIFICoordinate))
			//TODO: custom exception
			throw new NullPointerException("can only compare coordinates of the same type");
		WIFICoordinate coord1= (WIFICoordinate)c1;
		for(SSID s : coord1.getWifiSSIDs()){
			if(this.getWifiSSIDs().contains(s))
				return true;
			continue;
		}
		return false;
	}
	
}
