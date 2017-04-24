package ist.meic.cmu.locmess.domain.location;

public abstract class Coordinate {
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
