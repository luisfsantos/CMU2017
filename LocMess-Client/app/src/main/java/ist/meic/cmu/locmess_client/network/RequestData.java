package ist.meic.cmu.locmess_client.network;

import android.annotation.SuppressLint;

import java.net.URL;

/**
 * Created by Catarina on 21/04/2017.
 */

public class RequestData {
    public static final int GET = 1;
    public static final int POST = 2;

    private URL url;
    private int requestMethod;
    //FIXME add map for actual request data

    @SuppressLint("DefaultLocale")
    public RequestData(URL url, int requestMethod) {
        this.url = url;
        if (requestMethod == GET || requestMethod == POST) {
            this.requestMethod = requestMethod;
        } else {
            throw new IllegalArgumentException(String.format("Expected GET (%d) or POST (%d), but received %d", GET, POST, requestMethod));
        }
    }

    public URL getUrl() {

        return url;
    }

    public int getRequestMethod() {
        return requestMethod;
    }

    //TODO method that transforms map into json object
}
