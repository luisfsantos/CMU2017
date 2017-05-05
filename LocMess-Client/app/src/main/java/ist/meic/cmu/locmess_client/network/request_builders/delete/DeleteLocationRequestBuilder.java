package ist.meic.cmu.locmess_client.network.request_builders.delete;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;

/**
 * Created by lads on 05/05/2017.
 */

public class DeleteLocationRequestBuilder implements RequestBuilder {

    private final String url;

    public DeleteLocationRequestBuilder(int serverID) {
        this.url = LocMessURL.DELETE_LOCATION.replace(LocMessURL.ID, String.valueOf(serverID));
    }

    @Override
    public RequestData build(String url, @RequestData.RequestMethod int requestMethod) throws MalformedURLException {
        return new RequestData(this.url, requestMethod, null);
    }
}
