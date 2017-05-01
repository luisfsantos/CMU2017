package ist.meic.cmu.locmess_client.network.location_update;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ist.meic.cmu.locmess_client.network.RequestData;

/**
 * Created by Catarina on 28/04/2017.
 */

public class FetchLocationMessagesService extends IntentService {
    private static final String TAG = "FetchLocationMsgService";
    static final String INTENT_BUNDLE = "request_bundle";
    static final String INTENT_REQUEST = "request";

    public FetchLocationMessagesService() {
        super("FetchLocationMessagesService");
    }

    public FetchLocationMessagesService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            Log.wtf(TAG, "Intent was null");
            return;
        }
        Log.d(TAG, "I received an intent!");
        // TODO: 28/04/2017 check for connectivity again, abort (return) if no connectivity? is this overkill?
        Bundle bundle = intent.getBundleExtra(INTENT_BUNDLE);
        RequestData request = (RequestData)bundle.getSerializable(INTENT_REQUEST);
        // TODO: 28/04/2017 send webrequest to server
        // TODO: 28/04/2017 (remove all old available messages from table? or not here? where to do this? discuss)
        // TODO: 28/04/2017 collect results into TemporaryAvailableMessages table
        // TODO: 28/04/2017 fire notification
    }
}
