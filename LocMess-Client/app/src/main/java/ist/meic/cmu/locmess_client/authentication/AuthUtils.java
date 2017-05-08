package ist.meic.cmu.locmess_client.authentication;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.WebRequest;
import ist.meic.cmu.locmess_client.network.WebRequestResult;
import ist.meic.cmu.locmess_client.network.request_builders.GenericUserRequestBuilder;

/**
 * Created by Catarina on 08/05/2017.
 */

public class AuthUtils {

    private static final String TAG = "AuthUtils";

    public static String userLogin(String username, String password, @Nullable String authTokenType) throws IOException {
        try {
            RequestData data = new GenericUserRequestBuilder(username, password)
                    .build(LocMessURL.LOGIN, RequestData.POST);
            WebRequestResult response = new WebRequest(data).execute();
            if (response.getError() != null) {
                throw new IOException(response.getErrorMessages());
            }
            return response.getAuthToken();
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Malformed URL: ", e);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            throw e;
        }
    }

    public static String userSignUp(String username, String password, String authTokenType) throws IOException {
        try {
            RequestData data = new GenericUserRequestBuilder(username, password)
                    .build(LocMessURL.SIGNUP, RequestData.POST);
            WebRequestResult response = new WebRequest(data).execute();
            if (response.getError() != null) {
                throw new IOException(response.getErrorMessages());
            }
            return userLogin(username, password, authTokenType);
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Malformed URL: ", e);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            throw e;
        }
    }
}
