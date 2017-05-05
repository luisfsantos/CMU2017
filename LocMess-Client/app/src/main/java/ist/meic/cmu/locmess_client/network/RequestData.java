package ist.meic.cmu.locmess_client.network;

import android.support.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Catarina on 21/04/2017.
 */

public class RequestData implements Serializable {
    public static final int GET = 1;
    public static final int POST = 2;
    public static final int DELETE = 3;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GET, POST, DELETE}) public @interface RequestMethod {}

    private URL url;

    private String stringUrl;

    private int requestMethod;
    //    private JsonObjectAPI json;
    private String json;

    public RequestData(String url, @RequestMethod int requestMethod, String json) throws MalformedURLException {
        this.stringUrl = url;
        this.url = new URL(url);
        this.requestMethod = requestMethod;
        this.json = json;
    }

    public String getStringUrl() {
        return stringUrl;
    }

    public URL getUrl() {
        return url;
    }

    public @RequestMethod int getRequestMethod() {
        return requestMethod;
    }

    public String getJson() {
        return json;
    }
}
