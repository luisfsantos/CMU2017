package ist.meic.cmu.locmess_client.network.request_builders.create;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;

/**
 * Created by Catarina on 05/05/2017.
 */

public class NewKeypairRequestBuilder implements RequestBuilder {
    private final String key;
    private final String value;
    private Gson gson;

    public NewKeypairRequestBuilder(String key, String value) {
        this.key = key;
        this.value = value;
        this.gson = new Gson();
    }

    @Override
    public RequestData build(String url, @RequestData.RequestMethod int requestMethod) throws MalformedURLException {
        return new RequestData(url, requestMethod, buildJson());
    }

    private String buildJson() {
        JsonObjectAPI json = new JsonObjectAPI();
        JsonObject data = new JsonObject();
        data.addProperty(KEY, key);
        data.addProperty(VALUE, value);
        json.setData(data);
        return gson.toJson(json);
    }
}
