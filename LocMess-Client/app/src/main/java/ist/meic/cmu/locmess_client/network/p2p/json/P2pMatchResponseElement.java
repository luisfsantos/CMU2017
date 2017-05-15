package ist.meic.cmu.locmess_client.network.p2p.json;

/**
 * Created by Catarina on 15/05/2017.
 */

public class P2pMatchResponseElement {
    int id;
    boolean send;

    public P2pMatchResponseElement(int id, boolean send) {
        this.id = id;
        this.send = send;
    }

    public P2pMatchResponseElement() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }
}
