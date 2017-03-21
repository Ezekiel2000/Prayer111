package org.kccc.prayer111;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class WriteActivity extends AppCompatActivity {

    TextView btn_Ok;
    TextView btn_Cancel;
    TextView title;
    TextView write_name;
    EditText write_content;
    ImageView write_profile;
    ImageView write_select_image;
    ImageView ic_camera;

    private String userName;
    private String userId;
    private String profileUrl;
    private String email;
    private String password;

    final int REQ_CODE_SELECT_IMAGE = 100;

    private static String setUrl = "http://api.kccc.org/AppAjax/111prayer/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("name");
        profileUrl = intent.getStringExtra("user_profile");
        email = intent.getStringExtra("email");

        email = PropertyManager.getInstance().getUserEmail();
        userName = PropertyManager.getInstance().getUserName();


        Typeface typeface = Typeface.createFromAsset(getAssets(), "NotoSansCJKkr_Regular.otf");

        btn_Ok = (TextView) findViewById(R.id.text_ok);
        btn_Cancel = (TextView) findViewById(R.id.text_cancel);
        title = (TextView) findViewById(R.id.text_title);
        write_content = (EditText)findViewById(R.id.write_content);
        write_name = (TextView) findViewById(R.id.write_name);
        write_select_image = (ImageView) findViewById(R.id.write_select_image);
        write_profile = (ImageView) findViewById(R.id.write_profile);
        ic_camera = (ImageView) findViewById(R.id.ic_camera);

        btn_Ok.setTypeface(typeface);
        btn_Cancel.setTypeface(typeface);
        title.setTypeface(typeface);
        write_content.setTypeface(typeface);

        Log.d("하이",  "setDisplay 전 ");

        setDisplay();



//        Log.d("하이",  "name : " + name.toString());
//        Log.d("하이",  "image : " + uri.toString());

        btn_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {
                    @Override
                    public void run() {

                        HttpURLConnection conn = null;

                        try {

                            URL url = new URL(setUrl);
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setDoOutput(true);
                            conn.setDoInput(true);
                            conn.setChunkedStreamingMode(0);
                            conn.setRequestMethod("POST");
//                        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                            writer.write( "mode=setTogetherPray"
                                    + "&pray=" + write_content.getText()
                                    + "&id=" + email);
                            writer.flush();
                            writer.close();
                            out.close();

                            conn.connect();

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

                    }
                }.start();




                // 서버로 정보값 Post 하고 MainActivity 의 중보기도로 이동
                Intent okIntent = new Intent(getBaseContext(), MainActivity.class);
                okIntent.putExtra("position", "write");
                startActivity(okIntent);
                finish();

            }
        });


        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ic_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });


    }

    public void setDisplay() {

        write_name.setText(PropertyManager.getInstance().getUserName());

        Log.d("하이",  "setText 후 :" + profileUrl + userName + email + password);

            Glide.with(this)
                    .load(profileUrl)
                    .override(200, 200)
                    .error(R.drawable.write_pro_default)
                    .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                    .into(write_profile);

        Log.d("하이",  "glide 후 ");

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        Toast.makeText(getBaseContext(), "resultCode :" + requestCode, Toast.LENGTH_SHORT).show();

        if (requestCode == REQ_CODE_SELECT_IMAGE) {

            if (resultCode == Activity.RESULT_OK) {

                try {

                    String nameImage = getImageNameToUri(data.getData());

                    Log.d("하이", "파일 이름 : " + nameImage);

                    write_select_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    write_select_image.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(data.getData())
                            .into(write_select_image);

                    Toast.makeText(getBaseContext(), "resultCode :" + requestCode, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public String getImageNameToUri(Uri data) {

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        return imgPath;
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
