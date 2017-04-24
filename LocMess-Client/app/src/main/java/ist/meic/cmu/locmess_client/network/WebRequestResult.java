package ist.meic.cmu.locmess_client.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;

/**
 * Created by Catarina on 23/04/2017.
 */

public class WebRequestResult {
    private String mResult;
    private String mError;
    private Exception connectionException;

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
        try {
            JSONObject json = new JSONObject(mResult);
            JSONObject data = json.getJSONObject(RequestBuilder.DATA);
            return data.getString(RequestBuilder.STATUS);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setResult(String mResult) {
        this.mResult = mResult;
    }

    public String getError() {
        return mError;
    }

    public String getErrorMessages() {
        try {
            JSONObject json = new JSONObject(mError);
            JSONArray errors = json.getJSONArray(RequestBuilder.ERRORS);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < errors.length(); i++) {
                builder.append(errors.getJSONObject(i).getString(RequestBuilder.MESSAGE));
                builder.append("\n");
            }
            return builder.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setError(String mError) {
        this.mError = mError;
    }
}
