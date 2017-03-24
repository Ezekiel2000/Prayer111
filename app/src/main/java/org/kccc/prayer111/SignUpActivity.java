package org.kccc.prayer111;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    String ty;
    Uri mImageCaptureUri;

    final int REQ_CODE_SELECT_IMAGE = 100;
    final int REQ_CODE_CAPTRUE_IMAGE = 200;

    private static String setUrl = "http://api.kccc.org/AppAjax/111prayer/index.php";

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

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setTitle("사진 선택")
                        .setNegativeButton("사진첩", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);

                            }
                        })
                        .setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {

                                    doTakePhotoAction();

                                } catch (IOException e) {

                                    e.printStackTrace();
                                }

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

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

                    PropertyManager.getInstance().setUserName(text_sign_name.getText().toString());
                    PropertyManager.getInstance().setUserEmail(text_sign_email.getText().toString());
                    PropertyManager.getInstance().setPassword(text_sign_password.getText().toString());
                    PropertyManager.getInstance().setUserLoginType("EMAIL");
                    PropertyManager.getInstance().setUserProfile(imgPath);

                    Log.d("하이", "setURL : " + setUrl);

                    new MultiPartUpload().execute(
                            PropertyManager.getInstance().getUserName(), PropertyManager.getInstance().getUserEmail(),
                            PropertyManager.getInstance().getPassword(), PropertyManager.getInstance().getUserLoginType(), realPath
                    );


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
        } else if (requestCode == REQ_CODE_CAPTRUE_IMAGE) {

            if (resultCode == Activity.RESULT_OK) {

                Uri imageUri = Uri.parse(imgPath);
                File file = new File(imageUri.getPath());

                Log.d("하이", "imageUri : " + imageUri);
                Log.d("하이", "file : " + file.getPath());

                image_select.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(this)
                        .load(imageUri)
                        .error(R.drawable.signup_profile)
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(image_select);

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

        Log.d("하이", "realPath : " + this.imgPath);
        realPath = this.imgPath;

        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        Log.d("하이", "imgName : " + imgName);

        return imgPath;
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        Log.d("하이", "image : " + image.getPath());

        realPath = image.getPath();

        imgPath = "file:" + image.getAbsolutePath();
        return image;
    }


    public void doTakePhotoAction() throws IOException {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//        String url = "tep_" + String.valueOf(System.currentTimeMillis()) + ".jpg";

            mImageCaptureUri = FileProvider.getUriForFile(SignUpActivity.this,
                    "org.kccc.prayer111.provider",
                    createImageFile());

            Log.d("하이", "mImageCaptureUri : " + mImageCaptureUri);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            startActivityForResult(intent, REQ_CODE_CAPTRUE_IMAGE);
    }

    public class MultiPartUpload extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String res = null;

            try {
                String name = params[0];
                String email = params[1];
                String password = params[2];
                String method = params[3];
                String image = params[4];



                Log.d("하이", "image : " + image);

                File file = new File(image);

                Log.d("하이", "file : " + file.toString());

                final MediaType MEDIA_TYPE = MediaType.parse("image/*");
                final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
                final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
                String filename = image.substring(image.lastIndexOf("/") + 1);

                Log.d("하이", "filename : " + filename);

                RequestBody requestBody;
                RequestBody fileBody = RequestBody.create(MEDIA_TYPE, file);

                if (image.endsWith("png")) {
                    Log.d("하이", "png임");
                    fileBody = RequestBody.create(MEDIA_TYPE_PNG, file);
                } else if (image.endsWith("jpg")) {
                    Log.d("하이", "jpg임");
                    fileBody = RequestBody.create(MEDIA_TYPE_JPG, file);
                }

                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("mode", "joinProcess")
                        .addFormDataPart("name", name)
                        .addFormDataPart("email", email)
                        .addFormDataPart("password", password)
                        .addFormDataPart("method", method)
                        .addFormDataPart("up", filename, fileBody)
                        .build();


                Log.d("하이", "바디 : " + requestBody.toString());

                Request request = new Request.Builder()
                        .url(setUrl)
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                res = response.body().string();
                Log.d("하이", "response : " + res);

            } catch (UnknownHostException | UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject object = jsonObject.getJSONObject("result");
                ty = object.getString("ty");

                Log.d("하이", "ty : " + ty);

                if (ty.equals("new")) {

                    Intent intent = new Intent();

                    if (getIntent().getStringExtra("position").equals("cmt")) {

                        intent = new Intent(getBaseContext(), CommentListActivity.class);

                        intent.putExtra("name", text_sign_name.getText().toString());
                        intent.putExtra("userId", text_sign_email.getText().toString());
                        intent.putExtra("email", text_sign_email.getText().toString());
                        intent.putExtra("password", text_sign_password.getText().toString());
                        intent.putExtra("user_profile", imgPath);

                    } else if (getIntent().getStringExtra("position").equals("write")) {

                        intent = new Intent(getBaseContext(), WriteActivity.class);

                        intent.putExtra("name", text_sign_name.getText().toString());
                        intent.putExtra("userId", text_sign_email.getText().toString());
                        intent.putExtra("email", text_sign_email.getText().toString());
                        intent.putExtra("password", text_sign_password.getText().toString());
                        intent.putExtra("user_profile", imgPath);

                    } else if (getIntent().getStringExtra("position").equals("main")) {

                        intent = new Intent(getBaseContext(), MainActivity.class);

                        intent.putExtra("name", text_sign_name.getText().toString());
                        intent.putExtra("userId", text_sign_email.getText().toString());
                        intent.putExtra("email", text_sign_email.getText().toString());
                        intent.putExtra("password", text_sign_password.getText().toString());
                        intent.putExtra("user_profile", imgPath);

                    }

                    startActivity(intent);
                    finish();

                } else if (ty.equals("old")) {
                    Toast.makeText(getBaseContext(), "이미 있는 계정입니다.", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
