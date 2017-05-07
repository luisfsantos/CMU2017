package ist.meic.cmu.locmess_client.network.location_update;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.IOException;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.WebRequest;
import ist.meic.cmu.locmess_client.network.WebRequestResult;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.network.sync.merge.MergeMessage;

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
        Context context = getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        String jwt = pref.getString(context.getString(R.string.pref_jwtAuthenticator), "No auth");
        Bundle bundle = intent.getBundleExtra(INTENT_BUNDLE);
        RequestData request = (RequestData)bundle.getSerializable(INTENT_REQUEST);
        try {
            WebRequestResult response = new WebRequest(request, jwt).execute();
            JsonObjectAPI result = new Gson().fromJson(response.getResult(), JsonObjectAPI.class);
            JsonArray messages = result.getData().getAsJsonArray(WebRequestResult.MESSAGES);
            MergeMessage.mergeAllAvailable(getContentResolver(), messages, null);
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            return;
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating database: " + e.getMessage());
        }
        // TODO: 28/04/2017 fire notification
    }
}
