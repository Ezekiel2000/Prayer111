package org.kccc.prayer111;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ezekiel on 2017. 2. 2..
 */

public class IntercessionFragment extends Fragment {

    final int ITEM_SIZE = 5;
    List<ListData> listData;
    ListData[] data;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;


    private String TAG = IntercessionFragment.class.getSimpleName();
    private static String url = "http://api.kccc.org/a/pray111/get/together?s=0";

    ArrayList<HashMap<String, String>> IntercessionPraysList;


    public IntercessionFragment() {

    }

    public static IntercessionFragment newInstance() {
        IntercessionFragment fragment = new IntercessionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_intercession, container, false);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        listData = new ArrayList<>();

        new GetIntercessionPrays().execute();

        return view;

    }


    private class GetIntercessionPrays extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(url);

            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                String dataJson = jsonObject.getString("data");

                JSONArray jsonArray = new JSONArray(dataJson);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Log.d("하이", "불러오는 값 : " + object);

                    String number = object.getString("no");
                    String name = object.getString("id");
                    String content = object.getString("pray");
                    String date = object.getString("indate");
                    String profile = object.getString("photo");
                    int warn = object.getInt("warn");
                    int prayNumber = object.getInt("heart");
                    int commentNumber = object.getInt("comment");

                    Log.d("하이", "불러오는 값 : " + number);
                    Log.d("하이", "사진값 : " + profile);

                    data = new ListData[jsonArray.length()];

                    data[i] = new ListData(profile, name, date, content, prayNumber, commentNumber);
                    listData.add(data[i]);

                }

            } catch (JSONException e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            recyclerView.setAdapter(new ListDataAdapter(getActivity().getApplicationContext(), listData, R.layout.fragment_intercession));

        }
    }




    @Override
    public void onResume() {

//        FloatingActionButton fab_write = (FloatingActionButton) getActivity().findViewById(R.id.fab_write);
//        FloatingActionButton fab_share = (FloatingActionButton) getActivity().findViewById(R.id.fab_share);
//        fab_share.hide();
//        fab_write.show();

        Log.d("하이", "fragment3  Resume");


        super.onResume();
    }

    @Override
    public void onPause() {

        Log.d("하이", "fragment3  onPause");

        super.onPause();
    }
}
