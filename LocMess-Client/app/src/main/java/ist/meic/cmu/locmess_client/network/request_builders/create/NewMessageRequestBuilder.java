package ist.meic.cmu.locmess_client.network.request_builders.create;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.Map;

import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;
import ist.meic.cmu.locmess_client.utils.DateUtils;

/**
 * Created by Catarina on 08/05/2017.
 */

public class NewMessageRequestBuilder implements RequestBuilder {
    private final String title;
    private final String text;
    private final Date fromDate;
    private final Date toDate;
    private final int locationID;
    private final Map<String, String> whitelist;
    private final Map<String, String> blacklist;
    private Gson gson;

    public NewMessageRequestBuilder(String title, String text, Date fromDate, Date toDate,
                                    int locationID, Map<String, String> whitelist, Map<String, String> blacklist) {
        this.title = title;
        this.text = text;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.locationID = locationID;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        gson = new Gson();
    }


    @Override
    public RequestData build(@Nullable String url, @RequestData.RequestMethod int requestMethod) throws MalformedURLException {
        return new RequestData(url, RequestData.POST, buildJson());
    }

    private String buildJson() {
        JsonObjectAPI json = new JsonObjectAPI();
        JsonObject data = new JsonObject();
        data.addProperty(TITLE, title);
        data.addProperty(TEXT, text);
        data.addProperty(DATE_FROM, DateUtils.formatDateTimeISO8601(fromDate));
        data.addProperty(DATE_TO, DateUtils.formatDateTimeISO8601(toDate));

        JsonObject location = new JsonObject();
        location.addProperty(ID, locationID);
        data.add(LOCATION, location);

        JsonArray jwhitelist = new JsonArray();
        for (Map.Entry<String, String> entry : whitelist.entrySet()) {
            JsonObject element = new JsonObject();
            element.addProperty(KEY, entry.getKey());
            element.addProperty(VALUE, entry.getValue());
            jwhitelist.add(element);
        }
        data.add(WHITELIST, jwhitelist);

        JsonArray jblacklist = new JsonArray();
        for (Map.Entry<String, String> entry : blacklist.entrySet()) {
            JsonObject element = new JsonObject();
            element.addProperty(KEY, entry.getKey());
            element.addProperty(VALUE, entry.getValue());
            jblacklist.add(element);
        }
        data.add(BLACKLIST, jblacklist);

        json.setData(data);
        return gson.toJson(json);
    }
}
