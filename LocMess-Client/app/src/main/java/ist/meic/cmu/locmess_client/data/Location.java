package ist.meic.cmu.locmess_client.data;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;
import ist.meic.cmu.locmess_client.utils.CoordinatesUtils;

/**
 * Created by Catarina on 15/05/2017.
 */

public class Location {
    private int id;
    private String name;
    private Date creation_date;
    private String author;
    private JsonObject coordinate; // FIXME: 15/05/2017 make this something other than json object

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
            Log.wtf("LocationDeserializer", "Unknown coordinate type: " + coordinateType);
            return null;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreationDate(Date creation_date) {
        this.creation_date = creation_date;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCoordinate(JsonObject coordinate) {
        this.coordinate = coordinate;
    }
}

