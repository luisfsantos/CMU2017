package ist.meic.cmu.locmess_client.network.request_builders;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;

/**
 * Created by Catarina on 23/04/2017.
 */

public class GpsLocationRequestBuilder implements RequestBuilder {
    private final String name;
    private final double latitude;
    private final double longitude;
    private final double radius;
    private final String date_created;
    Gson gson;

    public GpsLocationRequestBuilder(String name, String date, double latitude, double longitude, double radius) {
        this.name = name;
        this.date_created = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        gson = new Gson();
    }

    @Override
    public RequestData build(String url, int requestMethod) throws MalformedURLException {
        return new RequestData(url, requestMethod, buildJson());
    }

    private String buildJson()  {
        JsonObjectAPI json = new JsonObjectAPI();
        JsonObject data = new JsonObject();
        data.addProperty(NAME, name);
        data.addProperty(CREATION_DATE, date_created);
        JsonObject coordinate = new JsonObject();
        coordinate.addProperty(TYPE, TYPE_GPS);
        coordinate.addProperty(LATITUDE, latitude);
        coordinate.addProperty(LONGITUDE, longitude);
        coordinate.addProperty(RADIUS, radius);
        data.add(COORDINATE, coordinate);
        json.setData(data);
        return gson.toJson(json);
    }
}
