package ist.meic.cmu.locmess_client.network.json;

/**
 * Created by Catarina on 24/04/2017.
 */

public class Error {
    int code;
    String message;

    public Error(int code, String message) {

        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
