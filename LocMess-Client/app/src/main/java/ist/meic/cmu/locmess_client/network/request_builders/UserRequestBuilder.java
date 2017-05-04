package ist.meic.cmu.locmess_client.network.request_builders;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.RequestData;

/**
 * Created by Catarina on 22/04/2017.
 */

public class UserRequestBuilder implements RequestBuilder {
    private final String username;
    private final String password;
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
        JsonObject data = new JsonObject();
        data.addProperty(USERNAME, username);
        data.addProperty(PASSWORD, password);
        return gson.toJson(data);
    }
}
