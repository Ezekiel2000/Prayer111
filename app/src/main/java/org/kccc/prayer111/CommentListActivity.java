package org.kccc.prayer111;

import android.content.Context;
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


        listCommentDatas = new ArrayList<>();
        data = new ListCommentData[ITEM_SIZE];
        data[0] = new ListCommentData(R.drawable.a, "PSY", "2014-12-15", "뭐 이래요?");
        data[1] = new ListCommentData(R.drawable.b, "전형배", "2015-10-25", "잘하시길..");

        for (int i = 0; i < ITEM_SIZE; i++) {
            listCommentDatas.add(data[i]);
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 버튼을 눌렀을 때 리플을 추가해 주는 구문

            }
        });

        recyclerView.setAdapter(new CommentListViewAdapter(getApplicationContext(), listCommentDatas, R.layout.activity_comment_list));

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
