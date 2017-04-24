package ist.meic.cmu.locmess_client.network.request_builders;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.RequestData;

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

    public RequestData build(String url, int requestMethod) throws JSONException, MalformedURLException {
        return new RequestData(url, requestMethod, buildJson());
    }

    private JSONObject buildJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put(DATA, (new JSONObject())
                .put(USERNAME, username)
                .put(PASSWORD, password)
        );
        return obj;
    }
}
