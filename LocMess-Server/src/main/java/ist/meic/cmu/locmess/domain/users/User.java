package ist.meic.cmu.locmess.domain.users;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by lads on 06/04/2017.
 */


@DatabaseTable(tableName = "user_accounts")
public class User {

    @DatabaseField(id=true)
    String username;

    @DatabaseField()
    String name;

    @DatabaseField
    String password;

    public User() {
    }

    public User(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
