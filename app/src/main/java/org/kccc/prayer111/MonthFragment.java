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

public class MonthFragment extends Fragment {

    TextView month_pray_title;
    TextView month_pray_content;

    private String TAG = MonthFragment.class.getSimpleName();

    private static String url = "http://api.kccc.org/AppAjax/111prayer/index.php?mode=getMonth";

    ArrayList<HashMap<String, String>> monthPraysList;

    SimpleDateFormat curYearFormat;
    SimpleDateFormat curMonthFormat;

    String month_pray;

    boolean loadingCheck = false;

    public MonthFragment() {

    }

    public static MonthFragment newInstance() {
        MonthFragment fragment = new MonthFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // fragment_month.xml 파일을 view 객체로 container 에 붙여서 생성
        View view = inflater.inflate(R.layout.fragment_month, container, false);

        Log.d("하이", "getID" + view.getId());


        monthPraysList = new ArrayList<>();

        // 한국지역의 연도와 달을 계산하여 각각의 변수에 대입
        curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);

        // view 에 있는 Textview 를 컨트롤하기 위해 id 값으로 불러와 변수에 대입
        month_pray_title = (TextView) view.findViewById(R.id.month_pray_title);
        month_pray_content = (TextView) view.findViewById(R.id.month_pray_content);

        // title 과 Content 에 각각의 폰트를 적용하기 위해 Type Setting
        Typeface typefaceTitle = Typeface.createFromAsset(getContext().getAssets(), "tvN_OTF_Light.otf");
        Typeface typefaceContent = Typeface.createFromAsset(getContext().getAssets(), "NotoSansCJKkr_Light.otf");

        // 각 Textview 에 폰트 설정
        month_pray_title.setTypeface(typefaceTitle);
        month_pray_content.setTypeface(typefaceContent);

        // 앱을 시작후 화면이 처음 생성 되어있는지 아닌지 체크
        if (!loadingCheck) {

            // 처음
            new GetMonthPrays().execute();
        } else {
            month_pray_content.setText(month_pray);
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            // 이달의 기도 페이지가 보일 때
        } else {
            // 이달의 기도 페이지가 보이지 않을 때
        }
    }

    // 이달의 기도 내용을 읽어와서 화면에 출력하기 위한 AsyncTask
    private class GetMonthPrays extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            // HttpHandler 객체를 생성
            HttpHandler sh = new HttpHandler();

            // 객체의 makeServiceCall 함수에 api url 을 매개변수로 넘겨주고 해당 json 내용을 String 값으로 리턴받기
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {

                try {

                    // String 값을 jsonObject 객체로 생성
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    // jsonObject 객체에서 result 값을 가진 키의 value 값을 jsonObject 객체로 생성
                    JSONObject object = jsonObject.getJSONObject("result");

                    // 각 String 변수에 object 객체에서 각각 key 값을 가진 value 값의 내용을 삽입
                    String pray = object.getString("prayer");
                    String yymm = object.getString("yymm");

                    // HashMap 객체를 생성하여 각각의 Key 값으로 각각의 Value 값을 삽입
                    HashMap<String, String> monthPray = new HashMap<>();
                    monthPray.put("pray", pray);
                    monthPray.put("yymm", yymm);

                    // HashMap 객체를 List 객체에 add
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

            // 현재 시간 및 날짜 확인하기
            long now = System.currentTimeMillis();
            Date date = new Date(now);

            // 오늘의 날짜를 String 값으로 정리 (연도+월)
            String strCurMonth = curYearFormat.format(date) + curMonthFormat.format(date);

            // API 를 잘 불러왔다면 true 값으로 설정하여 두번째부터는 안불러올 수 있도록 설정
            loadingCheck = true;

            // List 값에서 날짜값을 비교하여 일치할 경우 content 내용을 불러와 화면 Text 에 뿌려주기
            try {

                if (monthPraysList.get(0).get("yymm").equals(strCurMonth)) {

                    month_pray_content.setText(monthPraysList.get(0).get("pray"));
                    month_pray = month_pray_content.getText().toString();

                    ((MainActivity) getActivity()).month_pray_content = month_pray;

                } else {

                    Toast.makeText(getContext(), "기도제목을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
