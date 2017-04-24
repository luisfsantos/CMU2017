package ist.meic.cmu.locmess_client.network;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;


/**
 * Created by Catarina on 21/04/2017.
 */

public class RequestData {
    public static final int GET = 1;
    public static final int POST = 2;

    private URL url;
    private int requestMethod;
    private JsonObjectAPI json;
    Gson gson = new Gson();

    @SuppressLint("DefaultLocale")
    public RequestData(String url, int requestMethod, JsonObjectAPI json) throws MalformedURLException {
        this.url = new URL(url);
        if (requestMethod == GET || requestMethod == POST) {
            this.requestMethod = requestMethod;
        } else {
            throw new IllegalArgumentException(String.format("Expected GET (%d) or POST (%d), but received %d", GET, POST, requestMethod));
        }
        this.json = json;
    }

    public URL getUrl() {
        return url;
    }

    public int getRequestMethod() {
        return requestMethod;
    }

    public JsonObjectAPI getJson() {
        return json;
    }

    public String getJsonAsString() {
        return gson.toJson(json);
    }
}
