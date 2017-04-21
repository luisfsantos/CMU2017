package ist.meic.cmu.locmess.api.json;

/**
 * Created by lads on 21-04-2017.
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
