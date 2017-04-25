package ist.meic.cmu.locmess_client.network.request_builders;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;

/**
 * Created by Catarina on 22/04/2017.
 */

public class UserRequestBuilder implements RequestBuilder {
    private String username;
    private String password;
    Gson gson;

    public UserRequestBuilder(String username, String password) {
        this.username = username;
        this.password = password;
        gson = new Gson();
    }

    public RequestData build(String url, int requestMethod) throws MalformedURLException {
        return new RequestData(url, requestMethod, buildJson());
    }

    private String buildJson() {
        JsonObjectAPI json = new JsonObjectAPI();
        JsonObject data = new JsonObject();
        data.addProperty(USERNAME, username);
        data.addProperty(PASSWORD, password);
        json.setData(data);
        return gson.toJson(json);
    }
}
