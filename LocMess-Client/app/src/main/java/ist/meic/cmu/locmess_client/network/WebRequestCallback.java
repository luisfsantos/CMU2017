package ist.meic.cmu.locmess_client.network;

import android.content.Context;
import android.net.NetworkInfo;

/**
 * Created by Catarina on 23/04/2017.
 */

public interface WebRequestCallback {
    void onNoNetworkConnectivity();
    NetworkInfo getActiveNetworkInfo();
    void onWebRequestError(String message);
    void onWebRequestSuccessful(String message);
    Context getContext();
}
