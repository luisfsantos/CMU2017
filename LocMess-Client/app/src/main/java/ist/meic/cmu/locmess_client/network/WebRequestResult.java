package ist.meic.cmu.locmess_client.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import ist.meic.cmu.locmess_client.network.json.Error;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;

/**
 * Created by Catarina on 23/04/2017.
 */

public class WebRequestResult {
    private String mResult;
    private String mError;
    private Exception connectionException;
    Gson gson = new Gson();

    public Exception getException() {
        return connectionException;
    }

    public void setException(Exception mException) {
        this.connectionException = mException;
    }

    public String getResult() {
        return mResult;
    }

    public String getResultStatusMessage() {
        JsonObjectAPI json = gson.fromJson(mResult, JsonObjectAPI.class);
        JsonObject data = json.getData();
        return data.get(RequestBuilder.INFO).getAsString();
    }

    public void setResult(String mResult) {
        this.mResult = mResult;
    }

    public String getError() {
        return mError;
    }

    public String getErrorMessages() {
        JsonObjectAPI json = gson.fromJson(mError, JsonObjectAPI.class);
        ArrayList<Error> errors = json.getErrors();
        StringBuilder builder = new StringBuilder();
        for (Error error : errors) {
            builder.append(error.getMessage());
            builder.append("\n");
        }
        return builder.toString();
    }

    public void setError(String mError) {
        this.mError = mError;
    }
}
