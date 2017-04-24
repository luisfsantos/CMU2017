package ist.meic.cmu.locmess_client.network.request_builders;

import org.json.JSONException;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.RequestData;

/**
 * Created by Catarina on 23/04/2017.
 */

public interface RequestBuilder {
    String DATA = "data";
    String STATUS = "status";
    String ERRORS = "errors";
    String MESSAGE = "message";
    String USERNAME = "username";
    String PASSWORD = "password";
    String NAME = "name";
    String COORDINATES = "coordinates";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String RADIUS = "radius";
    String TYPE = "type";

    RequestData build(String url, int requestMethod) throws JSONException, MalformedURLException;
}
