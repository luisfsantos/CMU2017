package ist.meic.cmu.locmess_client.authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.network.BaseWebTask;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.WebRequest;
import ist.meic.cmu.locmess_client.network.WebRequestCallback;
import ist.meic.cmu.locmess_client.network.WebRequestResult;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;

/**
 * Created by Catarina on 23/04/2017.
 */

public class LoginTask extends BaseWebTask {

    private static final String TAG = "LoginTask";

    public LoginTask(WebRequestCallback callback, RequestData requestData) {
        super(callback, requestData);
    }

    @Override
    protected WebRequestResult doInBackground(RequestData... requestData) {
        try {
            return new WebRequest(mRequestData).execute();
        } catch (Exception e) {
            e.printStackTrace();
            WebRequestResult result = new WebRequestResult();
            result.setException(e);
            return result;
        }
    }

    @Override
    protected void onPostExecute(WebRequestResult result) {
        if (result != null && mCallback != null) {
            if (result.getException() != null) {
                mCallback.onWebRequestError(mCallback.getContext().getString(R.string.something_went_wrong));
            } else if (result.getError() != null) {
                String message = result.getErrorMessages();
                if (message == null) {
                    message = mCallback.getContext().getString(R.string.something_went_wrong);
                }
                mCallback.onWebRequestError(message);
            } else if (result.getResult() != null) {
                storeJwtAuth(result.getResult());
            }
        }
    }

    private void storeJwtAuth(String result) {
        String jwt;
        Context context = mCallback.getContext();
        String message;

        Gson gson = new Gson();
        JsonObjectAPI json = gson.fromJson(result, JsonObjectAPI.class);
        JsonObject data = json.getData();
        jwt = data.get("jwt").getAsString();
        message = data.get(RequestBuilder.INFO).getAsString();
        Log.d(TAG, "jwt: " + jwt);
        if (jwt == null) {
            mCallback.onWebRequestError(context.getString(R.string.something_went_wrong));
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.jwtAuthenticator), jwt);
        editor.commit();

        mCallback.onWebRequestSuccessful(message);
    }
}
