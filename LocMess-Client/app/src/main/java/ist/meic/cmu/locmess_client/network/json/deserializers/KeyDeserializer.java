package ist.meic.cmu.locmess_client.network.json.deserializers;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Catarina on 09/05/2017.
 */

public class KeyDeserializer {

    private static Gson gson = new Gson();

    public Key parse(JsonObject key) {
        return gson.fromJson(key, Key.class);
    }

    public SparseArray<Key> parseAll(JsonArray keys) {
        SparseArray<Key> keysMap = new SparseArray<>();
        for (JsonElement element : keys) {
            Key key = gson.fromJson(element, Key.class);
            keysMap.put(key.getId(), key);
        }
        return keysMap;
    }

    public class Key {
        int id;
        String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
