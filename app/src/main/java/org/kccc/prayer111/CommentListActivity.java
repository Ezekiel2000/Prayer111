package org.kccc.prayer111;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
    private static String setUrl = "http://api.kccc.org/AppAjax/111prayer/index.php?mode=setComment";
    String prayNumber;
    String cmtNumber;
    String cmtContent;

    String email;

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
        email = intent.getStringExtra("email");

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

                Log.d("하이", "코텐트 : " + jsonArray);

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
                    cmtContent = content;

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

                    if (email == null) {

                        signInIntent.putExtra("position", "cmt");
                        startActivity(signInIntent);

                    } else {

                        try {

                            setUrl = setUrl + "&UserId=" + email + "&prayNo=" + prayNumber + "&comment=" + input_comment.getText().toString() + "'\n'";

                            Log.d("하이",  "setURL : " + setUrl);

                            URL url = new URL(setUrl);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");

                            OutputStream outputStream = conn.getOutputStream();
                            outputStream.write(setUrl.getBytes());
                            outputStream.flush();
                            outputStream.close();

                            recyclerView.invalidate();

                        } catch (Exception e) {

                        }

                    }

                    // 버튼을 눌렀을 때 리플을 추가해 주는 구문

                }
            });


            recyclerView.setAdapter(new CommentListViewAdapter(getApplicationContext(), listCommentDatas, R.layout.activity_comment_list));
            progressDialog.dismiss();
        }
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

//            Drawable drawable = ContextCompat.getDrawable(context, commentData.getComment_profileImage());
//            holder.comment_profile.setBackground(drawable);

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
//
//                Typeface typefaceLight = Typeface.createFromAsset(getAssets(), "NotoSansCJKkr_Light.otf");
//                Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "NotoSansCJKkr_Regular.otf");
//
//                comment_name.setTypeface(typefaceLight);
//                comment_date.setTypeface(typefaceLight);
//                comment_content.setTypeface(typefaceRegular);

            }

        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
