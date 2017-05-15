package ist.meic.cmu.locmess_client.network.json.deserializers;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ist.meic.cmu.locmess_client.data.KeyPair;

/**
 * Created by Catarina on 05/05/2017.
 */

public class KeypairDeserializer {
    private static Gson gson = new Gson();

    public KeyPair parse(JsonObject keypair) {
        return gson.fromJson(keypair, KeyPair.class);
    }

    public SparseArray<KeyPair> parseAll(JsonArray keypairs) {
        SparseArray<KeyPair> keypairsMap = new SparseArray<>();
        for (JsonElement element : keypairs) {
            KeyPair keypair = gson.fromJson(element, KeyPair.class);
            keypairsMap.put(keypair.getId(), keypair);
        }
        return keypairsMap;
    }
}
