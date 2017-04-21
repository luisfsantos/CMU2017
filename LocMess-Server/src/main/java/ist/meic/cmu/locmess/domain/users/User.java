package ist.meic.cmu.locmess.domain.users;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.h2.security.SHA256;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by lads on 06/04/2017.
 */


@DatabaseTable(tableName = "user_accounts")
public class User {
    int SALT_SIZE = 20;

    @DatabaseField(id=true)
    String username;

    @DatabaseField()
    String name;

    @DatabaseField(dataType= DataType.BYTE_ARRAY )
    private byte[] password;

    @DatabaseField(dataType=DataType.BYTE_ARRAY )
    private byte[] password_salt = new byte[SALT_SIZE];

    public User() {
        makeSalt();
    }

    public User(String username, String name) {
        this.username = username;
        this.name = name;
        makePassword();
    }

    public User(String username, String name, String password) {
        this(username, name);
        setPassword(password);
    }

    private void makeSalt() {
        new SecureRandom().nextBytes(password_salt);
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

    public void setPassword(String password) {
        this.password = SHA256.getHashWithSalt(password.getBytes(), password_salt);
    }

    public void makePassword() {
        byte [] newPassword =  new byte[SALT_SIZE];
        new SecureRandom().nextBytes(newPassword);
        this.password = SHA256.getHashWithSalt(newPassword, password_salt);
    }

    public boolean validate(String password) {
        return Arrays.equals(SHA256.getHashWithSalt(password.getBytes(), password_salt), this.password);
    }
}
