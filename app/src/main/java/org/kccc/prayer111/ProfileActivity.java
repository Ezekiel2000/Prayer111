package org.kccc.prayer111;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.HttpURLConnection;
import java.net.URL;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ProfileActivity extends AppCompatActivity {

    Context context;

    ImageView image_profile_chage;
    TextView text_my_name;
    TextView text_my_email;
    TextView text_my_password_change;
    TextView text_write_intercession;
    TextView text_write_comment;
    Button btn_signout;
    Button btn_logout;

    String imgPath;

    final int REQ_CODE_SELECT_IMAGE = 100;

    private static String setUrl = "http://api.kccc.org/AppAjax/111prayer/index.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }

        image_profile_chage = (ImageView) findViewById(R.id.image_profile_chage);
        text_my_name = (TextView) findViewById(R.id.my_name);
        text_my_email = (TextView) findViewById(R.id.my_email);
        text_my_password_change = (TextView) findViewById(R.id.my_password_change);
        text_write_intercession = (TextView) findViewById(R.id.text_write_intercession);
        text_write_comment = (TextView) findViewById(R.id.text_write_comment);
        btn_signout = (Button) findViewById(R.id.btn_signout);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        text_my_name.setText(PropertyManager.getInstance().getUserName());
        text_my_email.setText(PropertyManager.getInstance().getUserEmail());

        if (PropertyManager.getInstance().getUserProfile().isEmpty()) {

            Uri uri = Uri.parse(PropertyManager.getInstance().getUserProfile());

            Glide.with(this)
                    .load(uri)
                    .error(R.drawable.signup_profile)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(image_profile_chage);
        }

        image_profile_chage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);

            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "로그아웃", Toast.LENGTH_SHORT).show();
            }
        });

        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {
                    @Override
                    public void run() {
                        HttpURLConnection conn = null;

                        try {

                            setUrl = setUrl + "?mode=signoutProcess" + "&userId=" + PropertyManager.getInstance().getUserEmail();

                            URL url = new URL(setUrl);
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true);
                            conn.setRequestMethod("GET");
                            conn.getInputStream();

                            Log.d("하이", "setUrl" + setUrl);

                        } catch (Exception e) {
                            Log.d("하이", "setUrl" + setUrl);
                            e.printStackTrace();
                        }
                    }
                }.start();

                PropertyManager.getInstance().setUserProfile("");
                PropertyManager.getInstance().setUserName("");
                PropertyManager.getInstance().setUserEmail("");
                PropertyManager.getInstance().setPassword("");

                Toast.makeText(v.getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_CODE_SELECT_IMAGE) {

            if (resultCode == Activity.RESULT_OK) {

                try {

                    String nameImage = getImageNameToUri(data.getData());

                    imgPath = data.getData().toString();


                    Log.d("하이", "data.getData :" + data.getData());

                    Log.d("하이", "파일 이름 : " + nameImage);

                    image_profile_chage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    Glide.with(this)
                            .load(data.getData())
                            .error(R.drawable.ic_myinfo)
                            .bitmapTransform(new CropCircleTransformation(this))
                            .into(image_profile_chage);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
        PropertyManager.getInstance().setUserProfile(imgPath);
        Log.d("하이", "이미지 : " + PropertyManager.getInstance().getUserProfile());
        super.onActivityResult(requestCode, resultCode, data);
    }


    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        imgPath = cursor.getString(column_index);

        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        return imgPath;
    }



}
