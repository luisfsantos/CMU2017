package ist.meic.cmu.locmess.ui.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ist.meic.cmu.locmess.database.Settings;
import ist.meic.cmu.locmess.domain.users.User;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.SQLException;

@Controller
@RequestMapping("/test")
public class BaseController {

    private static int counter = 0;
    private static final String VIEW_INDEX = "index";
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BaseController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String welcome(ModelMap model) {

        model.addAttribute("message", "Welcome");
        model.addAttribute("counter", ++counter);
        logger.debug("[welcome] counter : {}", counter);

        // Spring uses InternalResourceViewResolver and return back index.jsp
        return VIEW_INDEX;

    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public String welcomeName(@PathVariable String name, ModelMap model) {

        model.addAttribute("message", "Welcome " + name);
        model.addAttribute("counter", ++counter);
        logger.debug("[welcomeName] counter : {}", counter);
        return VIEW_INDEX;

    }

    @RequestMapping(value = "/create/{name}/{password}", method = RequestMethod.GET)
    public String createUser (@PathVariable String name, @PathVariable String password, ModelMap model){
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource(Settings.DB_URI);

            Dao<User, String> userDAO = DaoManager.createDao(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            if (userDAO.idExists(name)) {
                model.addAttribute("message", "User: " + name + " exists");
            }

            userDAO.create(new User(name, name, password));
            connectionSource.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.addAttribute("message", "Yay we created user: " + name);
        model.addAttribute("counter", ++counter);
        return VIEW_INDEX;
    }

}
