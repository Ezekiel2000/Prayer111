package org.kccc.prayer111;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by ezekiel on 2017. 2. 2..
 */

public class ListDataAdapter extends RecyclerView.Adapter<ListDataAdapter.ViewHolder> {

    final static int MIN_LINE = 2;
    final static int MAX_LINE = 500;

    Context context;
    List<ListData> listData;
    int list_intercession;
    Boolean icon_heart_clicked = false;

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

        // glide 라이브러리 사용하여 원형 프로필 만들기
        Glide.with(context)
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

        holder.text_prayer_number.setText(String.valueOf(data.getPrayerNumber()));
        holder.text_comment_number.setText(String.valueOf(data.getCommentNumber()));
        holder.icon_heart.setOnClickListener(v -> {
            if (icon_heart_clicked == false) {
                holder.icon_heart.setImageResource(R.drawable.ic_heart_red);
                holder.text_prayer_number.setText(String.valueOf(data.getPrayerNumber() + 1));
                Log.d("하이", "선택한 놈 : "+ holder.getAdapterPosition());
                icon_heart_clicked = true;
            } else {
                holder.icon_heart.setImageResource(R.drawable.ic_heart);
                holder.text_prayer_number.setText(String.valueOf(data.getPrayerNumber()));
                icon_heart_clicked = false;
            }
        });

        // 코멘트 버튼을 누르면 CommentListActivity로 이동함
        holder.icon_comment.setOnClickListener(v -> {
            Intent commentIntent = new Intent(context, CommentListActivity.class);
            v.getContext().startActivity(commentIntent);
        });

        // 공유 버튼을 클릭해을 경우 dialog창을 뜨게 함
        holder.icon_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("공유하기");
                builder.setItems(info, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Toast.makeText(context, "페이스북 공유", Toast.LENGTH_SHORT).show();

//                            ShareLinkContent content = new ShareLinkContent.Builder()
//                            .setContentTitle("오늘의 기도")
//                            .setContentDescription(
//                                    "테스트중입니다")
//                            .setContentUrl(Uri.parse("https://www.facebook.com/111Pray/"))
//                            .setShareHashtag(new ShareHashtag.Builder()
//                                    .setHashtag("#111기도")
//                                    .build())
//                            .build();


                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.putExtra("sms_body", holder.text_content.getText());
                            smsIntent.setType("vnd.android-dir/mms-sms");

                            try {
                                v.getContext().startActivity(smsIntent);
                            } catch (Exception e) {
                                Toast.makeText(context, "카카오톡이 설치가 안되어있습니다.", Toast.LENGTH_SHORT).show();
                            }



                            break;
                        case 1:

                            Toast.makeText(context, "카카오톡 공유", Toast.LENGTH_SHORT).show();
                            Log.d("하이", "선택한 놈 : "+ holder.getAdapterPosition());
                            Intent kakaoIntent = new Intent(Intent.ACTION_SEND);
                            kakaoIntent.setType("text/plain");
                            kakaoIntent.putExtra(Intent.EXTRA_SUBJECT, "[" + holder.text_name.getText() + " 의 중보 기도]\n");

                            kakaoIntent.putExtra(Intent.EXTRA_TEXT, holder.text_content.getText());
                            kakaoIntent.setPackage("com.kakao.talk");

                            try {
                                v.getContext().startActivity(kakaoIntent);
                            } catch (Exception e) {
                                Toast.makeText(context, "카카오톡이 설치가 안되어있습니다.", Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case 2:
                            Toast.makeText(context, "신고하기", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    dialog.dismiss();
                });

                builder.show();
            }

        });

//        holder.card_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, data.getName(), Toast.LENGTH_SHORT).show();
//
//            }
//        });



    }

    @Override
    public int getItemCount() {
        return this.listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView text_name;
        TextView text_content;
        TextView text_prayer_number;
        TextView text_comment_number;
        TextView text_more;
        ImageView icon_heart;
        ImageView icon_comment;
        ImageView icon_share;
        CardView card_list;

        public ViewHolder(View view) {
            super(view);

            profile = (ImageView) view.findViewById(R.id.image_profile);
            text_name = (TextView) view.findViewById(R.id.text_name);
            text_content = (TextView) view.findViewById(R.id.text_content);
            text_more = (TextView) view.findViewById(R.id.text_more);
            text_prayer_number = (TextView) view.findViewById(R.id.text_prayer_number);
            text_comment_number = (TextView) view.findViewById(R.id.text_comment_number);
            icon_heart = (ImageView) view.findViewById(R.id.icon_heart);
            icon_comment = (ImageView) view.findViewById(R.id.icon_speech);
            icon_share = (ImageView) view.findViewById(R.id.icon_share);
            card_list = (CardView) view.findViewById(R.id.card_list);
        }
    }
}
