package ist.meic.cmu.locmess.domain.location;

import java.util.Set;

public class WIFICoordinate extends Coordinate{

	 Set<String> wifiSSIDs;
	public WIFICoordinate(){
		
	}
	public WIFICoordinate( Set<String> wifiSSIDs){
		 this.wifiSSIDs= wifiSSIDs;
	}
	public Set<String> getWifiSSIDs() {
		return wifiSSIDs;
	}
	public void setWifiSSIDs(Set<String> wifiSSIDs) {
		this.wifiSSIDs = wifiSSIDs;
	}
	
}
