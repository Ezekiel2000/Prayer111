package org.kccc.prayer111;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CommentListActivity extends AppCompatActivity {

    final static int LOAD_ROW = 30;

    List<ListCommentData> listCommentDatas;
    ListCommentData[] data;
    ListCommentData[] addData;

    int start = 1;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private EditText input_comment;
    private ImageView btn_ok;

    ProgressDialog progressDialog;

    private static String getUrl = "http://api.kccc.org/AppAjax/111prayer/index.php?mode=getComment";
    private static String setUrl = "http://api.kccc.org/AppAjax/111prayer/index.php";
    String prayNumber;
    String cmtNumber;
    String cmtContent;
    String userId;

    Boolean endPsotion = false;

    CommentListViewAdapter commentListViewAdapter;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }
        Log.d("하이", "시스템바 변경");

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.comment_list_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        input_comment = (EditText) findViewById(R.id.input_comment);
        btn_ok = (ImageView) findViewById(R.id.btn_ok);

        Intent intent = getIntent();
        prayNumber = intent.getStringExtra("prayNumber");

        Log.d("하이", "클릭한 중보기도 넘버 : " + prayNumber);
        userId = PropertyManager.getInstance().getUserId();

        listCommentDatas = new ArrayList<>();
        commentListViewAdapter = new CommentListViewAdapter(getApplicationContext(), listCommentDatas, R.layout.activity_comment_list);
        new GetCommentList().execute();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = new Intent(CommentListActivity.this, SignInActivity.class);

                cmtContent = ((EditText) findViewById(R.id.input_comment)).getText().toString();

                if (userId == null) {

                    signInIntent.putExtra("position", "cmt");
                    startActivity(signInIntent);

                } else {

                    new Thread() {
                        @Override
                        public void run() {

                            HttpURLConnection conn = null;

                            try {

                                URL url = new URL(setUrl);
                                conn = (HttpURLConnection) url.openConnection();
                                conn.setDoInput(true);
                                conn.setDoOutput(true);
                                conn.setChunkedStreamingMode(0);
                                conn.setRequestMethod("POST");

                                OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                                writer.write( "mode=setComment"
                                        + "&userId=" + userId
                                        + "&prayNo=" + prayNumber
                                        + "&comment=" + cmtContent);
                                writer.flush();
                                writer.close();
                                out.close();

                                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                                StringBuilder builder = new StringBuilder();
                                String line = null;
                                while ((line =reader.readLine()) != null) {
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

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

//                                    ListCommentData data = new ListCommentData(prayNumber, profile, name, date, content);
//                                    listCommentDatas.add(data);

                                    commentListViewAdapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(commentListViewAdapter);
                                    input_comment.setText("");
                                }
                            });

                            Intent okIntent = new Intent(getBaseContext(), MainActivity.class);
                            okIntent.putExtra("position", "cmt");
                            startActivity(okIntent);
                        }
                    }.start();
                }
            }
        });
    }




    private class GetCommentList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CommentListActivity.this, null, "로딩중입니다.", true, false);
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpHandler sh = new HttpHandler();

            getUrl = getUrl + "&userId=" + userId +"&prayNo=" + prayNumber;
            String jsonStr = sh.makeServiceCall(getUrl);

            Log.d("하이", "getUrl : " + getUrl);
            Log.d("하이", "jsonStr : " + jsonStr);

            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                Log.d("하이", "jsonStr : " + jsonObject);
                String dataJson = jsonObject.getString("result");

                JSONArray jsonArray = new JSONArray(dataJson);

                jsonArray = soryJsonArray(jsonArray);

                for (int i = 0 ; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);

                    String commentNumber = object.getString("no");
                    String prayNumber = object.getString("keyno");
                    String id = object.getString("id");
                    String name = object.getString("name");
                    String content = object.getString("memo");
                    String profile = object.getString("photo");
                    String date = object.getString("indate");

                    cmtNumber = commentNumber;

                    data = new ListCommentData[jsonArray.length()];

                    data[i] = new ListCommentData(commentNumber, profile, name, date, content);
                    listCommentDatas.add(data[i]);

                }


            } catch (JSONException e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            recyclerView.setAdapter(commentListViewAdapter);
            commentListViewAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            recyclerView.setAdapter(new CommentListViewAdapter(getApplicationContext(), listCommentDatas, R.layout.activity_comment_list));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    // 내림차순으로 정렬하는 함수
    public static JSONArray soryJsonArray(JSONArray array) {
        List<JSONObject> objects = new ArrayList<JSONObject>();
        try {
            for (int i = 0 ; i < array.length() ; i++) {
                objects.add(array.getJSONObject(i));
            }
            Collections.sort(objects, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    String lid = "", rid = "";
                    try {
                        lid = o1.getString("no");
                        rid = o2.getString("no");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return  rid.compareTo(lid);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONArray(objects);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private class CommentListViewAdapter extends RecyclerView.Adapter<CommentListViewAdapter.ViewHolder> {

        private Context context;
        private List<ListCommentData> listCommentDatas;
        int list_comment;

        public CommentListViewAdapter(Context context, List<ListCommentData> listCommentDatas, int list_comment) {
            this.context = context;
            this.listCommentDatas = listCommentDatas;
            this.list_comment = list_comment;
        }


        @Override
        public CommentListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, null);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentListViewAdapter.ViewHolder holder, int position) {

            ListCommentData commentData = listCommentDatas.get(position);

            Glide.with(context)
                    .load(commentData.getComment_profileImage())
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.comment_profile);

            holder.comment_name.setText(commentData.comment_name);
            holder.comment_date.setText(commentData.comment_date);
            holder.comment_content.setText(commentData.comment_content);

            holder.comment_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("하이", "선택한 놈 : " + position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setTitle("댓글 삭제")
                            .setMessage("댓글을 삭제하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread() {
                                        @Override
                                        public void run() {

                                            HttpURLConnection conn = null;

                                            String url = setUrl;

                                            try {

                                                URL url1 = new URL(setUrl);
                                                conn = (HttpURLConnection) url1.openConnection();
                                                conn.setDoInput(true);
                                                conn.setDoOutput(true);
                                                conn.setChunkedStreamingMode(0);
                                                conn.setRequestMethod("POST");

                                                OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                                                writer.write("mode=removeComment"
                                                        + "&userId=" + userId
                                                        + "&commentNo=" + commentData.getPray_number());
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

                                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    commentListViewAdapter.remove(new ListCommentData(commentData.getPray_number(),
                                            commentData.getComment_profileImage(), commentData.getComment_name(), commentData.getComment_date(), commentData.getComment_content()), position);

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

                }
            });

            Log.d("하이", "코멘트 갯수 : " + position);
            Log.d("하이", "리스트 갯수 : " + listCommentDatas.size());

            if (position == listCommentDatas.size()-1 ) {

                if (!endPsotion) {

                    loadDataList();

                }

            }

        }

        public void loadDataList() {

            int row = (start++) * LOAD_ROW;

            String url = setUrl + "?mode=getComment" + "&startRow=" + row + "&userId=" + userId + "&prayNo=" + prayNumber;

            Log.d("하이", "url : " + setUrl);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Void... params) {

                    HttpHandler sh = new HttpHandler();

//                    String url = setUrl + "&userId=" + userId +"&prayNo=" + prayNumber;
                    String jsonStr = sh.makeServiceCall(url);

                    Log.d("하이", "getUrl : " + url);
                    Log.d("하이", "jsonStr : " + jsonStr);

                    try {

                        JSONObject jsonObject = new JSONObject(jsonStr);
                        Log.d("하이", "jsonStr : " + jsonObject);
                        String dataJson = jsonObject.getString("result");

                        JSONArray jsonArray = new JSONArray(dataJson);

                        jsonArray = soryJsonArray(jsonArray);

                        for (int i = 0 ; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            String commentNumber = object.getString("no");
                            String prayNumber = object.getString("keyno");
                            String id = object.getString("id");
                            String name = object.getString("name");
                            String content = object.getString("memo");
                            String profile = object.getString("photo");
                            String date = object.getString("indate");

                            cmtNumber = commentNumber;

                            addData = new ListCommentData[jsonArray.length()];

                            addData[i] = new ListCommentData(commentNumber, profile, name, date, content);
                            listCommentDatas.add(addData[i]);

                        }


                    } catch (JSONException e) {

                        endPsotion = true;
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
            return this.listCommentDatas.size();
        }


        public void remove(ListCommentData data, int position) {

            try {

                Log.d("하이", "리스트 번호" + listCommentDatas.indexOf(data));

                int pos = position;
                listCommentDatas.remove(pos);
                notifyItemRemoved(pos);

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView comment_profile;
            TextView comment_name;
            TextView comment_date;
            TextView comment_content;
            ImageView comment_delete;


            public ViewHolder(View view) {
                super(view);

                comment_profile = (ImageView) view.findViewById(R.id.comment_profile);
                comment_name = (TextView) view.findViewById(R.id.comment_name);
                comment_date = (TextView) view.findViewById(R.id.comment_date);
                comment_content = (TextView) view.findViewById(R.id.comment_content);
                comment_delete = (ImageView) view.findViewById(R.id.comment_delete);

            }

        }

    }

    @Override
    public void onBackPressed() {

        Intent okIntent = new Intent(getBaseContext(), MainActivity.class);
        okIntent.putExtra("position", "cmt");
        startActivity(okIntent);
        finish();
    }
}
