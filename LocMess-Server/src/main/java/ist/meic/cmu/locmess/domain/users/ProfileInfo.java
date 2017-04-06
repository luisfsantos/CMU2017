package ist.meic.cmu.locmess.domain.users;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by lads on 06/04/2017.
 */
@DatabaseTable(tableName = "profile_information")
public class ProfileInfo {

    @DatabaseField(canBeNull = false, foreign = true)
    Key key;
    @DatabaseField()
    String info;

    public ProfileInfo() {
    }

    public ProfileInfo(Key key, String info) {
        this.key = key;
        this.info = info;
    }


    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
