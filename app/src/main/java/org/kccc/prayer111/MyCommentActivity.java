package org.kccc.prayer111;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

        // 해당 Activity 의 상태창의 색을 지정하기 대신 SDK 21 이상에서만 지원
       if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }

        // 해당 Activity 화면에 recyclerView 설정하기
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.comment_list_my_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // listData 객체 생성
        listCommentDatas = new ArrayList<>();

        // AsyncTask 객체 execute 하기
        new GetMyCommentList().execute();

        // Adapter 객체를 생성하여 xml 파일 불러와 해당 listData 내용 넣기
        myCommentListViewAdapter = new MyCommentListViewAdapter(getApplicationContext(), listCommentDatas, R.layout.activity_my_comment);
        // 생성된 Adapter 객체를 recyclerView 에 Set 하기
        recyclerView.setAdapter(myCommentListViewAdapter);

    }

    // 내가 쓴 댓글만 불러오기 위한 AsyncTask 매소드
    private class GetMyCommentList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            // HttpHandler 객체 생성
            HttpHandler sh = new HttpHandler();

            // 기존의 url 에다가 usrId 를 추가하여 API 사용하기 위한 주소 생성
            getUrl = getUrl + "&userId=" + PropertyManager.getInstance().getUserId();
            // API 를 통해 String 리턴
            String jsonStr = sh.makeServiceCall(getUrl);

            try {

                // 리턴된 String 값을 JsonObject 값으로 변경
                JSONObject jsonObject = new JSONObject(jsonStr);
                // JsonObject 값 가운데 "result" 키에 해당하는 Value 값을 String 으로 반환
                String dataJson = jsonObject.getString("result");

                // 해당 String 값을 JsonArray 값으로 변경
                JSONArray jsonArray = new JSONArray(dataJson);

                for (int i = 0; i < jsonArray.length(); i++) {

                    // array 에서 각각의 JsonObject 값을 불러와 object 에 삽입
                    JSONObject object = jsonArray.getJSONObject(i);

                    // JsonObject 에서 각각의 해당하는 Key 값의 Value 값을 각각의 String 에다 삽입
                    String prayNumber = object.getString("keyno");
                    String name = PropertyManager.getInstance().getUserName();
                    String content = object.getString("memo");
                    String profile =  getIntent().getStringExtra("user_profile");
                    String date = object.getString("indate");

                    // data Array 객체를 JsonArray 크기로 생성
                    data = new ListCommentData[jsonArray.length()];

                    // listData 객체를 생성하여 미리 생성되어 있는 data list 에 순차적으로 삽입
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

    // 해당 Activity 안에 inner 클래스로 Adapter 클래스 생성
    private class MyCommentListViewAdapter extends RecyclerView.Adapter<MyCommentListViewAdapter.ViewHolder> {

        private Context context;
        private List<ListCommentData> listCommentDatas;
        int list_comment;

        private MyCommentListViewAdapter(Context context, List<ListCommentData> listCommentDatas, int list_comment) {
            this.context = context;
            this.listCommentDatas = listCommentDatas;
            this.list_comment = list_comment;
        }

        @Override
        public MyCommentListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, null);

            return new MyCommentListViewAdapter.ViewHolder(view);
        }

        // 각 list 화면에 해당 View 들을 setting
        @Override
        public void onBindViewHolder(MyCommentListViewAdapter.ViewHolder holder, int position) {

            ListCommentData commentData = listCommentDatas.get(position);

            Glide.with(context)
                    .load(getIntent().getStringExtra("user_profile"))
                    .override(200, 200)
                    .error(R.drawable.ic_profile_default)
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


        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView comment_profile;
            TextView comment_name;
            TextView comment_date;
            TextView comment_content;


            private ViewHolder(View view) {
                super(view);

                // 각 변수에 각 view 들을 setting
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
