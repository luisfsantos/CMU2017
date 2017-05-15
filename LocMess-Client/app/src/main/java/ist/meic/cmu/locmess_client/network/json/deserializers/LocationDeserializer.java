package ist.meic.cmu.locmess_client.network.json.deserializers;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ist.meic.cmu.locmess_client.data.Location;
import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;

/**
 * Created by Catarina on 04/05/2017.
 */

public class LocationDeserializer {

    private static Gson gson = new GsonBuilder()
            .setDateFormat(RequestBuilder.DATE_FORMAT)
            .create();

    public Location parse(JsonObject location) {
        return gson.fromJson(location, Location.class);
    }

    public SparseArray<Location> parseAll(JsonArray locations) {
        SparseArray<Location> locationsMap = new SparseArray<>();
        for (JsonElement element : locations) {
            Location location = gson.fromJson(element, Location.class);
            locationsMap.put(location.getId(), location);
        }
        return locationsMap;
    }
}
