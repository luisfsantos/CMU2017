package ist.meic.cmu.locmess.domain.location;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public abstract class Coordinate {
	@DatabaseField(generatedId = true)
	private long id;
	@DatabaseField
	CoordinateType type;

	public Coordinate() {
	}

	public CoordinateType getType() {
		return type;
	}

	public void setType(CoordinateType type) {
		this.type = type;
	}
/**
 * 
 * @param c1
 * @return true if c1 is close to this location( not the other way around!) false otherwise
 */
	public abstract boolean closeCoordinates(Coordinate c1);
}
