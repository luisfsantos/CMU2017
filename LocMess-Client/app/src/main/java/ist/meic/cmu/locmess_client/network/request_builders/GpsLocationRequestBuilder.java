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
    private String name;
    private double latitude;
    private double longitude;
    private double radius;
    Gson gson;

    public GpsLocationRequestBuilder(String name, double latitude, double longitude, double radius) {
        this.name = name;
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
        data.addProperty(TYPE, TYPE_GPS);
        data.addProperty(LATITUDE, latitude);
        data.addProperty(LONGITUDE, longitude);
        data.addProperty(RADIUS, radius);
        json.setData(data);
        return gson.toJson(json);
    }
}
