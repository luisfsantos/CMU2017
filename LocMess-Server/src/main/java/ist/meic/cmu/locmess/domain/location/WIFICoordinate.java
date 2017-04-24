package ist.meic.cmu.locmess.domain.location;

import java.util.List;

public class WIFICoordinate extends Coordinate{

	List<String> ssids;
	public WIFICoordinate(){
		
	}
	public WIFICoordinate(List<String> ssids){
		this.ssids=ssids;
	}
	public List<String> getSsid() {
		return this.ssids;
	}
	public void setSsid(List<String> ssids) {
		this.ssids = ssids;
	}
	public void addSsid(String ssid){
		this.ssids.add(ssid);
	}
}
