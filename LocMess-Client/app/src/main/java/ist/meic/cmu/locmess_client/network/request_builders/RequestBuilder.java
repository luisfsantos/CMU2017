package ist.meic.cmu.locmess_client.network.request_builders;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.RequestData;

/**
 * Created by Catarina on 23/04/2017.
 */

public interface RequestBuilder {
    String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    String STATUS = "status";
    String TOKEN = "token";
    String USERNAME = "username";
    String PASSWORD = "password";
    String NAME = "name";
    String CREATION_DATE = "creation_date";
    String COORDINATE = "coordinate";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String RADIUS = "radius";
    String SSIDS = "wifiSSIDs";
    String TYPE = "type";
    String TYPE_GPS = "GPS";
    String TYPE_WIFI = "WIFI";


    RequestData build(String url, int requestMethod) throws MalformedURLException;
}
