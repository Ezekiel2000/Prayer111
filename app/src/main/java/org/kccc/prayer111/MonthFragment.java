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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ezekiel on 2017. 2. 2..
 */

public class MonthFragment extends Fragment {

    TextView month_pray_title;
    TextView month_pray_content;

    private String TAG = MonthFragment.class.getSimpleName();

    private static String url = "http://api.kccc.org/AppAjax/111prayer/index.php?mode=getMonth";

    ArrayList<HashMap<String, String>> monthPraysList;

    SimpleDateFormat curYearFormat;
    SimpleDateFormat curMonthFormat;

    String month_pray;

    public MonthFragment() {

    }

    public static MonthFragment newInstance() {
        MonthFragment fragment = new MonthFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_month, container, false);

        Log.d("하이", "getID" + view.getId());

        monthPraysList = new ArrayList<>();

        curYearFormat = new SimpleDateFormat("yyyy");
        curMonthFormat = new SimpleDateFormat("MM");

        month_pray_title = (TextView) view.findViewById(R.id.month_pray_title);
        Typeface typefaceTitle = Typeface.createFromAsset(getContext().getAssets(), "tvN_OTF_Light.otf");
        month_pray_title.setTypeface(typefaceTitle);

        month_pray_content = (TextView) view.findViewById(R.id.month_pray_content);
        Typeface typefaceContent = Typeface.createFromAsset(getContext().getAssets(), "NotoSansCJKkr_Light.otf");
        month_pray_content.setTypeface(typefaceContent);

        new GetMonthPrays().execute();

        return view;
    }

    private class GetMonthPrays extends AsyncTask<Void, Void, Void> {

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

                    JSONObject jsonObject = new JSONObject(jsonStr);

                    JSONObject object = jsonObject.getJSONObject("result");

                    String pray = object.getString("prayer");
                    String yymm = object.getString("yymm");
                    String day = object.getString("day");


                    HashMap<String, String> monthPray = new HashMap<>();

                    monthPray.put("pray", pray);
                    monthPray.put("yymm", yymm);

                    monthPraysList.add(monthPray);

                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error" + e.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            long now = System.currentTimeMillis();
            Date date = new Date(now);

            String strCurMonth = curYearFormat.format(date) + curMonthFormat.format(date);

            if (monthPraysList.get(0).get("yymm").equals(strCurMonth)) {

                month_pray_content.setText(monthPraysList.get(0).get("pray"));
                month_pray = month_pray_content.getText().toString();

                ((MainActivity) getActivity()).month_pray_content = month_pray;

            } else {

                Toast.makeText(getContext(), "기도제목을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        }

    }



    @Override
    public void onResume() {

        Log.d("하이", "fragment2  Resume");


        super.onResume();
    }

    @Override
    public void onPause() {

        Log.d("하이", "fragment2  onPause");

        super.onPause();
    }
}
