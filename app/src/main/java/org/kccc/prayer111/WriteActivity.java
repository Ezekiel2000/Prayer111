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

    final int REQ_CODE_SELECT_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }

        Intent intent = getIntent();
//        String name = intent.getStringExtra("name");
//        Uri uri = Uri.parse(intent.getStringExtra("uri"));

        Typeface typeface = Typeface.createFromAsset(getAssets(), "NotoSansCJKkr_Regular.otf");

        btn_Ok = (TextView) findViewById(R.id.text_ok);
        btn_Cancel = (TextView) findViewById(R.id.text_cancel);
        title = (TextView) findViewById(R.id.text_title);
        write_content = (EditText)findViewById(R.id.write_content);
        write_select_image = (ImageView) findViewById(R.id.write_select_image);
        ic_camera = (ImageView) findViewById(R.id.ic_camera);

        btn_Ok.setTypeface(typeface);
        btn_Cancel.setTypeface(typeface);
        title.setTypeface(typeface);
        write_content.setTypeface(typeface);

//        setDisplay();

//        Log.d("하이",  "name : " + name.toString());
//        Log.d("하이",  "image : " + uri.toString());


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

    private void setDisplay() {

        write_name.setText("전형배");
        Glide.with(this)
                .load("https://graph.facebook.com/1197597296976302/picture")
                .bitmapTransform(new CropCircleTransformation(this))
                .into(write_profile);

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
