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
    private String name;
    private List<String> ssids;
    Gson gson;

    public WifiLocationRequestBuilder(String name, List<String> ssids) {
        this.name = name;
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
        data.addProperty(TYPE, TYPE_WIFI);
        JsonArray jssids = new JsonArray();
        for (String ssid : ssids) {
            jssids.add(ssid);
        }
        data.add(SSIDS, jssids);
        json.setData(data);
        return gson.toJson(json);
    }
}
