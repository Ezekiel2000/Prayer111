package org.kccc.prayer111;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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

public class ProfileActivity extends AppCompatActivity {

    Context context;

    ImageView image_profile_chage;
    TextView text_my_name;
    TextView text_my_password_change;
    TextView text_write_intercession;
    TextView text_write_comment;
    EditText input_password_change;
    EditText input_password_change_conform;
    Button btn_signout;
    Button btn_logout;

    String imgPath;
    String realPath;

    String userId;
    String after_password;
    String after_password_conform;

    String ty;
    Uri mImageCaptureUri;

    final int REQ_CODE_SELECT_IMAGE = 100;
    final int REQ_CODE_CAPTRUE_IMAGE = 200;

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
        text_my_password_change = (TextView) findViewById(R.id.my_password_change);
        text_write_intercession = (TextView) findViewById(R.id.text_write_intercession);
        text_write_comment = (TextView) findViewById(R.id.text_write_comment);
        btn_signout = (Button) findViewById(R.id.btn_signout);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        text_my_name.setText(PropertyManager.getInstance().getUserName());

        if (!PropertyManager.getInstance().getUserLoginType().equals("EMAIL")) {
            text_my_password_change.setVisibility(View.GONE);
        }

        userId = getIntent().getStringExtra("userId");

        Log.d("하이", "이미지 나옴 : " + getIntent().getStringExtra("user_profile"));
        Glide.with(this)
                .load(getIntent().getStringExtra("user_profile"))
                .override(200, 200)
                .error(R.drawable.signup_profile)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(image_profile_chage);

        image_profile_chage.setOnClickListener(new View.OnClickListener() {
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

        text_my_password_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAlertDialog();

            }
        });

        text_write_intercession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntercessionIntent = new Intent(ProfileActivity.this, MyIntercessionActivity.class);
                myIntercessionIntent.putExtra("my", "intercession");
                startActivity(myIntercessionIntent);
            }
        });

        text_write_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myCommentIntent = new Intent(ProfileActivity.this, MyCommentActivity.class);
                myCommentIntent.putExtra("user_profile", getIntent().getStringExtra("user_profile"));
                myCommentIntent.putExtra("my", "comment");
                startActivity(myCommentIntent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PropertyManager.getInstance().getUserLoginType().equals("kakao")) {

                    // 카카오톡 강제 로그아웃
                    UserManagement.requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            Log.d("하이", "로그아웃 성공");
                        }
                    });


                } else if (PropertyManager.getInstance().getUserLoginType().equals("facebook")) {

                    // 페이스북 로그아웃
                    LoginManager.getInstance().logOut();


                } else if (PropertyManager.getInstance().getUserLoginType().equals("EMAIL")) {



                }

                // PropertyManager 초기화
//                PropertyManager.getInstance().setUserProfile("");
//                PropertyManager.getInstance().setUserName("");
//                PropertyManager.getInstance().setUserId("");
//                PropertyManager.getInstance().setPassword("");
//                PropertyManager.getInstance().setUserLoginType("");

                Toast.makeText(v.getContext(), "로그아웃", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra("check", false);
                startActivity(intent);
                finish();

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

                            setUrl = setUrl + "?mode=signoutProcess" + "&userId=" + PropertyManager.getInstance().getUserId();

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
                PropertyManager.getInstance().setUserId("");
                PropertyManager.getInstance().setPassword("");

                PropertyManager.getInstance().remove();

                Toast.makeText(v.getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                finish();

            }
        });


    }


    private void showAlertDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout pwdChangeLayout = (LinearLayout) inflater.inflate(R.layout.dialog_password_change, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(pwdChangeLayout);
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext() ,"확인", Toast.LENGTH_SHORT).show();
                input_password_change = (EditText) pwdChangeLayout.findViewById(R.id.input_password_change);
                input_password_change_conform = (EditText) pwdChangeLayout.findViewById(R.id.input_password_change_conform);

                after_password = input_password_change.getText().toString();
                after_password_conform = input_password_change_conform.getText().toString();

                Log.d("하이", "패스워드 : " + after_password);

                if ( !after_password.isEmpty() && !after_password_conform.isEmpty()) {

                    if ( after_password.equals(after_password_conform )) {

                        new Thread() {
                            @Override
                            public void run() {

                                HttpURLConnection conn = null;

                                try {
                                    URL url = new URL(setUrl);
                                    conn = (HttpURLConnection) url.openConnection();
                                    conn.setDoInput(true);
                                    conn.setDoOutput(true);
                                    conn.setChunkedStreamingMode(0);
                                    conn.setRequestMethod("POST");

                                    OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                                    writer.write( "mode=changePassword"
                                            + "&id=" + userId
                                            + "&password=" + after_password);
                                    writer.flush();
                                    writer.close();
                                    out.close();

                                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                                    StringBuilder builder = new StringBuilder();
                                    String line = null;
                                    while ((line = reader.readLine()) != null) {
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

                        Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다." , Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();

                    }

                } else {

                    Toast.makeText(getApplicationContext(), "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();

                }

            }
        });
        builder.show();

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
        } else if (requestCode == REQ_CODE_CAPTRUE_IMAGE) {

            if (resultCode == Activity.RESULT_OK) {

                Uri imageUri = Uri.parse(imgPath);
                File file = new File(imageUri.getPath());

                Log.d("하이", "imageUri : " + imageUri);
                Log.d("하이", "file : " + file.getPath());

                image_profile_chage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(this)
                        .load(imageUri)
                        .error(R.drawable.ic_myinfo)
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(image_profile_chage);

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

        mImageCaptureUri = FileProvider.getUriForFile(ProfileActivity.this,
                "org.kccc.prayer111.provider",
                createImageFile());

        Log.d("하이", "mImageCaptureUri : " + mImageCaptureUri);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, REQ_CODE_CAPTRUE_IMAGE);
    }

    @Override
    public void onBackPressed() {

        Intent reIntent = new Intent();
        PropertyManager.getInstance().setUserProfile(imgPath);
        setResult(Activity.RESULT_OK, reIntent);
        finish();

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


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent imageChangeIntent = new Intent(ProfileActivity.this, MainActivity.class);
            imageChangeIntent.putExtra("user_profile", imgPath);
            startActivity(imageChangeIntent);

        }
    }



}
