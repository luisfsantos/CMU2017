package ist.meic.cmu.locmess_client.data;

/**
 * Created by Catarina on 02/04/2017.
 */

public class KeyPair {
    public String key;
    public String value;

    public KeyPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + ":" + value;
    }
}
