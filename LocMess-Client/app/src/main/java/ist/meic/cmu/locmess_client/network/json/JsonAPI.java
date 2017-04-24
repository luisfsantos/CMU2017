package ist.meic.cmu.locmess_client.network.json;

import java.util.ArrayList;

/**
 * Created by Catarina on 24/04/2017.
 */

public class JsonAPI {
    String meta;
    ArrayList<Error> errors;

    public JsonAPI(ArrayList<Error> errors, String meta) {
        this.meta = meta;
        this.errors = errors;
    }

    public JsonAPI() {
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public ArrayList<Error> getErrors() {
        return errors;
    }

    public void addError(Error error) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(error);
    }

    public void setErrors(ArrayList<Error> errors) {
        this.errors = errors;
    }
}
