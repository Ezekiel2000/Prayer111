package org.kccc.prayer111;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

    int ITEM_SIZE = 2;

    List<ListCommentData> listCommentDatas;
    ListCommentData[] data;

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
    String email;

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
        email = intent.getStringExtra("email");

        email = PropertyManager.getInstance().getUserEmail();

        listCommentDatas = new ArrayList<>();

        new GetCommentList().execute();


        recyclerView.setAdapter(new CommentListViewAdapter(getApplicationContext(), listCommentDatas, R.layout.activity_comment_list));
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

            getUrl = getUrl +"&prayNo=" + prayNumber;
            String jsonStr = sh.makeServiceCall(getUrl);

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
                    String chkHeart = object.getString("chkHeart");

                    cmtNumber = commentNumber;

                    data = new ListCommentData[jsonArray.length()];

                    data[i] = new ListCommentData(prayNumber, profile, name, date, content);
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

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent signInIntent = new Intent(CommentListActivity.this, SignInActivity.class);

                    cmtContent = ((EditText) findViewById(R.id.input_comment)).getText().toString();

                    if (email == null) {

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
                                            + "&userId=" + email
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
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                        recyclerView.setAdapter(new CommentListViewAdapter(CommentListActivity.this, listCommentDatas, R.layout.activity_comment_list));

                                    }
                                });




                            }




                        }.start();

                        input_comment.setText("");

                        recyclerView.setAdapter(new CommentListViewAdapter(CommentListActivity.this, listCommentDatas, R.layout.activity_comment_list));

                    }

                }

            });


            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.setAdapter(new CommentListViewAdapter(getApplicationContext(), listCommentDatas, R.layout.activity_comment_list));

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

        recyclerView.getAdapter().notifyDataSetChanged();

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

        }

        @Override
        public int getItemCount() {
            return this.listCommentDatas.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView comment_profile;
            TextView comment_name;
            TextView comment_date;
            TextView comment_content;


            public ViewHolder(View view) {
                super(view);

                comment_profile = (ImageView) view.findViewById(R.id.comment_profile);
                comment_name = (TextView) view.findViewById(R.id.comment_name);
                comment_date = (TextView) view.findViewById(R.id.comment_date);
                comment_content = (TextView) view.findViewById(R.id.comment_content);

            }

        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
