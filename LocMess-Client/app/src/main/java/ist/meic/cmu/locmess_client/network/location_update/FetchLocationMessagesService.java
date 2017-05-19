package ist.meic.cmu.locmess_client.network.location_update;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.IOException;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.authentication.AccountService;
import ist.meic.cmu.locmess_client.messages.inbox.InboxActivity;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.WebRequest;
import ist.meic.cmu.locmess_client.network.WebRequestResult;
import ist.meic.cmu.locmess_client.network.json.JsonObjectAPI;
import ist.meic.cmu.locmess_client.network.sync.merge.MergeMessage;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;

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
        Log.d(TAG, "Fetching messages from server...");
        // TODO: 28/04/2017 check for connectivity again, abort (return) if no connectivity? is this overkill?
        String jwt;
        AccountManager am = AccountManager.get(getBaseContext());
        Account account = AccountService.getActiveAccount(am);
        Bundle bundle = intent.getBundleExtra(INTENT_BUNDLE);
        RequestData request = (RequestData) bundle.getSerializable(INTENT_REQUEST);
        assert request != null;

        int numNotRead = 0;
        int numNewMessages = 0;

        try {
            jwt = am.blockingGetAuthToken(account, AccountService.AUTH_TOKEN_TYPE, false);
            WebRequestResult response = new WebRequest(request, jwt).execute();
            try {
                response.assertValidJwtToken();
            } catch (WebRequestResult.JwtExpiredException e) {
                Log.e(TAG, e.getMessage());
                jwt = AccountService.refreshAuthToken(getBaseContext(), account, AccountService.AUTH_TOKEN_TYPE, jwt);
                response = new WebRequest(request, jwt).execute();
            }
            JsonObjectAPI result = new Gson().fromJson(response.getResult(), JsonObjectAPI.class);
            JsonArray messages = result.getData().getAsJsonArray(WebRequestResult.MESSAGES);
            numNewMessages = MergeMessage.mergeAllAvailable(getContentResolver(), messages, null);
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            return;
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating database: " + e.getMessage());
            return;
        } catch (OperationCanceledException | AuthenticatorException e) {
            Log.e(TAG, "Error getting auth token from Account Manager: ", e);
            stopSelf();
        }
        if (numNewMessages > 0) {
            // we only show the notification when there are new messages since the last update,
            // but the notification displays how many messages are still not read in general
            Cursor c = getContentResolver().query(
                    LocMessDBContract.AvailableMessages.CONTENT_URI,
                    new String[]{LocMessDBContract.AvailableMessages._ID},
                    LocMessDBContract.AvailableMessages.COLUMN_READ + " = ?",
                    new String[]{String.valueOf(LocMessDBContract.AvailableMessages.MESSAGE_NOT_READ)},
                    null
            );
            if (c != null) {
                numNotRead = c.getCount();
                c.close();
            }
            Intent inboxIntent = new Intent(this, InboxActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    inboxIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            String messageSingOrPlural = (numNotRead > 1 ?
                    getString(R.string.message_plural) : getString(R.string.message_singular));
            String contentText = getString(R.string.new_messages_notif_text, numNotRead, messageSingOrPlural);
            Notification notif = new NotificationCompat.Builder(getBaseContext())
                    .setSmallIcon(R.drawable.ic_message_black_24dp)
                    .setContentTitle(getString(R.string.new_messages_notif_title))
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            NotificationManager mNotifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifManager.notify(R.id.new_message_notification_id, notif);
        }
    }
}
