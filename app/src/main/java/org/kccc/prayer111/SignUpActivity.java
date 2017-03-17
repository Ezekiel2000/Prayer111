package org.kccc.prayer111;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class SignUpActivity extends AppCompatActivity {

    Context context;
    EditText text_sign_name;
    EditText text_sign_email;
    EditText text_sign_password;
    EditText text_sign_password_conform;
    ImageView image_select;
    Button btn_sign;

    String imgPath;
    String realPath;

    final int REQ_CODE_SELECT_IMAGE = 100;

    private static String setUrl = "http://api.kccc.org/AppAjax/111prayer/index.php";

    String attachmentName = "bitmap";
    String attachmentFileName = "bitmap.bmp";
    String crlf = "\r\n";
    String twoHyphens = "--";
    String boundary =  "*****";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }


        text_sign_name = (EditText) findViewById(R.id.text_sign_name);
        text_sign_email = (EditText) findViewById(R.id.text_sign_email);
        text_sign_password = (EditText) findViewById(R.id.text_sign_password);
        text_sign_password_conform = (EditText) findViewById(R.id.text_sign_password_conform);
        btn_sign = (Button) findViewById(R.id.btn_sign);
        image_select = (ImageView) findViewById(R.id.image_select);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "NotoSansCJKkr_Light.otf");

        text_sign_name.setTypeface(typeface);
        text_sign_email.setTypeface(typeface);
        text_sign_password.setTypeface(typeface);
        text_sign_password_conform.setTypeface(typeface);


        image_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (text_sign_name.getText().length() == 0) {
                    Toast.makeText(v.getContext(), "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                } else if (text_sign_email.getText().length() == 0) {
                    Toast.makeText(v.getContext(), "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                } else if (text_sign_password.getText().length() == 0) {
                    Toast.makeText(v.getContext(), "패스워드를 입력하세요", Toast.LENGTH_SHORT).show();
                } else if (text_sign_password_conform.getText().length() == 0) {
                    Toast.makeText(v.getContext(), "패스워드 확인을 입력하세요", Toast.LENGTH_SHORT).show();
                } else if (text_sign_password.getText().toString().equals(text_sign_password_conform.getText().toString())) {

                    Toast.makeText(v.getContext(), "성공", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getBaseContext(), WriteActivity.class);

                    intent.putExtra("name", text_sign_name.getText().toString());
                    intent.putExtra("userId", text_sign_email.getText().toString());
                    intent.putExtra("user_profile", imgPath);

                    PropertyManager.getInstance().setUserName(text_sign_name.getText().toString());
                    PropertyManager.getInstance().setUserEmail(text_sign_email.getText().toString());
                    PropertyManager.getInstance().setPassword(text_sign_password.getText().toString());
                    PropertyManager.getInstance().setUserLoginType("EMAIL");
                    PropertyManager.getInstance().setUserProfile(imgPath);

                    Log.d("하이",  "setURL : " + setUrl);


//                    new Thread() {
//                        @Override
//                        public void run() {
//
//
//                            HttpURLConnection conn = null;
//
//                            try {
//
//                                URL url = new URL(setUrl);
//                                conn = (HttpURLConnection) url.openConnection();
//                                conn.setDoOutput(true);
//                                conn.setDoInput(true);
//                                conn.setChunkedStreamingMode(0);
//                                conn.setRequestMethod("POST");
//
//
//
//                                OutputStream out = new BufferedOutputStream(conn.getOutputStream());
//                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//
//                                writer.write( "mode=joinProcess"
//                                        + "&name=" + text_sign_name.getText()
//                                        + "&email=" + text_sign_email.getText()
//                                        + "&password=" + text_sign_password.getText()
//                                        + "&method=EMAIL");
//                                writer.flush();
//                                writer.close();
//                                out.close();
//
//                                conn.connect();
//
//                                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//
//                                StringBuilder builder = new StringBuilder();
//                                String line = null;
//                                while ((line =reader.readLine()) != null) {
//                                    if (builder.length() > 0) {
//                                        builder.append("\n");
//                                    }
//                                    builder.append(line);
//                                }
//
//                                Log.d("하이", builder.toString());
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            } finally {
//                                if (conn != null) {
//                                    conn.disconnect();
//                                }
//                            }
//
//                        }
//                    }.start();
                    new MultiPartUpload().execute(
                            PropertyManager.getInstance().getUserName(), PropertyManager.getInstance().getUserEmail(),
                            PropertyManager.getInstance().getPassword(), PropertyManager.getInstance().getUserLoginType(), realPath);



                    startActivity(intent);
                    finish();

                } else {

                    Log.d("하이", text_sign_password_conform.getText().toString());

                    Toast.makeText(v.getContext(), "패스워드가 틀립니다.", Toast.LENGTH_SHORT).show();

                }
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
                    Log.d("하이", "imgPath : " + this.imgPath);
                    Log.d("하이", "파일 이름 : " + nameImage);

                    image_select.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    Glide.with(this)
                            .load(data.getData())
                            .error(R.drawable.signup_profile)
                            .bitmapTransform(new CropCircleTransformation(this))
                            .into(image_select);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        imgPath = cursor.getString(column_index);

        Log.d("하이", "imgPath : " + this.imgPath);
        realPath = this.imgPath;

        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        Log.d("하이", "imgName : " + imgName);

        return imgPath;
    }


    //    public void doTakePhotoAction() {
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        String url = "tep_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//        startActivity(intent, PICK_FROM_CAMERA);
//
//    }


}
