package ist.meic.cmu.locmess.api.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ist.meic.cmu.locmess.api.json.Error;
import ist.meic.cmu.locmess.api.json.JsonListAPI;
import ist.meic.cmu.locmess.api.json.JsonObjectAPI;
import ist.meic.cmu.locmess.api.json.wrappers.LocationWrapper;
import ist.meic.cmu.locmess.database.Settings;
import ist.meic.cmu.locmess.domain.location.Coordinate;
import ist.meic.cmu.locmess.domain.location.GPSCoordinate;
import ist.meic.cmu.locmess.domain.location.Location;
import jwtAuthentication.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.Authenticator;
import java.util.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by lads on 22/04/2017.
 */
@RestController
@RequestMapping("/location")
public class LocationController extends AuthenticatedController{
    private final static Logger logger = Logger.getLogger(UserController.class.getName());
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<JsonObjectAPI> create(@RequestBody JsonObjectAPI locationInfo) {
        JsonObjectAPI response = new JsonObjectAPI();
        Location location;
        LocationWrapper newLocation = gson.fromJson(locationInfo.getData(), LocationWrapper.class);
        logger.info("Data: " + locationInfo.getData().toString());
        logger.info("There is a location " + newLocation.getName() + " being created");
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource(Settings.DB_URI);

            Dao<Location, String> locationDAO = DaoManager.createDao(connectionSource, Location.class);
            TableUtils.createTableIfNotExists(connectionSource, Location.class);
            location = newLocation.createLocation(getUser());
            locationDAO.create(location);
            connectionSource.close();

        } catch (SQLException e) {
            response.addError(new Error(2, "Location could not be created please try again later"));
            e.printStackTrace();
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        JsonObject data = new JsonObject();
        data.addProperty("code", 0);
        data.addProperty("status", "Location " + newLocation.getName() + " created");
        data.add("location", gson.toJsonTree(new LocationWrapper(location), LocationWrapper.class));
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/test/create", method = RequestMethod.GET)
    public ResponseEntity<JsonObjectAPI> createTest() {
        JsonObjectAPI response = new JsonObjectAPI();
        Location location = new Location();
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource(Settings.DB_URI);

            Dao<Location, String> locationDAO = DaoManager.createDao(connectionSource, Location.class);
            Dao<Coordinate, String> coordinateDao = DaoManager.createDao(connectionSource, Coordinate.class);
            TableUtils.createTableIfNotExists(connectionSource, Location.class);
            TableUtils.createTableIfNotExists(connectionSource, Coordinate.class);
            Coordinate coordinate = new Coordinate(Double.valueOf(22.1), Double.valueOf(23.1), Double.valueOf(23.1));
            coordinateDao.create(coordinate);
            location = new Location("arco", getUser(), coordinate, new Date());
            locationDAO.create(location);
            connectionSource.close();

        } catch (SQLException e) {
            response.addError(new Error(2, "Location could not be created please try again later"));
            e.printStackTrace();
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        JsonObject data = new JsonObject();
        data.add("location", gson.toJsonTree(location, Location.class));
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET,produces ="application/json")
    public ResponseEntity<JsonListAPI> list() {
    	 JsonListAPI response = new JsonListAPI();;
    	logger.info("request locations");
    	List<Location> locations=null;
    	try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource(Settings.DB_URI);

            Dao<Location, String> locationDAO = DaoManager.createDao(connectionSource, Location.class);
            TableUtils.createTableIfNotExists(connectionSource, Location.class);
            locations = locationDAO.queryForAll();
            connectionSource.close();

        } catch (SQLException e) {
            response.addError(new Error(2, "Locations could not be listed please try again later"));
            e.printStackTrace();
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    	JsonArray data = new JsonArray();
        for (Location l: locations) {
            data.add(gson.toJsonTree(new LocationWrapper(l), LocationWrapper.class));
        }
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
