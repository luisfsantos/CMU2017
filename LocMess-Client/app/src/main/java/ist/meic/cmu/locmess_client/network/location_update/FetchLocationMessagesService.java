package ist.meic.cmu.locmess_client.network.location_update;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.IntentService;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.IOException;

import ist.meic.cmu.locmess_client.authentication.GenericAccountService;
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
        Log.d(TAG, "Preparing request...");
        // TODO: 28/04/2017 check for connectivity again, abort (return) if no connectivity? is this overkill?
        String jwt;
        AccountManager am = AccountManager.get(getBaseContext());
        Account account = GenericAccountService.GetActiveAccount(am);
        Bundle bundle = intent.getBundleExtra(INTENT_BUNDLE);
        RequestData request = (RequestData)bundle.getSerializable(INTENT_REQUEST);
        assert request != null;
        try {
            jwt = am.blockingGetAuthToken(account, GenericAccountService.AUTH_TOKEN_TYPE, false);
            WebRequestResult response = new WebRequest(request, jwt).execute();
            try {
                response.assertValidJwtToken();
            } catch (WebRequestResult.JwtExpiredException e) {
                Log.e(TAG, e.getMessage());
                jwt = GenericAccountService.refreshAuthToken(getBaseContext(), account, GenericAccountService.AUTH_TOKEN_TYPE, jwt);
                response = new WebRequest(request, jwt).execute();
            }
            JsonObjectAPI result = new Gson().fromJson(response.getResult(), JsonObjectAPI.class);
            JsonArray messages = result.getData().getAsJsonArray(WebRequestResult.MESSAGES);
            MergeMessage.mergeAllAvailable(getContentResolver(), messages, null);
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            return;
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating database: " + e.getMessage());
        } catch (OperationCanceledException | AuthenticatorException e) {
            Log.e(TAG, "Error getting auth token from Account Manager: ", e);
            stopSelf();
        }
        // TODO: 28/04/2017 fire notification
    }
}
