package ist.meic.cmu.locmess_client.network.p2p;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;

/**
 * Created by Catarina on 13/05/2017.
 */

public class P2pMessageSenderService extends IntentService {
    private static final String TAG = "P2pMessageSenderService";

    static final String INTENT_DATA = "data";
    static final String INTENT_RECEIVER = "result_receiver";
    static final String INTENT_IPADDR = "remote_ip_addr";
    static final String INTENT_PORT = "remote_port";

    static final int MATCH_RESULT_CODE = 10;
    static final int ERROR_RESULT_CODE = 11;
    static final String RESULT_DATA = "result_data";

    public P2pMessageSenderService() {
        super(TAG);
    }

    public P2pMessageSenderService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SimWifiP2pSocketManager.Init(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Received an intent");
        if (intent == null) {
            Log.i(TAG, "Intent was null");
            return;
        }
        String data = intent.getStringExtra(INTENT_DATA);
        Log.d(TAG, "To send: " + data);
        ResultReceiver receiver = intent.getParcelableExtra(INTENT_RECEIVER);
        String ipaddr = intent.getStringExtra(INTENT_IPADDR);
        int port = intent.getIntExtra(INTENT_PORT,-1);
        if (ipaddr == null || port == -1) {
            throw new IllegalArgumentException("Incorrect INTENT_IPADDR or INTENT_PORT");
        }
        Bundle resultData = new Bundle();

        try {
            SimWifiP2pSocket cliSocket = new SimWifiP2pSocket(ipaddr, port);
            cliSocket.getOutputStream().write((data + "\n").getBytes());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(cliSocket.getInputStream()));
            String response = reader.readLine();
            cliSocket.close();
            resultData.putString(RESULT_DATA, response);
            if (receiver != null) receiver.send(MATCH_RESULT_CODE, resultData);
        } catch (UnknownHostException e) {
            Log.e(TAG, "Unknown host: ", e);
            if (receiver != null) receiver.send(ERROR_RESULT_CODE, resultData);
        } catch (IOException e) {
            Log.e(TAG, "IO error: ", e);
            if (receiver != null) receiver.send(ERROR_RESULT_CODE, resultData);
        }
    }
}
