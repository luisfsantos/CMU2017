package ist.meic.cmu.locmess_client.network.json.deserializers;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;

import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;

/**
 * Created by Catarina on 07/05/2017.
 */

public class MessageDeserializer {

    private static Gson gson = new GsonBuilder()
            .setDateFormat(RequestBuilder.DATE_FORMAT)
            .create();

    public Message parse(JsonObject message) {
        return gson.fromJson(message, Message.class);
    }

    public SparseArray<Message> parseAll(JsonArray messages) {
        SparseArray<Message> messagesMap = new SparseArray<>();
        for (JsonElement element : messages) {
            Message message = gson.fromJson(element, Message.class);
            messagesMap.put(message.getId(), message);
        }
        return messagesMap;
    }

    public class Message {
        int id;
        String title;
        String text;
        Date fromDate;
        Date toDate;
        LocationDeserializer.Location location;
        String author;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getText() {
            return text;
        }


        public String getAuthor() {
            return author;
        }

        public LocationDeserializer.Location getLocation() {
            return location;
        }

        public Date getFromDate() {
            return fromDate;
        }

        public Date getToDate() {
            return toDate;
        }
    }
}
