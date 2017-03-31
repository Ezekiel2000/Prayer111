package org.kccc.prayer111;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private TextView textDate;
    private GridAdapter gridAdapter;
    private ArrayList<String> dayList;
    private ArrayList<String> dayPrayCheckedList;
    private GridView gridView;
    private Calendar mCal;

    private AlertDialog.Builder alert_today;

    String json;
    ArrayList checkday = new ArrayList();

    private boolean today_checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }
        Log.d("하이", "시스템바 변경");

        textDate = (TextView) findViewById(R.id.text_date);
        gridView = (GridView) findViewById(R.id.grid_view);

        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        textDate.setText(curYearFormat.format(date) + "." + curMonthFormat.format(date));

        dayList = new ArrayList<String>();
        dayPrayCheckedList = new ArrayList<String>();
        dayList.add("일");
        dayList.add("월");
        dayList.add("화");
        dayList.add("수");
        dayList.add("목");
        dayList.add("금");
        dayList.add("토");

        for (int i = 0 ; i < 40 ; i++) {
            dayPrayCheckedList.add("0");
        }
        dayPrayCheckedList.add(10, "1");

        json = PropertyManager.getInstance().getUserCalendarCheck();

        Log.d("하이", "json 의 값 : " + json);

        if (json != null) {

            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0 ; i < array.length(); i++) {
                    String check = array.getString(i);
                    if (check.isEmpty()) {
                        check = "false";
                    }
                    checkday.add(i, check);
                }

            } catch (JSONException e) {

            }

        }

        mCal = Calendar.getInstance();

        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);

        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }

        setCalendarDate(mCal.get(Calendar.MONTH) + 1);


       gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        });

        Log.d("하이", String.valueOf(today_checked) + 0);

        Intent intent = getIntent();
        today_checked = intent.getBooleanExtra("checked", today_checked);

        Log.d("하이", String.valueOf(today_checked) + 1);


        Log.d("하이", String.valueOf(today_checked) + 3);


        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);

    }

    private void setCalendarDate(int month) {

        mCal.set(Calendar.MONTH, month, -1);

        for (int j = 0; j < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); j++) {
            dayList.add("" + (j + 1));
            Log.d("하이", "j의 값 :" + j + dayList.toString());
            Log.d("하이", "month 의 값 :" + month);
        }

    }

//    private void savePreference() {
//        SharedPreferences pref = getPreferences("pref", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//
//    }


    private class GridAdapter extends BaseAdapter {

        private final List<String> list;
        private final LayoutInflater inflater;


        public GridAdapter(Context context, List<String> list) {

            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
                holder = new ViewHolder();

                holder.textItemGridView = (TextView) convertView.findViewById(R.id.text_item_grid_view);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textItemGridView.setText("" + getItem(position));

            mCal = Calendar.getInstance();

            Integer today = mCal.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(1);

            Log.d("하이", "포지션 : " + position);
            Log.d("하이", "체크값 : " + dayPrayCheckedList.get(position));

            Log.d("하이", "포지션 : " + getItem(position));

//            for (int i = 0; i < checkday.size(); i++) {

//                if (getItem(position).toString().equals(String.valueOf(i))) {

//                    if (checkday.get(i).toString().equals("true")) {
//
//                        Log.d("하이", "찍힌 날 : " + checkday.get(i) + i);
//                        holder.textItemGridView.setTextColor(getResources().getColor(R.color.colorCalender));
//                        holder.textItemGridView.setBackground(getResources().getDrawable(R.drawable.calendar_stamp));
//
//                    }

//                }

//                Log.d("하이", "안찍힌 날 : " + checkday.get(i) + i);

//            }


            return convertView;
        }



    }

    private class ViewHolder {
        TextView textItemGridView;
    }

    @Override
    public void onBackPressed() {

        if (today_checked == true) {

            Intent reIntent = new Intent();
            reIntent.putExtra("checked", today_checked);
            setResult(Activity.RESULT_OK, reIntent);
            finish();

        } else if (today_checked == false) {

            setResult(Activity.RESULT_CANCELED);
            finish();

        }

    }
}
