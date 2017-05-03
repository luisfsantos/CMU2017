package ist.meic.cmu.locmess_client.authentication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.network.BaseWebTask;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.WebRequest;
import ist.meic.cmu.locmess_client.network.WebRequestCallback;
import ist.meic.cmu.locmess_client.network.WebRequestResult;
import ist.meic.cmu.locmess_client.network.location_update.AlarmReceiver;
import ist.meic.cmu.locmess_client.network.location_update.LocationUpdateService;
import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;

/**
 * Created by Catarina on 23/04/2017.
 */

public class LoginTask extends BaseWebTask {

    private static final String TAG = "LoginTask";
    private final String username;

    public LoginTask(WebRequestCallback callback, RequestData requestData, String username) {
        super(callback, requestData);
        this.username = username;
    }

    @Override
    protected WebRequestResult doInBackground(Void... voids) {
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
    protected void onPostExecute(final WebRequestResult result) { //runs on the UI thread!!
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
                //runs on another thread, not on UI thread :)
                Handler handler = new Handler();
                MyRunnable runnable = new MyRunnable(mCallback.getContext(), result);
                handler.post(runnable);
            }
        }
    }

    private class MyRunnable implements Runnable {

        Context context;
        SharedPreferences pref;
        WebRequestResult result;

        public MyRunnable(Context context, WebRequestResult result) {
            this.context = context;
            pref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            this.result = result;
        }

        @Override
        public void run() {
            setupLocationUpdates(pref);
            storeJwtAuth(pref, result.getResult());
        }

        private void storeJwtAuth(SharedPreferences pref, String result) {
            String jwt;
            Context context = mCallback.getContext();

            Gson gson = new Gson();
            JsonObject data = gson.fromJson(result, JsonObject.class);
            jwt = data.get(RequestBuilder.TOKEN).getAsString();
            Log.d(TAG, "jwt: " + jwt);
            if (jwt == null) {
                mCallback.onWebRequestError(context.getString(R.string.something_went_wrong));
                return;
            }
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(context.getString(R.string.pref_jwtAuthenticator), jwt);
            editor.putString(context.getString(R.string.pref_username), username); // keep track of what user is logged in
            editor.apply();

            mCallback.onWebRequestSuccessful("User " + username + " logged in");
        }

        private void setupLocationUpdates(SharedPreferences pref) {
            Context context = mCallback.getContext();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);

            // inexact repeating to reduce battery drain
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, //trigger in 5 seconds (forced by Android 5+)
                    AlarmReceiver.REPEAT_INTERVAL, pendingIntent);

            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(context.getString(R.string.pref_currentAlarmInterval), AlarmReceiver.REPEAT_INTERVAL);
            editor.apply();

            Intent serviceIntent = new Intent(context, LocationUpdateService.class);
            context.startService(serviceIntent);
        }
    }
}
