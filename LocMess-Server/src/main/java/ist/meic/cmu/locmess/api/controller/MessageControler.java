package ist.meic.cmu.locmess.api.controller;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
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
import ist.meic.cmu.locmess.api.json.wrappers.MessageWrapper;
import ist.meic.cmu.locmess.api.json.wrappers.UserLocationWrapper;
import ist.meic.cmu.locmess.database.Settings;
import ist.meic.cmu.locmess.domain.location.CoordinateType;
import ist.meic.cmu.locmess.domain.location.GPSCoordinate;
import ist.meic.cmu.locmess.domain.location.Location;
import ist.meic.cmu.locmess.domain.location.WIFICoordinate;
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
            //TODO: see what happens when foirgen keys do not exist? SQLEXCEPTION?
            Dao<Message, String> messageDAO = DaoManager.createDao(connectionSource, Message.class);
            TableUtils.createTableIfNotExists(connectionSource, Message.class);
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
	@RequestMapping(value = "/list", method = RequestMethod.GET,produces ="application/json")
    public ResponseEntity<JsonListAPI> list(@RequestBody JsonObjectAPI queryInfo) {
		JsonListAPI response = new JsonListAPI();
		UserLocationWrapper query = gson.fromJson(queryInfo.getData(), UserLocationWrapper.class);
        logger.info("Data: " + queryInfo.getData().toString());
        JsonArray jArray = new JsonArray();
        try {
           
        	jArray=getMessages(query);
        } catch (SQLException e) {
        	
            response.addError(new Error(2, "Message could not be created please try again later"));
            e.printStackTrace();
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        response.setData(jArray);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	private JsonArray getMessages(UserLocationWrapper query) throws SQLException{
		 ConnectionSource connectionSource =new JdbcConnectionSource(Settings.DB_URI);
         Dao<Location, String> locationDAO = DaoManager.createDao(connectionSource, Location.class);
         Dao<Message, String> messageDAO = DaoManager.createDao(connectionSource, Message.class);
         JsonArray jArray = new JsonArray();
         GPSCoordinate gps = (GPSCoordinate) query.getCoordinates().createCoordinate();
         WIFICoordinate wifi = (WIFICoordinate) query.getCoordinates().createCoordinate();
         
         for(Location l: locationDAO){
        	 if(l.getType().equals(CoordinateType.GPS)){
        		if(l.getCoordinates().closeCoordinates(gps)){
        			jArray=getMessagesFromLocation(messageDAO,jArray,l);
        		}
        	 }else {
        		 if(l.getCoordinates().closeCoordinates(wifi)){
        			 jArray= getMessagesFromLocation(messageDAO,jArray,l);
         		}
        	 }
     	   
        }
        
         connectionSource.close();
         return jArray;
	}
	
	private JsonArray getMessagesFromLocation( Dao<Message, String> messageDAO,JsonArray jArray,Location l) throws SQLException{
		for (Message m:messageDAO.queryForEq("location", l)){
			//TODO: add check of user keys
			jArray.add(gson.toJson(m));
		}
		return jArray;
	}

}
