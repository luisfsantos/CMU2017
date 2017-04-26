package ist.meic.cmu.locmess_client.network;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Catarina on 21/04/2017.
 */

public class RequestData implements Serializable {
    public static final int GET = 1;
    public static final int POST = 2;

    private URL url;

    private String stringUrl;

    private int requestMethod;
    //    private JsonObjectAPI json;
    private String json;
    @SuppressLint("DefaultLocale")
    public RequestData(String url, int requestMethod, String json) throws MalformedURLException {
        this.stringUrl = url;
        this.url = new URL(url);
        if (requestMethod == GET || requestMethod == POST) {
            this.requestMethod = requestMethod;
        } else {
            throw new IllegalArgumentException(String.format("Expected GET (%d) or POST (%d), but received %d", GET, POST, requestMethod));
        }
        this.json = json;
    }

    public String getStringUrl() {
        return stringUrl;
    }

    public URL getUrl() {
        return url;
    }

    public int getRequestMethod() {
        return requestMethod;
    }

    public String getJson() {
        return json;
    }
}
