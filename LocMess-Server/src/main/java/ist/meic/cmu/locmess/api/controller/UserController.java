package ist.meic.cmu.locmess.api.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ist.meic.cmu.locmess.database.Settings;
import ist.meic.cmu.locmess.domain.users.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * Created by lads on 06-04-2017.
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

        @RequestMapping(value = "/create", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
        public User create(@RequestParam(value="username", defaultValue="World") String username, @RequestParam(value="password", defaultValue="password") String password) {
            User user = new User();
            try {
                ConnectionSource connectionSource =
                        new JdbcConnectionSource(Settings.DB_URI);

                Dao<User, String> userDAO = DaoManager.createDao(connectionSource, User.class);
                TableUtils.createTableIfNotExists(connectionSource, User.class);
                if (userDAO.idExists(username)) {
                    return user;
                }
                user = new User(username, username, password);
                userDAO.create(user);
                connectionSource.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return user;
        }
}
