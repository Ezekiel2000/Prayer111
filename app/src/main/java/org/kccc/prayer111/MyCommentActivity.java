package org.kccc.prayer111;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MyCommentActivity extends AppCompatActivity {

    List<ListCommentData> listCommentDatas;
    ListCommentData[] data;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private static String getUrl = "http://api.kccc.org/AppAjax/111prayer/index.php?mode=getMyComment";

    MyCommentListViewAdapter myCommentListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }
        Log.d("하이", "시스템바 변경");

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.comment_list_my_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        listCommentDatas = new ArrayList<>();

        new GetMyCommentList().execute();

        myCommentListViewAdapter = new MyCommentListViewAdapter(getApplicationContext(), listCommentDatas, R.layout.activity_my_comment);
        recyclerView.setAdapter(myCommentListViewAdapter);





    }

    private class GetMyCommentList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpHandler sh = new HttpHandler();

            getUrl = getUrl + "&userId=" + PropertyManager.getInstance().getUserEmail();
            String jsonStr = sh.makeServiceCall(getUrl);

            Log.d("하이", "getURL : " + getUrl);

            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                String dataJson = jsonObject.getString("result");

                JSONArray jsonArray = new JSONArray(dataJson);

                Log.d("하이", "jsonarray : " + jsonArray);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);

                    String commentNumber = object.getString("no");
                    String prayNumber = object.getString("keyno");
                    String id = object.getString("id");
                    String name = PropertyManager.getInstance().getUserName();
                    String content = object.getString("memo");
                    String profile = PropertyManager.getInstance().getUserProfile();
                    String date = object.getString("indate");

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

            myCommentListViewAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(myCommentListViewAdapter);

        }
    }

    private class MyCommentListViewAdapter extends RecyclerView.Adapter<MyCommentListViewAdapter.ViewHolder> {

        private Context context;
        private List<ListCommentData> listCommentDatas;
        int list_comment;

        public MyCommentListViewAdapter(Context context, List<ListCommentData> listCommentDatas, int list_comment) {
            this.context = context;
            this.listCommentDatas = listCommentDatas;
            this.list_comment = list_comment;
        }


        @Override
        public MyCommentListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, null);

            return new MyCommentListViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyCommentListViewAdapter.ViewHolder holder, int position) {

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
