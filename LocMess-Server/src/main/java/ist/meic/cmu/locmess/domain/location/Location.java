package ist.meic.cmu.locmess.domain.location;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by lads on 22/04/2017.
 */
@DatabaseTable(tableName = "locations")
public class Location {
    @DatabaseField(generatedId = true)
    int id;

    @DatabaseField()
    String name;

    public Location(String name) {
        this.name = name;
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
}
