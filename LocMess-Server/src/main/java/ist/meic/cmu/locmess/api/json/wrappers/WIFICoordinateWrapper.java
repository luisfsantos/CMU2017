package ist.meic.cmu.locmess.api.json.wrappers;

import java.util.Set;

import ist.meic.cmu.locmess.domain.location.Coordinate;
import ist.meic.cmu.locmess.domain.location.WIFICoordinate;

/**
 * Created by lads on 22/04/2017.
 */
public class WIFICoordinateWrapper extends CoordinateWrapper{
    Set<String> wifiSSIDs;

    public WIFICoordinateWrapper(Set<String> wifiSSIDs) {
        this.wifiSSIDs = wifiSSIDs;
    }

    public WIFICoordinateWrapper() {
    }

    public Set<String> getWifiSSIDs() {
        return wifiSSIDs;
    }

    public void setWifiSSIDs(Set<String> wifiSSIDs) {
        this.wifiSSIDs = wifiSSIDs;
    }

	@Override
	public Coordinate createCoordinate() {
		
		return new WIFICoordinate(this.wifiSSIDs);
	}
}
