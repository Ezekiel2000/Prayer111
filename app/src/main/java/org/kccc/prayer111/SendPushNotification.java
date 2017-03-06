package org.kccc.prayer111;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ezekiel on 2017. 3. 6..
 */

public class SendPushNotification {

    public static final String PUSHWOOSH_SERVICE_BASE_URL = "https://cp.pushwoosh.com/json/1.3/";
    private static final String AUTH_TOKEN = "Q1wgPWshHYPIkqUWSJXWk2MWFMLaLnqpugKn1qUN0NGk1XqEpHf5JxRS9DY790DAgW3yBIeBT3vBRiictHbx";
    private static final String APPLICATION_CODE = "5B4AC-805EF";

    public static void main(String[] args) throws JSONException, MalformedURLException {

        String method = "createMessage";
        URL url = new URL(PUSHWOOSH_SERVICE_BASE_URL + method);

        JSONArray notifi_Array = new JSONArray()
                .put(new JSONObject()
                        .put("send_date", "now")
                        .put("content", "test")
                        .put("link", "http://pushwoosh.com/"));
        JSONObject requestObj = new JSONObject()
                .put("application", APPLICATION_CODE)
                .put("auth", AUTH_TOKEN)
                .put("notifications", notifi_Array);

        JSONObject mainRequest = new JSONObject().put("request", requestObj);
        JSONObject response = SendServerRequest.sendJSONRequest(url, mainRequest.toString());

        Log.d("하이", "Response " + response);

    }
}

class SendServerRequest {

    static JSONObject sendJSONRequest(URL url, String request) {

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.write(request.getBytes("UTF-8"));
            writer.flush();
            writer.close();

            return parseResponse(connection);

        } catch (Exception e) {

            Log.d("하이" , "error : " + e);
            return null;

        } finally {

            if (connection != null) {

                connection.disconnect();

            }
        }
    }

    static JSONObject parseResponse(HttpURLConnection connection) throws IOException, JSONException {

        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {

            response.append(line).append('\r');

        }
        reader.close();

        return new JSONObject(response.toString());

    }
}
