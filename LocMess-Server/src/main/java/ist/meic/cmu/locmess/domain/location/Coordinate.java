package ist.meic.cmu.locmess.domain.location;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.Set;

@DatabaseTable(tableName = "coordinates")
public class Coordinate {
	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField
	CoordinateType type;

	@ForeignCollectionField(eager = true)
	Collection<SSID> wifiSSIDs;

	@DatabaseField
	Double longitude;

	@DatabaseField
	Double latitude;

	@DatabaseField
	Double radius;

	public Coordinate() {
		this.type = CoordinateType.EMPTY;
	}

	public Coordinate(Collection<SSID> wifiSSIDs) {
		this.type = CoordinateType.WIFI;
		this.wifiSSIDs = wifiSSIDs;
	}

	public Coordinate(Double longitude, Double latitude, Double radius) {
		this.type = CoordinateType.GPS;
		this.longitude = longitude;
		this.latitude = latitude;
		this.radius = radius;
	}


	public CoordinateType getType() {
		return type;
	}

	public void setType(CoordinateType type) {
		this.type = type;
	}

	public void setType(String type) {
		this.type = CoordinateType.valueOf(type);
	}

	public Collection<SSID> getWifiSSIDs() {
		return wifiSSIDs;
	}

	public void setWifiSSIDs(Collection<SSID> wifiSSIDs) {
		this.wifiSSIDs = wifiSSIDs;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
