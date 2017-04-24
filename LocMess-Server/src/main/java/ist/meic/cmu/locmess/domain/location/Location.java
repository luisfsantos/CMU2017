package ist.meic.cmu.locmess.domain.location;

import java.sql.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by lads on 22/04/2017.
 */
@DatabaseTable(tableName = "locations")
public class Location {
    @DatabaseField(generatedId = true)
    int id;

    @DatabaseField(canBeNull = false)
    String name;
    
    @DatabaseField(canBeNull = false)
    Coordinate coordinates;
    
    @DatabaseField(canBeNull = false)
    Date date;
    
    @DatabaseField(canBeNull = false, foreign = true)
    String username;

    public Location(String name,String authorUsername ,Coordinate coordinates,Date date) {
        this.name = name;
        this.coordinates=coordinates;
        this.username=authorUsername;
        this.date=date;
    }

    public Location() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public Coordinate getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinate coordinates) {
		this.coordinates = coordinates;
	}
	
	public CoordinateType getType(){
		return this.coordinates.getType();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
