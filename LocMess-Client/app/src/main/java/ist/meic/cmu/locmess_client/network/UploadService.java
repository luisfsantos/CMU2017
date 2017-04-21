package ist.meic.cmu.locmess_client.network;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import ist.meic.cmu.locmess_client.R;

/**
 * A service for asynchronously uploading data to a server without
 * expecting an answer that updates the UI.
 * Important: This service is both a started and bound service.
 *
 * Can be used for:
 *  - Posting/Unposting messages
 *  - Creating/removing locations
 *  - Creating/removing keypairs
 */
public class UploadService extends Service {
    // TODO: 21/04/2017 TEST THIS!!!

    private static final String TAG = "UploadService";

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (hasNetworkConnection()) {
                    // network connection has been restored
                    unregisterReceiver(this);
                    handleRequest();
                }
            }
        }
    };

    private View mSnackbarAnchor;

    private final Queue<RequestData> mRequestQueue = new LinkedList<>();
    private final UploadServiceBinder mBinder = new UploadServiceBinder();

    public class UploadServiceBinder extends Binder {
        // FIXME: 20/04/2017 method to add a view so that we can create a snackbar when there's no network connection
        public void bindSnackbarAnchor(View view) {
            mSnackbarAnchor = view;
        }
        public void enqueueRequest(RequestData data) {
            mRequestQueue.offer(data);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSnackbarAnchor = null;
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void handleRequest() {
        RequestData request = mRequestQueue.peek();
        if (request == null) {
            stopSelf();
        } else {
            new Request().execute(request);
        }
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() &&
                (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE));
    }

    private class Request extends AsyncTask<RequestData, Void, Boolean> {

        @Override
        protected Boolean doInBackground(RequestData... requests) {
            try {
                //TODO
                new WebRequest(requests[0]).execute();
                return true;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                mRequestQueue.remove();
                handleRequest();
            } else {
                if (!hasNetworkConnection()) {
                    // Device is offline
                    //FIXME decide whether snackbar creation is kept or not
                    if (mSnackbarAnchor != null) {
                        Snackbar.make(mSnackbarAnchor,
                                getApplicationContext().getString(R.string.no_network_connection),
                                Snackbar.LENGTH_INDEFINITE);
                    }
                    // Register receiver in order to resume processing requests once internet connectivity is restored
                    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    registerReceiver(mReceiver, filter);

                } else {
                    // something else went wrong, retry request
                    handleRequest();
                }
            }
        }
    }
}
