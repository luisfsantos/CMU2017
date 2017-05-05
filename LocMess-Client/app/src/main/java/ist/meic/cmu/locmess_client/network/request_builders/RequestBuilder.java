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

    String KEY = "key";
    String VALUE = "value";

    String UPDATE_GPS = "gps";
    String UPDATE_WIFI = "wifi";
    String UPDATE_SSID = "ssid";
    String DATE = "date";

    RequestData build(String url, @RequestData.RequestMethod int requestMethod) throws MalformedURLException;
}
