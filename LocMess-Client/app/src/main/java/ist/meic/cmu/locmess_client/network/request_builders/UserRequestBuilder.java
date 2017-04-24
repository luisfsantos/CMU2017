package ist.meic.cmu.locmess_client.network.request_builders;

import com.google.gson.JsonObject;

import org.json.JSONException;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;

/**
 * Created by Catarina on 22/04/2017.
 */

public class UserRequestBuilder implements RequestBuilder {
    private String username;
    private String password;

    public UserRequestBuilder(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public RequestData build(String url, int requestMethod) throws MalformedURLException {
        return new RequestData(url, requestMethod, buildJson());
    }

    private JsonObjectAPI buildJson() {
        JsonObjectAPI json = new JsonObjectAPI();
        JsonObject data = new JsonObject();
        data.addProperty(USERNAME, username);
        data.addProperty(PASSWORD, password);
        json.setData(data);
        return json;
    }
}
