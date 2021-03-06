package org.kccc.prayer111;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by ezekiel on 2017. 2. 2..
 */

public class ListDataAdapter extends RecyclerView.Adapter<ListDataAdapter.ViewHolder> {

    final static int MIN_LINE = 10;
    final static int MAX_LINE = 500;

    final static int LOAD_ROW = 30;
    int start = 1;

    Context context;
    List<ListData> listData;
    ListData[] datas;
    int list_intercession;
    Boolean icon_heart_clicked = true;
    String heart_check;
    String pray_number;

    SimpleDateFormat curMonthFormat;
    SimpleDateFormat curDayFormat;

    String userId = "";

    String dateMonth;
    String dateDay;

    Boolean endPosition = false;

    public ProgressBar progressBar;

    Handler handler = new Handler();

    private String postUrl = "http://api.kccc.org/AppAjax/111prayer/index.php";
    private String url = "http://api.kccc.org/AppAjax/111prayer/index.php?mode=getTogether";
    private String addurl = "http://api.kccc.org/AppAjax/111prayer/index.php?mode=getTogether&startRow=";

    public ListDataAdapter(Context context, List<ListData> listData, int list_intercession) {
        this.context = context;
        this.listData = listData;
        this.list_intercession = list_intercession;
    }

    @Override
    public ListDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list, null);

        Log.d("하이", "CreateViewholder");

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        CharSequence info[] = new CharSequence[] {"메세지로 공유하기", "카카오톡 공유하기", "기도제목 신고하기"};
        ListData data = listData.get(position);

        SimpleDateFormat original_format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        curMonthFormat = new SimpleDateFormat("M");
        curDayFormat = new SimpleDateFormat("d");

        try {
            Date original_date = original_format.parse(data.getDate());
            dateMonth = curMonthFormat.format(original_date);
            dateDay = curDayFormat.format(original_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("하이", "원래 날짜 : " + data.getDate() );
        Log.d("하이", "날짜 : " + dateMonth + dateDay);

        holder.text_date.setText(data.getDate());

        // glide 라이브러리 사용하여 원형 프로필 만들기
        Glide.with(holder.profile.getContext())
                .load(data.getProfileImage())
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.profile);

        Log.d("하이", "Context : " + context);

        // holder 에 각 item 에 해당하는 내용들 set
        holder.text_name.setText(data.getName());

        holder.text_content.setText(data.getContent());

        // textView가 layout에 그려지지 않아 getLine이 0으로 반환되어서 post로 확인하고 반환시킴
        holder.text_content.post(new Runnable() {
            @Override
            public void run() {

                //  중보기도 내용 글이 MIN_LINE 보다 클 경우 더 보기로 MIN_LINE 길이로 줄이기
                holder.text_more.setVisibility(View.GONE);
                if (holder.text_content.getLineCount() >= MIN_LINE) {
                    holder.text_content.setMaxLines(MIN_LINE);
                    holder.text_more.setVisibility(View.VISIBLE);
                }
            }
        });

        // 더보기 버튼을 클릭했을 때 MAX_LINE 길이로 늘려서 보여줌
        holder.text_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                holder.text_content.setMaxLines(MAX_LINE);
            }
        });

        // 이미지가 있을 경우 이미지 뷰를 보여줌
        if (data.getImageInput() != null) {
            Glide.with(holder.image_input.getContext())
                    .load(data.getImageInput())
                    .override(1500, 1500)
                    .into(holder.image_input);
            holder.image_input.setVisibility(View.VISIBLE);
        }

        Log.d("하이", "이메일 : " + PropertyManager.getInstance().getUserId());


        Log.d("하이", "Id : " + data.getEmail());

        Log.d("하이", "로그인 채크 : " + PropertyManager.getInstance().getLoginCheck());

        if (PropertyManager.getInstance().getLoginCheck()) {

                holder.card_delete.setVisibility(View.VISIBLE);
        }

       holder.card_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("하이", "이메일 : " + PropertyManager.getInstance().getUserId());
                Log.d("하이", "Id : " + data.getEmail());
                Log.d("하이", "로그인 채크 : " + PropertyManager.getInstance().getLoginCheck());

                if (PropertyManager.getInstance().getUserId().equals(data.getEmail()))
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setTitle("중보기도 삭제")
                            .setMessage("중보기도를 삭제하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (PropertyManager.getInstance().getLoginCheck()) {

                                        new Thread() {
                                            @Override
                                            public void run() {

                                                HttpHandler sh = new HttpHandler();

                                                String url = postUrl + "?mode=removeTogetherPray" + "&userId=" + PropertyManager.getInstance().getUserId() + "&prayNo=" + data.getNumber();

                                                String jsonStr = sh.makeServiceCall(url);

                                                if (jsonStr != null) {

                                                    try {

                                                        JSONObject jsonObject = new JSONObject(jsonStr);
                                                        JSONObject object = jsonObject.getJSONObject("result");

                                                        String result = object.getString("msg");

                                                    } catch (JSONException e) {
                                                        Log.e("하", "Json parsing error:" + e.getMessage());
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }

                                            }

                                        }.start();

                                        remove(new ListData(data.getNumber(), data.getProfileImage(), data.getEmail(), data.getName(),
                                                data.getDate(), data.getContent(), data.getImageInput(), data.getWarn(), data.getPrayerNumber(),
                                                data.getCommentNumber(), data.getChkHeart()), position);
                                        Toast.makeText(v.getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();


                                    } else {

                                        Toast.makeText(v.getContext(), "로그인하세요.", Toast.LENGTH_SHORT).show();

                                    }

                                }

                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Toast.makeText(v.getContext(), "내가 쓴 글이 아닙니다. 삭제 불가능", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.text_prayer_number.setText(String.valueOf(data.getPrayerNumber()));
        holder.text_comment_number.setText(String.valueOf(data.getCommentNumber()));

        Log.d("하이", "getChkHeart: " + data.getChkHeart() );

        if (data.getChkHeart() == 0) {

            holder.icon_heart.setImageResource(R.drawable.ic_heart);

        } else  {

            holder.icon_heart.setImageResource(R.drawable.ic_heart_red);
        }

        holder.icon_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("하이", "heart_check : " + heart_check);


                Log.d("하이", "icon_heart_clicked: " + icon_heart_clicked);

                if (PropertyManager.getInstance().getLoginCheck()) {

                    final Handler handler = new Handler();
                    new Thread() {
                        @Override
                        public void run() {

                            HttpURLConnection conn = null;

                            try {

                                URL url = new URL(postUrl);
                                conn = (HttpURLConnection) url.openConnection();
                                conn.setDoInput(true);
                                conn.setDoOutput(true);
                                conn.setChunkedStreamingMode(0);
                                conn.setRequestMethod("POST");

                                OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                                writer.write("mode=setHeart"
                                        + "&UserId=" + PropertyManager.getInstance().getUserId()
                                        + "&prayNo=" + data.getNumber());
                                writer.flush();
                                writer.close();
                                out.close();

                                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                                StringBuilder builder = new StringBuilder();
                                String line = null;
                                while ((line = reader.readLine()) != null) {
                                    if (builder.length() > 0) {
                                        builder.append("\n");
                                    }
                                    builder.append(line);
                                }

                                Log.d("하이", builder.toString());

                                JSONObject jsonObject = new JSONObject(builder.toString());
                                String json = jsonObject.getString("result");

                                JSONObject object = new JSONObject(json);
                                heart_check = object.getString("chkHeart");
                                pray_number = object.getString("heart");

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (data.getChkHeart() == 1) {

                                            upDateList(new ListData(data.getNumber(), data.getProfileImage(), data.getEmail(), data.getName(),
                                                    data.getDate(), data.getContent(), data.getImageInput(), data.getWarn(), data.getPrayerNumber()-1,
                                                    data.getCommentNumber(), 0), position);

                                            Toast.makeText(v.getContext(), "좋아요를 클릭을 해제하셨습니다.", Toast.LENGTH_SHORT).show();

                                        } else {

                                            upDateList(new ListData(data.getNumber(), data.getProfileImage(), data.getEmail(), data.getName(),
                                                    data.getDate(), data.getContent(), data.getImageInput(), data.getWarn(), data.getPrayerNumber()+1,
                                                    data.getCommentNumber(), 1), position);

                                            Toast.makeText(v.getContext(), "좋아요를 클릭하셨습니다.", Toast.LENGTH_SHORT).show();


                                        }

                                    }
                                });


                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (conn != null) {
                                    conn.disconnect();
                                }
                            }

                        }

                    }.start();

                    holder.icon_heart.setImageResource(R.drawable.ic_heart_red);
                    icon_heart_clicked = false;



                } else {

                    Toast.makeText(context, "로그인을 하시요.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        holder.icon_comment.setOnClickListener(v -> {
            Intent commentIntent = new Intent();

            if (PropertyManager.getInstance().getLoginCheck()) {

                commentIntent = new Intent(holder.icon_comment.getContext(), CommentListActivity.class);
                commentIntent.putExtra("prayNumber", data.getNumber());
                v.getContext().startActivity(commentIntent);

            } else {

                Toast.makeText(v.getContext(), "로그인을 하시요.", Toast.LENGTH_SHORT).show();

            }

        });

        // 공유 버튼을 클릭해을 경우 dialog창을 뜨게 함
        holder.icon_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 로그인을 안해서 name값이 없을 경우 로그인하라고 Toast 띄움
                if (PropertyManager.getInstance().getLoginCheck()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("공유하기");
                    builder.setItems(info, (dialog, which) -> {
                        switch (which) {
                            case 0:

                                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                smsIntent.putExtra("sms_body", holder.text_content.getText());
                                smsIntent.setType("vnd.android-dir/mms-sms");

                                try {
                                    Toast.makeText(v.getContext(), "메세지 공유", Toast.LENGTH_SHORT).show();
                                    v.getContext().startActivity(smsIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(v.getContext(), "문자 어플리케이션이 없습니다.", Toast.LENGTH_SHORT).show();
                                }


                                break;
                            case 1:

                                Log.d("하이", "선택한 놈 : " + holder.getAdapterPosition());
                                Intent kakaoIntent = new Intent(Intent.ACTION_SEND);
                                kakaoIntent.setType("text/plain");
                                kakaoIntent.putExtra(Intent.EXTRA_SUBJECT, "[" + holder.text_name.getText() + " 의 중보 기도]\n");

                                kakaoIntent.putExtra(Intent.EXTRA_TEXT, holder.text_content.getText());
                                kakaoIntent.setPackage("com.kakao.talk");

                                try {
                                    Toast.makeText(v.getContext(), "카카오톡 공유", Toast.LENGTH_SHORT).show();
                                    v.getContext().startActivity(kakaoIntent);
                                } catch (Exception e) {
                                    Toast.makeText(v.getContext(), "카카오톡이 설치가 안되어있습니다.", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case 2:

                                new Thread() {
                                    @Override
                                    public void run() {

                                        HttpURLConnection conn = null;

                                        try {

                                            URL url = new URL(postUrl);
                                            conn = (HttpURLConnection) url.openConnection();
                                            conn.setDoInput(true);
                                            conn.setDoOutput(true);
                                            conn.setChunkedStreamingMode(0);
                                            conn.setRequestMethod("POST");

                                            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                                            writer.write("mode=setWarn"
                                                    + "&userId=" + PropertyManager.getInstance().getUserId()
                                                    + "&prayNo=" + data.getNumber());
                                            writer.flush();
                                            writer.close();
                                            out.close();

                                            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                                            StringBuilder builder = new StringBuilder();
                                            String line = null;
                                            while ((line = reader.readLine()) != null) {
                                                if (builder.length() > 0) {
                                                    builder.append("\n");
                                                }
                                                builder.append(line);
                                            }

                                            Log.d("하이", builder.toString());

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            if (conn != null) {
                                                conn.disconnect();
                                            }
                                        }

                                    }
                                }.start();

                                remove(new ListData(data.getNumber(), data.getProfileImage(), data.getEmail(), data.getName(),
                                        data.getDate(), data.getContent(), data.getImageInput(), data.getWarn(), data.getPrayerNumber(),
                                        data.getCommentNumber(), data.getChkHeart()), position);
                                Toast.makeText(v.getContext(), "해당 기도제목을 신고하였습니다.", Toast.LENGTH_SHORT).show();

                                break;
                        }
                        dialog.dismiss();
                    });

                    builder.show();

                // 로그인을 했을 경우에만 Dialog창을 띄움
                } else {

                    Toast.makeText(v.getContext(), "로그인을 하시요.", Toast.LENGTH_SHORT).show();

                }

            }

        });

        Log.d("하이", "포지션 값 : " + position);
        Log.d("하이", "사이즈 값 : " + listData.size());

        if (position == listData.size()-1 && listData.size() >= (LOAD_ROW * start) ) {

            if (!endPosition) {
                loadDataList();
            }

        }


    }


    public void remove(ListData data, int position) {

        try {

            int pos = position;
            listData.remove(pos);
            notifyItemRemoved(pos);

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    public void upDateList(ListData data, int position) {
        listData.set(position, data);
        notifyDataSetChanged();
    }

    public void loadDataList() {

        int row = (start++) * LOAD_ROW;
        String setUrl = addurl + row;

        Log.d("하이", "url : " + setUrl);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                HttpHandler sh = new HttpHandler();

                String jsonStr = sh.makeServiceCall(setUrl);
                Log.d("하이", "체크체크 : " +setUrl);

                try {

                    JSONObject jsonObject = new JSONObject(jsonStr);
                    String dataJson = jsonObject.getString("result");

                    JSONArray jsonArray = new JSONArray(dataJson);

                    for (int i = 0; i < jsonArray.length() ; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

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
                        int chkHeart = object.getInt("chkHeart");

                        Log.d("하이", "체크체크 : " + object.toString());

                        datas = new ListData[jsonArray.length()];

                        // 신고당한 리스트(warn = 1인경우)는 add 하지 않음
                        if (warn == 0) {

                            datas[i] = new ListData(number, profile, email, name, date, content, imageInput, warn, prayNumber, commentNumber, chkHeart);
                            listData.add(datas[i]);

                        }
                    }

                } catch (JSONException e) {

                    endPosition = true;
                    e.printStackTrace();

                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyDataSetChanged();
            }
        }.execute();

    }



    @Override
    public int getItemCount() {
        return this.listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView text_date;
        TextView text_name;
        TextView text_content;
        TextView text_prayer_number;
        TextView text_comment_number;
        TextView text_more;
        ImageView image_input;
        ImageView card_delete;
        ImageView icon_heart;
        ImageView icon_comment;
        ImageView icon_share;
        CardView card_list;

        public ViewHolder(View view) {
            super(view);

            profile = (ImageView) view.findViewById(R.id.image_profile);
            text_date = (TextView) view.findViewById(R.id.text_date);
            text_name = (TextView) view.findViewById(R.id.text_name);
            text_content = (TextView) view.findViewById(R.id.text_content);
            text_more = (TextView) view.findViewById(R.id.text_more);
            text_prayer_number = (TextView) view.findViewById(R.id.text_prayer_number);
            text_comment_number = (TextView) view.findViewById(R.id.text_comment_number);
            image_input = (ImageView) view.findViewById(R.id.image_input);
            card_delete = (ImageView) view.findViewById(R.id.pray_delete);

            icon_heart = (ImageView) view.findViewById(R.id.icon_heart);
            icon_comment = (ImageView) view.findViewById(R.id.icon_speech);
            icon_share = (ImageView) view.findViewById(R.id.icon_share);
            card_list = (CardView) view.findViewById(R.id.card_list);
        }
    }
}
