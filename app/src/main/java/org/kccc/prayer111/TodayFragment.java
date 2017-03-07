package org.kccc.prayer111;

import android.app.ProgressDialog;
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

    private static String url = "http://api.kccc.org/AppAjax/111prayer/index.php?mode=getToday";

    String today_pray;

    ArrayList<HashMap<String, String>> todayPraysList;

    ProgressDialog progressDialog;

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
        curDayFormat = new SimpleDateFormat("d");

        new GetTodayPrays().execute();
        Log.d("하이", "fragment1  onCreateView" + today_pray);
        return view;
    }

    private class GetTodayPrays extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(), null, "로딩중입니다.", true, false);
        }

        @Override
        protected Void doInBackground(Void... params) {


            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {

                try {

                    JSONObject jsonObject = new JSONObject(jsonStr);

                    JSONObject object = jsonObject.getJSONObject("result");

                    String pray = object.getString("prayer");
                    String yymm = object.getString("yymm");
                    String day = object.getString("day");

                    HashMap<String, String> todayPray = new HashMap<>();

                    Log.d("하이", "fragment1  doInBackground" + jsonObject);

                    todayPray.put("pray", pray);
                    todayPray.put("yymm", yymm);
                    todayPray.put("day", day);

                    todayPraysList.add(todayPray);

                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error:" + e.getMessage());

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            long now = System.currentTimeMillis();
            Date date = new Date(now);

            String strCurYear = curYearFormat.format(date) + curMonthFormat.format(date);
            String strCurDay = curDayFormat.format(date);
            Log.d("하이", "날짜" +  strCurDay);
            Log.d("하이", "날짜" +  todayPraysList.get(0).get("day"));

            Log.d("하이", "날짜" +  todayPraysList.get(0).get("yymm"));
            Log.d("하이", "날짜" +  strCurYear);

            if (todayPraysList.get(0).get("yymm").equals(strCurYear) && todayPraysList.get(0).get("day").equals(strCurDay)) {
                today_pray_content.setText(todayPraysList.get(0).get("pray"));
                today_pray = today_pray_content.getText().toString();

                ((MainActivity) getActivity()).today_pray_content = today_pray;

            } else {
                Toast.makeText(getContext(), "기도제목을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }

            Log.d("하이", "fragment1  onPostExecute" + today_pray);

        }
    }

    @Override
    public void onResume() {

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
