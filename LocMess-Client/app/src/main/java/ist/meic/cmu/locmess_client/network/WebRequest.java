package ist.meic.cmu.locmess_client.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by Catarina on 21/04/2017.
 */

public class WebRequest {
    private static final String TAG = "WebRequest";
    private static final int TIMEOUT = 5;
    private RequestData mRequest;
    private String mAuth = null;

    public WebRequest(RequestData request) {
        mRequest = request;
    }

    public WebRequest(RequestData request, String auth) {
        mRequest = request;
        mAuth = auth;
    }

    public void setAuth(String auth) {
        this.mAuth = auth;
    }

    public WebRequestResult execute() throws IOException{
        InputStream in = null;
        HttpURLConnection connection = null;
        WebRequestResult result = null;
        try {
            connection = (HttpURLConnection) mRequest.getUrl().openConnection();
            connection.setReadTimeout(TIMEOUT * 1000);
            connection.setConnectTimeout(TIMEOUT * 1000);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            if (mAuth != null) {
                connection.setRequestProperty("Authorization", "Bearer " + mAuth);
            }
            if (mRequest.getRequestMethod() == RequestData.GET) {
                connection.setRequestMethod("GET");
            } else if (mRequest.getRequestMethod() == RequestData.POST) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStream out = connection.getOutputStream();
                out.write(mRequest.getData().toString().getBytes());
                out.close();
            }
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
                result = new WebRequestResult();
                result.setError(readStream(in));
                Log.i(TAG, "error from server: " + result.getError());
            } else {
                in = connection.getInputStream();
                if (in != null) {
                    result = new WebRequestResult();
                    result.setResult(readStream(in));
                    Log.i(TAG, "result from server: " + result.getResult());
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private String readStream(InputStream in) throws IOException{
        String line;
        StringBuilder result = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
