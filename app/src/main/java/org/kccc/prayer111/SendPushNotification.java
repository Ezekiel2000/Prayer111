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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ezekiel on 2017. 3. 6..
 */

public class SendPushNotification {

    public static final String PUSHWOOSH_SERVICE_BASE_URL = "https://cp.pushwoosh.com/json/1.3/";
    private static final String AUTH_TOKEN = "Q1wgPWshHYPIkqUWSJXWk2MWFMLaLnqpugKn1qUN0NGk1XqEpHf5JxRS9DY790DAgW3yBIeBT3vBRiictHbx";
    private static final String APPLICATION_CODE = "5B4AC-805EF";

    ArrayList<HashMap<String, String>> todayPraysList;
    private static String url = "http://api.kccc.org/a/pray111/get/today";

    SimpleDateFormat now;
    String today;
    public String push_message = null;

    public void SendPush() throws JSONException, MalformedURLException {

        now = new SimpleDateFormat("yyyy-MM-dd");
        Date Time = new Date();
        today = now.format(Time);

        today = today + " 15:40";
        HttpHandler sh = new HttpHandler();

        Log.d("하이", "지금 현재 시각은? : " + today );

        String jsonStr = sh.makeServiceCall(url);

        if (jsonStr != null) {

            try {

                todayPraysList = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(jsonStr);

                push_message= jsonObject.getString("prayer");
                String yymm = jsonObject.getString("yymm");
                String day = jsonObject.getString("day");

                Log.d("하이", "오늘의 날짜 : " + yymm + day);

            } catch (JSONException e) {

            }
        }

        String method = "createMessage";
        URL url = new URL(PUSHWOOSH_SERVICE_BASE_URL + method);

        JSONArray notificationsArray = new JSONArray()
                .put(new JSONObject()
                        .put("send_date", now)
                        .put("content", push_message)
                        .put("link", "org.kccc.prayer111"));
        JSONObject requestObj = new JSONObject()
                .put("application", APPLICATION_CODE)
                .put("auth", AUTH_TOKEN)
                .put("notifications", notificationsArray);

        Log.d("하이", "보내는 내용 :  " + notificationsArray.toString());

        JSONObject mainRequest = new JSONObject().put("request", requestObj);
        JSONObject response = SendServerRequest.sendJSONRequest(url, mainRequest.toString());

        Log.d("하이", "push 내용 : " + push_message);
        Log.d("하이", "Response : " + response);

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
