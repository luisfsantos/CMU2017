package ist.meic.cmu.locmess.domain.users;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by lads on 06/04/2017.
 */
@DatabaseTable(tableName = "keys_for_profile")
public class Key {
    @DatabaseField(id = true)
    String key;

    public Key() {
        
    }

    public Key(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
