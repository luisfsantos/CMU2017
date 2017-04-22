package ist.meic.cmu.locmess.api.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ist.meic.cmu.locmess.api.json.Error;
import ist.meic.cmu.locmess.api.json.JsonObjectAPI;
import ist.meic.cmu.locmess.api.json.wrappers.UserWrapper;
import ist.meic.cmu.locmess.database.Settings;
import ist.meic.cmu.locmess.domain.users.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by lads on 06-04-2017.
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {
        private final static Logger logger = Logger.getLogger(UserController.class.getName());
        Gson gson = new Gson();

        @RequestMapping(value = "/create", method = RequestMethod.POST)
        public ResponseEntity<JsonObjectAPI> create(@RequestBody JsonObjectAPI userInfo) {
            JsonObjectAPI response = new JsonObjectAPI();
            UserWrapper newUser = gson.fromJson(userInfo.getData(), UserWrapper.class);
            logger.info("Data: " + userInfo.getData().toString());
            logger.info("There is a user " + newUser.getUsername() + " being created");
            try {
                ConnectionSource connectionSource =
                        new JdbcConnectionSource(Settings.DB_URI);

                Dao<User, String> userDAO = DaoManager.createDao(connectionSource, User.class);
                TableUtils.createTableIfNotExists(connectionSource, User.class);
                if (userDAO.idExists(newUser.getUsername())) {
                    response.addError(new Error(1, "the username is taken"));
                    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
                }
                userDAO.create(newUser.createUser());
                connectionSource.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            JsonObject data = new JsonObject();
            data.addProperty("code", 0);
            data.addProperty("status", "User " + newUser.getUsername() + "created");
            response.setData(data);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<JsonObjectAPI> login(@RequestBody JsonObjectAPI userInfo) {
        JsonObjectAPI response = new JsonObjectAPI();
        UserWrapper userWrapper = gson.fromJson(userInfo.getData(), UserWrapper.class);
        logger.info(userWrapper.getUsername() + "is trying to login");
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource(Settings.DB_URI);

            Dao<User, String> userDAO = DaoManager.createDao(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            if (userDAO.idExists(userWrapper.getUsername())) {
                User user = userDAO.queryForId(userWrapper.getUsername());
                logger.info("found the user " +userWrapper.getUsername());
                if (user.validate(userWrapper.getPassword())){

                    JsonObject data = new JsonObject();
                    Algorithm algorithm = Algorithm.HMAC256("secret");
                    String token = JWT.create()
                            .withSubject(userWrapper.getUsername())
                            .withIssuer("auth0")
                            .sign(algorithm);


                    data.addProperty("jwt", token);
                    data.addProperty("code", 0);
                    data.addProperty("status", userWrapper.getUsername() + " is now logged in successfully.");
                    response.setData(data);


                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
            connectionSource.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException exception){
            //UTF-8 encoding not supported
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
        }

        response.addError(new Error(1, "the username or password is incorrect"));
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
