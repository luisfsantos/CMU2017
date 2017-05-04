package ist.meic.cmu.locmess.domain.location;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import ist.meic.cmu.locmess.database.Settings;
import ist.meic.cmu.locmess.domain.users.User;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by lads on 22/04/2017.
 */
@DatabaseTable(tableName = "locations")
public class Location {
    @DatabaseField(generatedId = true)
    long id;

    @DatabaseField(canBeNull = false)
    String name;
    
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    Coordinate coordinates;

    @DatabaseField(canBeNull = false)
    Date date;
    
    @DatabaseField(canBeNull = false, foreign = true)
    User author;

    public Location(String name, User author ,Coordinate coordinates, Date date) {
        this.name = name;
        this.coordinates=coordinates;
        this.author=author;
        this.date=date;
    }

    public Location(String name, String author, Coordinate coordinates, Date date) {
        this.name = name;
        this.coordinates=coordinates;
        setAuthor(author);
        this.date=date;
    }

    public Location() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

    public void setAuthor(String username) {
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource(Settings.DB_URI);

            Dao<User, String> userDAO = DaoManager.createDao(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            if (userDAO.idExists(username)) {
                this.author = userDAO.queryForId(username);
            }
            connectionSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
}
