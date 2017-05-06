package ist.meic.cmu.locmess_client.network.json.serializers;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;

/**
 * Created by Catarina on 05/05/2017.
 */

public class KeypairSerializer {
    private static Gson gson = new GsonBuilder()
            .setDateFormat(RequestBuilder.DATE_FORMAT)
            .create();

    public KeyPair parse(JsonObject keypair) {
        return gson.fromJson(keypair, KeyPair.class);
    }

    public SparseArray<KeyPair> parseAll(JsonArray locations) {
        SparseArray<KeyPair> keypairsMap = new SparseArray<>();
        for (JsonElement element : locations) {
            KeyPair keypair = gson.fromJson(element, KeyPair.class);
            keypairsMap.put(keypair.getId(), keypair);
        }
        return keypairsMap;
    }

    public class KeyPair {
        int id;

        public int getId() {
            return id;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        String key;
        String value;
    }
}
