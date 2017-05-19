package ist.meic.cmu.locmess_client.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.WebRequest;
import ist.meic.cmu.locmess_client.network.WebRequestResult;
import ist.meic.cmu.locmess_client.network.location_update.LocationUpdateService;
import ist.meic.cmu.locmess_client.network.location_update.UpdateLocationAlarmReceiver;
import ist.meic.cmu.locmess_client.network.p2p.P2pDeliveryAlarmReceiver;
import ist.meic.cmu.locmess_client.network.p2p.P2pMessageReceiverService;
import ist.meic.cmu.locmess_client.network.p2p.P2pMessageScannerService;
import ist.meic.cmu.locmess_client.network.request_builders.GenericUserRequestBuilder;

/**
 * Created by Catarina on 08/05/2017.
 */

public class AuthUtils {

    private static final String TAG = "AuthUtils";

    public static String userLogin(Context context, String username,
                                   String password, @Nullable String authTokenType) throws IOException {
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
        } catch (ConnectException e) {
            throw new IOException(context.getString(R.string.no_network_connection));
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            throw e;
        }
    }

    public static String userSignUp(Context context, String username,
                                    String password, String authTokenType) throws IOException {
        try {
            RequestData data = new GenericUserRequestBuilder(username, password)
                    .build(LocMessURL.SIGNUP, RequestData.POST);
            WebRequestResult response = new WebRequest(data).execute();
            if (response.getError() != null) {
                throw new IOException(response.getErrorMessages());
            }
            return userLogin(context, username, password, authTokenType);
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Malformed URL: ", e);
            throw e;
        } catch (ConnectException e) {
            throw new IOException(context.getString(R.string.no_network_connection));
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            throw e;
        }
    }

    public static void userLogout(Context context) {
        AccountManager manager = AccountManager.get(context);
        Account account = AccountService.getActiveAccount(manager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            manager.removeAccountExplicitly(account);
        } else {
            manager.removeAccount(account, null, null);
        }

        //unregister alarms
        P2pDeliveryAlarmReceiver.unscheduleAlarm(context);
        UpdateLocationAlarmReceiver.unscheduleAlarm(context);

        //stop running services
        Intent updateIntent = new Intent(context, LocationUpdateService.class);
        context.stopService(updateIntent);

        Intent p2pReceiverIntent = new Intent(context, P2pMessageReceiverService.class);
        context.stopService(p2pReceiverIntent);

        Intent p2pScannerIntent = new Intent(context, P2pMessageScannerService.class);
        context.stopService(p2pScannerIntent);
    }
}
