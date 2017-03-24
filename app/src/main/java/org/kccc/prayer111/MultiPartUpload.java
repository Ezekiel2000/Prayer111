package org.kccc.prayer111;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ezekiel on 2017. 3. 17..
 */

public class MultiPartUpload extends AsyncTask<String, Integer, String> {

    private static String setUrl = "http://api.kccc.org/AppAjax/111prayer/index.php";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String res = null;

        try {
            String name = params[0];
            String email = params[1];
            String password = params[2];
            String method = params[3];
            String image = params[4];



            Log.d("하이", "image : " + image);

            File file = new File(image);

            Log.d("하이", "file : " + file.toString());

            final MediaType MEDIA_TYPE = MediaType.parse("image/*");
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
            final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
            String filename = image.substring(image.lastIndexOf("/") + 1);

            Log.d("하이", "filename : " + filename);

            RequestBody requestBody;
            RequestBody fileBody = RequestBody.create(MEDIA_TYPE, file);

            if (image.endsWith("png")) {
                Log.d("하이", "png임");
                fileBody = RequestBody.create(MEDIA_TYPE_PNG, file);
            } else if (image.endsWith("jpg")) {
                Log.d("하이", "jpg임");
                fileBody = RequestBody.create(MEDIA_TYPE_JPG, file);
            }

            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mode", "joinProcess")
                    .addFormDataPart("name", name)
                    .addFormDataPart("email", email)
                    .addFormDataPart("password", password)
                    .addFormDataPart("method", method)
                    .addFormDataPart("up", filename, fileBody)
                    .build();


            Log.d("하이", "바디 : " + requestBody.toString());

            Request request = new Request.Builder()
                    .url(setUrl)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            res = response.body().string();
            Log.d("하이", "response : " + res);

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject object = jsonObject.getJSONObject("result");
            String ty = object.getString("ty");

            Log.d("하이", "ty : " + ty);

            if (ty.equals("old")) {

//                Toast.makeText(this, "이미 있는 계정입니다.", Toast.LENGTH_SHORT).show();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
