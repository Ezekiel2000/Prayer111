package org.kccc.prayer111;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by ezekiel on 2017. 2. 14..
 */

public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {
    }

    public String makeServiceCall(String reqUrl) {

        String response = null;

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException : " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException : " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException : " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage());
        }

        return response;
    }

    public void makeServicePost(String reqUrl) {

        String response = null;

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setChunkedStreamingMode(0);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());


            InputStream in = new BufferedInputStream(conn.getInputStream());




        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException : " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException : " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException : " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage());
        }

    }


    private String convertStreamToString(InputStream is) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

}
