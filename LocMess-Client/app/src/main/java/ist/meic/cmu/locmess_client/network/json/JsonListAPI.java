package ist.meic.cmu.locmess_client.network.json;

import com.google.gson.JsonArray;

import java.util.ArrayList;

/**
 * Created by Catarina on 24/04/2017.
 */

public class JsonListAPI extends JsonAPI {
    JsonArray data;

    public JsonListAPI(JsonArray data, ArrayList<Error> errors, String meta) {
        super(errors, meta);
        this.data = data;
    }

    public JsonListAPI() {
    }

    public JsonArray getData() {
        return data;
    }

    public void setData(JsonArray data) {
        this.data = data;
    }
}
