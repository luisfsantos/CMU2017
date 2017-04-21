package ist.meic.cmu.locmess_client.network;

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
    private static int TIMEOUT = 5;
    RequestData mRequest;

    public WebRequest(RequestData request) {
        mRequest = request;
    }

    public String execute() throws IOException{
        InputStream in = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) mRequest.getUrl().openConnection();
            connection.setReadTimeout(TIMEOUT * 1000);
            connection.setConnectTimeout(TIMEOUT * 1000);
            connection.setDoInput(true);
//            connection.setRequestProperty("Content-Type", "application/json");
            if (mRequest.getRequestMethod() == RequestData.GET) {
                connection.setRequestMethod("GET");
            } else if (mRequest.getRequestMethod() == RequestData.POST) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
//                OutputStream out = connection.getOutputStream();
                //TODO write request data to json object, then to output stream
//                out.write(...);
//                out.close();
            }
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            in = connection.getInputStream();
            if (in != null) {
                result = readStream(in);
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
