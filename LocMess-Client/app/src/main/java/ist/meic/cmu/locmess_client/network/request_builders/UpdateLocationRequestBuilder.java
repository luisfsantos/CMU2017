package ist.meic.cmu.locmess_client.network.request_builders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.utils.DateUtils;

/**
 * Created by Catarina on 28/04/2017.
 */

public class UpdateLocationRequestBuilder implements RequestBuilder {
    private final double latitude;
    private final double longitude;
    private final Set<String> ssids;
    private final Date date;
    private Gson gson;

    public UpdateLocationRequestBuilder(double latitude, double longitude, Set<String> ssids, Date date){
        this.latitude = latitude;
        this.longitude = longitude;
        this.ssids = ssids;
        this.date = date;
        gson = new Gson();
    }

    @Override
    public RequestData build(String url, @RequestData.RequestMethod int requestMethod) throws MalformedURLException {
        return new RequestData(url, requestMethod, buildJson());
    }

    private String buildJson() {
        JsonObjectAPI json = new JsonObjectAPI();
        JsonObject data = new JsonObject();

        JsonObject gps = new JsonObject();
        gps.addProperty(LATITUDE, latitude);
        gps.addProperty(LONGITUDE, longitude);
        data.add(UPDATE_GPS, gps);

        JsonArray wifi = new JsonArray();
        for (String ssid : ssids) {
            JsonObject jssid = new JsonObject();
            jssid.addProperty(UPDATE_SSID, ssid);
            wifi.add(jssid);
        }
        data.add(UPDATE_WIFI, wifi);
        data.addProperty(DATE, DateUtils.formatDateTimeISO8601(date));
        json.setData(data);
        return gson.toJson(json);
    }
}
