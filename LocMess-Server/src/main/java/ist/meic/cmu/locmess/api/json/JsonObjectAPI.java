package ist.meic.cmu.locmess.api.json;

import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by lads on 21-04-2017.
 */
public class JsonObjectAPI extends JsonAPI {

    JsonObject data;

    public JsonObjectAPI(JsonObject data, ArrayList<Error> errors, String meta) {
        super(errors, meta);
        this.data = data;
    }

    public JsonObjectAPI() {
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }
}
