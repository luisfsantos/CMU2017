package ist.meic.cmu.locmess_client.network;

import android.annotation.SuppressLint;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Catarina on 21/04/2017.
 */

public class RequestData {
    public static final int GET = 1;
    public static final int POST = 2;

    private URL url;
    private int requestMethod;
    private JSONObject data;

    @SuppressLint("DefaultLocale")
    public RequestData(String url, int requestMethod, JSONObject data) throws MalformedURLException {
        this.url = new URL(url);
        if (requestMethod == GET || requestMethod == POST) {
            this.requestMethod = requestMethod;
        } else {
            throw new IllegalArgumentException(String.format("Expected GET (%d) or POST (%d), but received %d", GET, POST, requestMethod));
        }
        this.data = data;
    }

    public URL getUrl() {
        return url;
    }

    public int getRequestMethod() {
        return requestMethod;
    }

    public JSONObject getData() { return data; }
}
