package org.kccc.prayer111;

import android.app.Activity;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

public class WriteActivity extends AppCompatActivity {

    TextView btn_Ok;
    TextView btn_Cancel;
    TextView title;
    TextView write_name;
    EditText write_content;
    ImageView write_profile;
    ImageView write_select_image;
    ImageView ic_camera;

    Uri mImageCaptureUri;
    String imgPath;

    private String userName;
    private String userId;
    private String profileUrl;
    private String email;
    private String password;
    private String input_image;

    final int REQ_CODE_SELECT_IMAGE = 100;
    final int REQ_CODE_CAPTRUE_IMAGE = 200;

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

//                new Thread() {
//                    @Override
//                    public void run() {
//
//                        HttpURLConnection conn = null;
//
//                        try {
//
//                            URL url = new URL(setUrl);
//                            conn = (HttpURLConnection) url.openConnection();
//                            conn.setDoOutput(true);
//                            conn.setDoInput(true);
//                            conn.setChunkedStreamingMode(0);
//                            conn.setRequestMethod("POST");
////                        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
//
//                            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
//                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//
//                            writer.write( "mode=setTogetherPray"
//                                    + "&pray=" + write_content.getText()
//                                    + "&id=" + email);
//                            writer.flush();
//                            writer.close();
//                            out.close();
//
//                            conn.connect();
//
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//
//                            StringBuilder builder = new StringBuilder();
//                            String line = null;
//                            while ((line =reader.readLine()) != null) {
//                                if (builder.length() > 0) {
//                                    builder.append("\n");
//                                }
//                                builder.append(line);
//                            }
//
//                            Log.d("하이", builder.toString());
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        } finally {
//                            if (conn != null) {
//                                conn.disconnect();
//                            }
//                        }
//
//                    }
//                }.start();




                // 서버로 정보값 Post 하고 MainActivity 의 중보기도로 이동

                Log.d("하이" ,"아이디 : " + userId);
                Log.d("하이" ,"아이디 : " + write_content.getText().toString());
                Log.d("하이" ,"아이디 : " + input_image);

                new WirteUpload().execute(userId, write_content.getText().toString(), input_image);


                Intent okIntent = new Intent(getBaseContext(), MainActivity.class);
                okIntent.putExtra("position", "write");
                okIntent.putExtra("userId", userId);
                okIntent.putExtra("name", userName);
                okIntent.putExtra("email", email);
                okIntent.putExtra("user_profile", profileUrl);
                okIntent.putExtra("password", password);
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


    }

    public class WirteUpload extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String res = null;

            try {

                String id = params[0];
                String content = params[1];
                String image = params[2];

                RequestBody requestBody;

                if (TextUtils.isEmpty(image)) {

                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("mode", "setTogetherPray")
                            .addFormDataPart("pray", content)
                            .addFormDataPart("id", id)
                            .build();

                } else {

                    File file = new File(image);

                    final MediaType MEDIA_TYPE = MediaType.parse("image/*");
                    String filename = image.substring(image.lastIndexOf("/") + 1);

                    Log.d("하이" ,"앵크드 : " + id);
                    Log.d("하이" ,"앵크드 : " + content);
                    Log.d("하이" ,"앵크드 : " + image);


                    RequestBody fileBody = RequestBody.create(MEDIA_TYPE, file);

                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("mode", "setTogetherPray")
                            .addFormDataPart("pray", content)
                            .addFormDataPart("id", id)
                            .addFormDataPart("up", filename, fileBody)
                            .build();

                }

                Request request = new Request.Builder()
                        .url(setUrl)
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                res = response.body().string();

                JSONObject json = new JSONObject(res);
                Log.d("하이", "json : " + json.toString());

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

        }
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

                write_select_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                write_select_image.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(imageUri)
                        .into(write_select_image);

            }


        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    public String getImageNameToUri(Uri data) {

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        imgPath = cursor.getString(column_index);

        input_image = imgPath;

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

        input_image = image.getPath();

        imgPath = "file:" + image.getAbsolutePath();
        return image;
    }


    public void doTakePhotoAction() throws IOException {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//        String url = "tep_" + String.valueOf(System.currentTimeMillis()) + ".jpg";

        mImageCaptureUri = FileProvider.getUriForFile(WriteActivity.this,
                "org.kccc.prayer111.provider",
                createImageFile());

        Log.d("하이", "mImageCaptureUri : " + mImageCaptureUri);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, REQ_CODE_CAPTRUE_IMAGE);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
