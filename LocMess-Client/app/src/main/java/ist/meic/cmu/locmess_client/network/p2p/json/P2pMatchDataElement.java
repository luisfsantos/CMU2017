package ist.meic.cmu.locmess_client.network.p2p.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ist.meic.cmu.locmess_client.data.KeyPair;

/**
 * Created by Catarina on 14/05/2017.
 */

public class P2pMatchDataElement {
    @SerializedName("id") int messageID;
    List<KeyPair> whitelist;
    List<KeyPair> blacklist;

    public int getId() {
        return messageID;
    }

    public void setId(int id) {
        this.messageID = id;
    }

    public List<KeyPair> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<KeyPair> whitelist) {
        this.whitelist = whitelist;
    }

    public List<KeyPair> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<KeyPair> blacklist) {
        this.blacklist = blacklist;
    }
}
