package ist.meic.cmu.locmess_client.network.request_builders;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;

/**
 * Created by Catarina on 28/04/2017.
 */

public class UpdateLocationRequestBuilder implements RequestBuilder {
    Gson gson;

    public UpdateLocationRequestBuilder(/*FIXME add params*/){
        gson = new Gson();
        // TODO: 28/04/2017
    }

    @Override
    public RequestData build(String url, int requestMethod) throws MalformedURLException {
        return new RequestData(url, requestMethod, buildJson());
    }

    private String buildJson() {
        JsonObjectAPI json = new JsonObjectAPI();
        JsonObject data = new JsonObject();

        // TODO: 03/05/2017
        json.setData(data);
        return gson.toJson(json);
    }
}
