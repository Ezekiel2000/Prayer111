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

        // API21 이상 버전에서만 해당 화면의 상태창 색깔을 변경하기
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }

        // SharedPreference 에 저장된 유저 정보들을 불러와 전역변수로 저장하기
        userId = PropertyManager.getInstance().getUserId();
        userName = PropertyManager.getInstance().getUserName();
        profileUrl = PropertyManager.getInstance().getUserProfile();

        // 폰트 적용을 위한 타입 선언
        Typeface typeface = Typeface.createFromAsset(getAssets(), "NotoSansCJKkr_Regular.otf");

        // 각 뷰를 사용할 수 있도록 ResourceId를 통해 각 뷰들을 제어할 변수들을 선언
        btn_Ok = (TextView) findViewById(R.id.text_ok);
        btn_Cancel = (TextView) findViewById(R.id.text_cancel);
        title = (TextView) findViewById(R.id.text_title);
        write_content = (EditText)findViewById(R.id.write_content);
        write_name = (TextView) findViewById(R.id.write_name);
        write_select_image = (ImageView) findViewById(R.id.write_select_image);
        write_profile = (ImageView) findViewById(R.id.write_profile);
        ic_camera = (ImageView) findViewById(R.id.ic_camera);

        // 해당 뷰에 폰트 적용한 타입을 세팅
        btn_Ok.setTypeface(typeface);
        btn_Cancel.setTypeface(typeface);
        title.setTypeface(typeface);
        write_content.setTypeface(typeface);

        setDisplay();

        // 중보기도 작성 화면에서 Ok 버튼을 클릭시 실행
        btn_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 작성한 글을 업로드 하기 위한 AsyncTask 를 실행
                new WriteUpload().execute(userId, write_content.getText().toString(), input_image);


                // 글을 작성을 한 후에 Intent 를 통해 해당 정보들을 MainActivity 로 다시 넘김
                Intent okIntent = new Intent(getBaseContext(), MainActivity.class);
                okIntent.putExtra("position", "write");
                okIntent.putExtra("userId", userId);
                okIntent.putExtra("name", userName);
                okIntent.putExtra("email", userId);
                okIntent.putExtra("user_profile", profileUrl);
                okIntent.putExtra("password", password);
                startActivity(okIntent);
                finish();

            }
        });

        // 중보기도 작성 화면에서 Cancel 버튼 클릭시 실행
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 중보기도 작성 화면에서 카메라 이미지 클릭시 실행
        ic_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 다이얼로그 창을 띄우기 위해 builder 객체를 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                // builder 객체에 대한 타이틀 및 두가지 버튼에 대한 세팅
                builder.setTitle("사진 선택")
                        .setNegativeButton("사진첩", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // intent 를 생성하여 사진첩으로 가서 사진을 선택
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                // Result 값을 받아와서 그 값으로 사진을 불러올 때 사진을 직접 선택하는 것인지 촬영을 한 사진을 선택한 것인지 구분
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

                // 다이얼로그 객체 생성하여 builder 객체
               AlertDialog dialog = builder.create();
                // 다이얼로그 보여주기
                dialog.show();

            }
        });


    }

    // AsyncTask 를 사용하여 백그라운드에서 작성한 중보기도를 API를 통해 서버에 등록함
    // 세가지 파라미터중
    // 첫번째 파라미터는 doInBackground 파라미터 타입이며, execute 메소드의 인자 값
    // 두번째 파라미터는 doInBackground 작업시 진행 단위의 타입 onProgressUpdate 파라미터 값
    // 세번째 파라미터는 doInBackground 리턴값으로 onPostExecute 파라미터 값
    public class WriteUpload extends AsyncTask<String, Void, String> {


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

                // RequestBody 객체 생성
                RequestBody requestBody;

                // image 변수에 값이 없을 경우와 있을 경우 구분하여 객체에 정보들을 담음
                if (TextUtils.isEmpty(image)) {

                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("mode", "setTogetherPray")
                            .addFormDataPart("pray", content)
                            .addFormDataPart("id", id)
                            .build();

                } else {

                    // image 변수에 값이 있을 경우 파일 객체로 생성
                    File file = new File(image);

                    final MediaType MEDIA_TYPE = MediaType.parse("image/*");
                    String filename = image.substring(image.lastIndexOf("/") + 1);

                    RequestBody fileBody = RequestBody.create(MEDIA_TYPE, file);

                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("mode", "setTogetherPray")
                            .addFormDataPart("pray", content)
                            .addFormDataPart("id", id)
                            .addFormDataPart("up", filename, fileBody)
                            .build();

                }

                // Request 객체를 POST 방식으로 생성하여 해당 URL 로 requestBody 의 내용을 전송하기 위해 생성
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

    // 작성하는 화면시 필요한 이름과 사진을 불러와서 해당 뷰에 띄움
    public void setDisplay() {

        write_name.setText(PropertyManager.getInstance().getUserName());

            Glide.with(this)
                    .load(profileUrl)
                    .override(200, 200)
                    .error(R.drawable.write_pro_default)
                    .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                    .into(write_profile);

    }

    // onActivityResult 매소드는 Intent 로 값을 전달하고 다시 잘 되돌아 오는지 체크
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 되돌아온 코드 값이 REQ_CODE_SELECT_IMAGE 일 경우
        if (requestCode == REQ_CODE_SELECT_IMAGE) {

            if (resultCode == Activity.RESULT_OK) {

                try {

                    String nameImage = getImageNameToUri(data.getData());

                    // 화면에 선택한 이미지 크기 결정 및 화면에 보이기
                    write_select_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    write_select_image.setVisibility(View.VISIBLE);

                    // 이미지를 보여주기 위해 Glide 라이브러리를 사용
                    Glide.with(this)
                            .load(data.getData())
                            .into(write_select_image);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        // 되돌아 온 코드 값이 REQ_CODE_CAPTRUE_IMAGE 일 경우
        } else if (requestCode == REQ_CODE_CAPTRUE_IMAGE) {

            if (resultCode == Activity.RESULT_OK) {

                Uri imageUri = Uri.parse(imgPath);
                File file = new File(imageUri.getPath());

                Log.d("하이", "imageUri : " + imageUri);
                Log.d("하이", "file : " + file.getPath());

                // 화면에 선택한 이미지 크기 결정 및 화면에 보이기
                write_select_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                write_select_image.setVisibility(View.VISIBLE);

                // 이미지를 보여주기 위해 Glide 라이브러리를 사용
                Glide.with(this)
                        .load(imageUri)
                        .into(write_select_image);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // URI 에서 파일명을 추출하는 매소드
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

    // 이미지에 대한 파일을 만들어서 내부 저장소에 저장하는 매소드
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

        input_image = image.getPath();

        imgPath = "file:" + image.getAbsolutePath();
        return image;
    }

    // 카메라로 촬영시 실행되는 매소드
    public void doTakePhotoAction() throws IOException {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mImageCaptureUri = FileProvider.getUriForFile(WriteActivity.this,
                "org.kccc.prayer111.provider",
                createImageFile());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, REQ_CODE_CAPTRUE_IMAGE);
    }

    // BackKey 눌렀을 때 호출되는 함수 오버라이드
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
