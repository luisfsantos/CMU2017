package ist.meic.cmu.locmess_client.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ist.meic.cmu.locmess_client.data.KeyPair;

/**
 * Created by Catarina on 14/05/2017.
 */

public class WhiteBlackListUtils {

    public static String serializeToDbFormat(List<KeyPair> list) {
        JsonArray array = new JsonArray();
        for (KeyPair entry : list) {
            JsonObject obj = new JsonObject();
            obj.addProperty("key", entry.getKey());
            obj.addProperty("value", entry.getValue());
            array.add(obj);
        }
        return new Gson().toJson(array);
    }

    public static List<KeyPair> deserializeFromDbFormat(String list) {
        Type listType = new TypeToken<List<KeyPair>>(){}.getType();
        return new Gson().fromJson(list, listType);
    }
}
