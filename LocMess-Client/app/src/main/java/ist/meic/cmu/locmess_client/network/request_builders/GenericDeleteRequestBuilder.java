package ist.meic.cmu.locmess_client.network.request_builders;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;

/**
 * Created by lads on 05/05/2017.
 */

public class GenericDeleteRequestBuilder implements RequestBuilder {

    private final int serverID;

    public GenericDeleteRequestBuilder(int serverID) {
        this.serverID = serverID;
    }

    @Override
    public RequestData build(String url, @RequestData.RequestMethod int requestMethod) throws MalformedURLException {
        String urlWithID = url.replace(LocMessURL.ID, String.valueOf(serverID));
        return new RequestData(urlWithID, requestMethod, null);
    }
}
