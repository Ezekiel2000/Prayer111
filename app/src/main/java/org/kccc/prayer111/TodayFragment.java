package org.kccc.prayer111;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ezekiel on 2017. 2. 2..
 */

public class TodayFragment extends Fragment {

    TextView today_pray_title;
    TextView today_pray_content;

    SimpleDateFormat curYearFormat;
    SimpleDateFormat curMonthFormat;
    SimpleDateFormat curDayFormat;

    private String TAG = TodayFragment.class.getSimpleName();

    private static String url = "http://api.kccc.org/a/pray111/get/today";

    ArrayList<HashMap<String, String>> todayPraysList;

    public TodayFragment() {

    }

    public static TodayFragment newInstance() {
        TodayFragment fragment = new TodayFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_today, container, false);

        Log.d("하이", "getID" + view.getId());

        todayPraysList = new ArrayList<>();

        today_pray_title = (TextView) view.findViewById(R.id.today_pray_title);
        Typeface typefaceTitle = Typeface.createFromAsset(getContext().getAssets(), "tvN_OTF_Light.otf");
        today_pray_title.setTypeface(typefaceTitle);

        today_pray_content = (TextView) view.findViewById(R.id.today_pray_content);
        Typeface typefaceContent = Typeface.createFromAsset(getContext().getAssets(), "NotoSansCJKkr_Light.otf");
        today_pray_content.setTypeface(typefaceContent);

        curYearFormat = new SimpleDateFormat("yyyy");
        curMonthFormat = new SimpleDateFormat("MM");
        curDayFormat = new SimpleDateFormat("dd");

        new GetTodayPrays().execute();

        return view;
    }

    private class GetTodayPrays extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {


            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONArray jsonary = new JSONArray(jsonStr);

                    for (int i = 0 ; i < jsonary.length() ; i++) {

                        JSONObject jsonObj = jsonary.getJSONObject(i);

                        String pray = jsonObj.getString("prayer");
                        String yymm = jsonObj.getString("yymm");
                        String day = jsonObj.getString("day");

                        HashMap<String, String> todayPray = new HashMap<>();

                        todayPray.put("pray", pray);
                        todayPray.put("yymm", yymm);
                        todayPray.put("day", day);

                        todayPraysList.add(todayPray);

                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error:" + e.getMessage());

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            long now = System.currentTimeMillis();
            Date date = new Date(now);

            String strCurYear = curYearFormat.format(date) + curMonthFormat.format(date);
            String strCurDay = curDayFormat.format(date);


            for ( int i = 0 ; i < todayPraysList.size() ; i++ ) {
                if ( todayPraysList.get(i).get("yymm").equals(strCurYear) && todayPraysList.get(i).get("day").equals(strCurDay)) {
                    today_pray_content.setText(todayPraysList.get(i).get("pray"));
                } else {
                    Toast.makeText(getContext(), "기도제목을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onResume() {

//        FloatingActionButton fab_write = (FloatingActionButton) getActivity().findViewById(R.id.fab_write);
//        FloatingActionButton fab_share = (FloatingActionButton) getActivity().findViewById(R.id.fab_share);
//        fab_share.show();
//        fab_write.hide();

        Log.d("하이", "fragment1  Resume");

        getActivity().findViewById(R.id.fab_write).setVisibility(View.GONE);
        getActivity().findViewById(R.id.multiple_action).setVisibility(View.VISIBLE);

        super.onResume();
    }

    @Override
    public void onPause() {

        getActivity().findViewById(R.id.fab_write).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.multiple_action).setVisibility(View.GONE);


        Log.d("하이", "fragment1  onPause");
        super.onPause();
    }



}
