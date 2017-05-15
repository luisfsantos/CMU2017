package ist.meic.cmu.locmess_client.network.p2p.json;

import com.google.gson.JsonElement;

/**
 * Created by Catarina on 14/05/2017.
 */

public class P2pRequest {
    public static final String TYPE_MATCH = "match";
    public static final String TYPE_MESSAGES = "messages";
    String type;
    JsonElement data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }
}
