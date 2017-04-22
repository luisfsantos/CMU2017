package ist.meic.cmu.locmess.api.controller;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import ist.meic.cmu.locmess.api.json.Error;
import ist.meic.cmu.locmess.api.json.JsonObjectAPI;
import ist.meic.cmu.locmess.api.json.wrappers.LocationWrapper;
import ist.meic.cmu.locmess.api.json.wrappers.MessageWrapper;
import ist.meic.cmu.locmess.database.Settings;
import ist.meic.cmu.locmess.domain.location.Location;
import ist.meic.cmu.locmess.domain.message.Message;

@RestController
@RequestMapping("/message")
public class MessageControler {
	private final static Logger logger = Logger.getLogger(UserController.class.getName());
	Gson gson = new Gson();

	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<JsonObjectAPI> create(@RequestBody JsonObjectAPI messageInfo) {
        JsonObjectAPI response = new JsonObjectAPI();
        MessageWrapper newMessage = gson.fromJson(messageInfo.getData(), MessageWrapper.class);
        logger.info("Data: " + messageInfo.getData().toString());
        logger.info("There is a message " + newMessage.getTitle() + " from User "+ newMessage.getAuthor().getUsername() +" in the location "+newMessage.getLocation().getName());
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource(Settings.DB_URI);
            //TODO: see what happens when foirgen keys do not exist
            Dao<Message, String> messageDAO = DaoManager.createDao(connectionSource, Message.class);
            TableUtils.createTableIfNotExists(connectionSource, Location.class);
            messageDAO.create(newMessage.createMessage());
            connectionSource.close();

        } catch (SQLException e) {
        	
            response.addError(new Error(2, "Message could not be created please try again later"));
            e.printStackTrace();
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        JsonObject data = new JsonObject();
        data.addProperty("code", 0);
        data.addProperty("status", "Message " + newMessage.getTitle() + "created");
        response.setData(data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
