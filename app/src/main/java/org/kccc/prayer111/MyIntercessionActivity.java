package org.kccc.prayer111;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyIntercessionActivity extends AppCompatActivity {

    List<ListData> listData;
    ListData[] data;

    ListDataAdapter myListDataAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private static String getUrl = "http://api.kccc.org/AppAjax/111prayer/index.php?mode=getMyTogether";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_intercession);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }
        Log.d("하이", "시스템바 변경");

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.list_my_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        listData = new ArrayList<>();
        myListDataAdapter = new ListDataAdapter(getApplicationContext(), listData, R.layout.activity_my_intercession);

        new MyGetIntercession().execute();

    }

    private class MyGetIntercession extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpHandler sh = new HttpHandler();

            getUrl = getUrl + "&userId=" + PropertyManager.getInstance().getUserId();
            String jsonStr = sh.makeServiceCall(getUrl);

            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                String dataJson = jsonObject.getString("result");

                JSONArray jsonArray = new JSONArray(dataJson);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);

                    Log.d("하이", "불러오는 값 : " + object);

                    String number = object.getString("no");
                    String email = object.getString("id");
                    String name = object.getString("name");
                    String content = object.getString("pray");
                    String date = object.getString("indate");
                    String profile = object.getString("prayPhoto");
                    String imageInput = object.getString("photo");
                    int warn = object.getInt("warn");
                    int prayNumber = object.getInt("heart");
                    int commentNumber = object.getInt("comment");

                    Log.d("하이", "불러오는 값 : " + number);
                    Log.d("하이", "사진값 : " + profile);

                    data = new ListData[jsonArray.length()];

                    data[i] = new ListData(number, profile, email, name, date, content, imageInput, warn, prayNumber, commentNumber);
                    listData.add(data[i]);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            myListDataAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(myListDataAdapter);

        }
    }



}
