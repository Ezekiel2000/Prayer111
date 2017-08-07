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
import java.util.Locale;

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
    Boolean loadingCheck = false;

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

        todayPraysList = new ArrayList<>();

        today_pray_title = (TextView) view.findViewById(R.id.today_pray_title);
        today_pray_content = (TextView) view.findViewById(R.id.today_pray_content);

        Typeface typefaceTitle = Typeface.createFromAsset(getContext().getAssets(), "tvN_OTF_Light.otf");
        Typeface typefaceContent = Typeface.createFromAsset(getContext().getAssets(), "NotoSansCJKkr_Light.otf");

        today_pray_title.setTypeface(typefaceTitle);
        today_pray_content.setTypeface(typefaceContent);

        curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        curDayFormat = new SimpleDateFormat("d", Locale.KOREA);

        // 처음 한번만 API로 서버에서 기도제목을 불러오고 2번째 부터는 처음 불러왔을 때 기도제목을 저장하여 보여줌
        if (!loadingCheck) {
            new GetTodayPrays().execute();
        } else {
            today_pray_content.setText(today_pray);
        }

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

                    JSONObject jsonObject = new JSONObject(jsonStr);

                    JSONObject object = jsonObject.getJSONObject("result");

                    String pray = object.getString("prayer");
                    String yymm = object.getString("yymm");
                    String day = object.getString("day");

                    HashMap<String, String> todayPray = new HashMap<>();

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

            long now = System.currentTimeMillis();
            Date date = new Date(now);

            String strCurYear = curYearFormat.format(date) + curMonthFormat.format(date);
            String strCurDay = curDayFormat.format(date);

            try {

                if (todayPraysList.get(0).get("yymm").equals(strCurYear) && todayPraysList.get(0).get("day").equals(strCurDay)) {
                    today_pray_content.setText(todayPraysList.get(0).get("pray"));
                    today_pray = today_pray_content.getText().toString();

                    ((MainActivity) getActivity()).today_pray_content = today_pray;

                } else {
                    Toast.makeText(getContext(), "기도제목을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }

            } catch (IndexOutOfBoundsException e) {

            }
            loadingCheck = true;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.d("하이", "페이지 1번 보인다.");
        } else {
            Log.d("하이", "페이지 1번 안보인다.");
        }
    }

    @Override
    public void onResume() {

        getActivity().findViewById(R.id.fab_write).setVisibility(View.GONE);
        getActivity().findViewById(R.id.multiple_action).setVisibility(View.VISIBLE);

        super.onResume();
    }

    @Override
    public void onPause() {

        getActivity().findViewById(R.id.fab_write).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.multiple_action).setVisibility(View.GONE);

        super.onPause();
    }



}
