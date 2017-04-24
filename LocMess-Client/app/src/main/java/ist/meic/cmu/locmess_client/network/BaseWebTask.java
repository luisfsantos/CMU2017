package ist.meic.cmu.locmess_client.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

/**
 * Created by Catarina on 23/04/2017.
 */

public abstract class BaseWebTask extends AsyncTask<RequestData, Void, WebRequestResult> {

    protected final WebRequestCallback mCallback;
    protected final RequestData mRequestData;

    public BaseWebTask(WebRequestCallback callback, RequestData requestData) {
        mCallback = callback;
        mRequestData = requestData;
    }

    @Override
    protected void onPreExecute() {
        if (mCallback != null) {
            NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update callback
                mCallback.onNoNetworkConnectivity();
                cancel(true);
            }
        }
    }

    protected abstract WebRequestResult doInBackground(RequestData... requestData);
}
