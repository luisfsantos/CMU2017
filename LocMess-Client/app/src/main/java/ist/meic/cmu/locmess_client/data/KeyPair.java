package ist.meic.cmu.locmess_client.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Catarina on 14/05/2017.
 */

public class KeyPair {
    @SerializedName("key")
    String key;
    @SerializedName("value")
    String value;
    @SerializedName("id")
    Integer id;

    public KeyPair() {}

    public KeyPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof KeyPair)) {
            return false;
        }
        return key.equals(((KeyPair) obj).getKey()) && value.equals(((KeyPair) obj).getValue());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return key + ": " + value;
    }
}
