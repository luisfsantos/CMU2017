package ist.meic.cmu.locmess_client.network.request_builders;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;
import java.util.List;

import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;

/**
 * Created by Catarina on 26/04/2017.
 */

public class WifiLocationRequestBuilder implements RequestBuilder {
    private final String name;
    private final List<String> ssids;
    private final String date_created;
    Gson gson;

    public WifiLocationRequestBuilder(String name, String date, List<String> ssids) {
        this.name = name;
        this.date_created = date;
        this.ssids = ssids;
        gson = new Gson();
    }

    @Override
    public RequestData build(String url, int requestMethod) throws MalformedURLException {
        return new RequestData(url, requestMethod, buildJson());
    }

    private String buildJson() {
        JsonObjectAPI json = new JsonObjectAPI();
        JsonObject data = new JsonObject();
        data.addProperty(NAME, name);
        data.addProperty(CREATION_DATE, date_created);
        JsonObject coordinate = new JsonObject();
        coordinate.addProperty(TYPE, TYPE_WIFI);
        JsonArray jssids = new JsonArray();
        for (String ssid : ssids) {
            JsonObject jssid = new JsonObject();
            jssid.addProperty(NAME, ssid);
            jssids.add(jssid);
        }
        coordinate.add(SSIDS, jssids);
        data.add(COORDINATE, coordinate);
        json.setData(data);
        return gson.toJson(json);
    }
}
