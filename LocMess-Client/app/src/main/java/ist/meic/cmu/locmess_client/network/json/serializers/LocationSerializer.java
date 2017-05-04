package ist.meic.cmu.locmess_client.network.json.serializers;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;
import ist.meic.cmu.locmess_client.utils.CoordinatesUtils;

/**
 * Created by Catarina on 04/05/2017.
 */

public class LocationSerializer {

    private static Gson gson = new GsonBuilder()
            .setDateFormat(RequestBuilder.DATE_FORMAT)
            .create();

    public Location parse(JsonObject location) {
        return gson.fromJson(location, Location.class);
    }

    public HashMap<Integer, Location> parseAll(JsonArray locations) {
        HashMap<Integer, Location> locationsHashMap = new HashMap<>();
        for (JsonElement element : locations) {
            Location location = gson.fromJson(element, Location.class);
            locationsHashMap.put(location.getId(), location);
        }
        return locationsHashMap;
    }

    public class Location {
        private int id;
        private String name;
        private Date creation_date;
        private String author;
        private JsonObject coordinate;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Date getCreationDate() {
            return creation_date;
        }

        public String getAuthor() {
            return author;
        }

        public String getCoordinatesDbFormat() {
            String coordinateType = coordinate.get(RequestBuilder.TYPE).getAsString();
            if (coordinateType.equals(RequestBuilder.TYPE_GPS)) {
                return CoordinatesUtils.formatGpsToDb(
                        coordinate.get(RequestBuilder.LATITUDE).getAsDouble(),
                        coordinate.get(RequestBuilder.LONGITUDE).getAsDouble(),
                        coordinate.get(RequestBuilder.RADIUS).getAsDouble());

            } else if (coordinateType.equals(RequestBuilder.TYPE_WIFI)) {
                Set<String> ssids = new HashSet<>();
                JsonArray jssids = coordinate.getAsJsonArray(RequestBuilder.SSIDS);
                for (JsonElement element : jssids) {
                    ssids.add(element.getAsJsonObject().get(RequestBuilder.NAME).getAsString());
                }
                return CoordinatesUtils.formatWifiToDb(ssids);
            } else {
                Log.wtf("LocationSerializer", "Unknown coordinate type: " + coordinateType);
                return null;
            }
        }



    }
}
