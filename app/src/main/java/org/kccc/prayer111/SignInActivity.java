package org.kccc.prayer111;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {

    boolean saveLoginData;

    EditText text_input_email;
    EditText text_input_password;
    CheckBox checkBox;
    Button loginBtn;
    Button kakaoLoginBtn;
    Button facebookLoginBtn;
    TextView signtext;

    private String userName;
    private String userId;
    private String profileUrl;
    private String password;

    User user;

    Bitmap mSaved;

    private SessionCallback mKakaocallback;

    private CallbackManager mFacebookcallbackManager;
    private AccessToken mToken = null;

    private static String setUrl = "http://api.kccc.org/AppAjax/111prayer/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }

        login();

    }

    private void login() {

        text_input_email = (EditText) findViewById(R.id.text_input_email);
        text_input_password = (EditText) findViewById(R.id.text_input_password);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        loginBtn = (Button) findViewById(R.id.btn_sign_in);
        signtext = (TextView) findViewById(R.id.text_sign_up);
        kakaoLoginBtn = (Button) findViewById(R.id.btn_sign_kakaotalk);
        facebookLoginBtn = (Button) findViewById(R.id.btn_sign_facebook);

        signtext = (TextView) findViewById(R.id.text_sign_up);

        Boolean chk = PropertyManager.getInstance().getUserRememberCheck();
        Log.d("하이", "체크값 : " + chk);

        if (chk) {

            checkBox.setChecked(chk);
            Log.d("하이", "들어옴" );
            text_input_email.setText(PropertyManager.getInstance().getUserId());
            text_input_password.setText(PropertyManager.getInstance().getPassword());

        }

        // 일반적인 이메일 로그인 버튼 클릭 시
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chk) {

                    PropertyManager.getInstance().setUserId(text_input_email.getText().toString());
                    PropertyManager.getInstance().setPassword(text_input_password.getText().toString());

                }

                Log.d("하이", "암호값 : " + text_input_password.getText().toString() );
                password = text_input_password.getText().toString();
                userId = text_input_email.getText().toString();

                if (text_input_email.getText().length() != 0 && text_input_password.getText().length() != 0) {


                    new LoginProcess().execute();

                    // 서버에서 이메일과 비밀번호를 검색해서 일치되는 것이 있을 경우 로그인
                    // 일치되는 것이 없을 경우 Toast를 사용하여 다시 입력하

                } else {
                    Toast.makeText(getApplicationContext(), "빈칸을 입력하세요", Toast.LENGTH_SHORT).show();
                }


            }
        });

        signtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                signIntent.putExtra("position", getIntent().getStringExtra("position"));
                startActivity(signIntent);
            }
        });

        kakaoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isKakaoLogin();
            }
        });

        LoginManager.getInstance().logOut();

        facebookLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isFacebookLogin();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("하이", "Resume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("하이", "Pause");
        PropertyManager.getInstance().setUserRememberCheck(checkBox.isChecked());

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("하이", "Stop");
    }

    private class LoginProcess extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection conn = null;
            StringBuilder builder = new StringBuilder();

            try {

                URL url = new URL(setUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setChunkedStreamingMode(0);
                conn.setRequestMethod("POST");

                OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.write( "mode=loginProcess"
                        + "&email=" + userId
                        + "&password=" + password
                        + "&method=EMAIL" );
                writer.flush();
                writer.close();
                out.close();

                conn.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));


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

            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("하이", "onPost : " + result);

            try {

                JSONObject jsonObject = new JSONObject(result);

                Log.d("하이", "jsonObject : " + jsonObject);

                String resultData = jsonObject.getString("result");

                Log.d("하이", "resultData : " + resultData);

                JSONObject object = new JSONObject(resultData);

                if (resultData == null) {

                    Log.d("하이", "onPost null: " + object.toString());
//                    Toast.makeText(getBaseContext(), "아이디 또는 패스워드가 틀렸습니다.", Toast.LENGTH_SHORT).show();

                } else {


                    Log.d("하이", "onPost not null: " + object.toString());

                    profileUrl = object.getString("photo");
                    userId = object.getString("id");
                    userName = object.getString("name");
//                    password = object.getString("passwd");
                    userId = object.getString("email");

                    setLayoutText();

                    user = new User(profileUrl, userId, userName, "");

                    PropertyManager.getInstance().setUserName(userName);
                    PropertyManager.getInstance().setUserId(userId);
                    PropertyManager.getInstance().setPassword(password);
                    PropertyManager.getInstance().setUserProfile(profileUrl);
                    PropertyManager.getInstance().setUserLoginType("EMAIL");

                    Log.d("하이", "이미지 : " + profileUrl);

                    Intent intent = new Intent();

                    if (getIntent().getStringExtra("position").equals("cmt")) {

                        intent = new Intent(getBaseContext(), CommentListActivity.class);

                        Log.d("하이", "어디서 왔나 : " + "cmt");

                        intent.putExtra("user_profile", profileUrl);
                        intent.putExtra("userId", userId);
                        intent.putExtra("name", userName);
                        intent.putExtra("password", password);
//                        intent.putExtra("userData", user);
                        intent.putExtra("prayNumber", getIntent().getStringExtra("prayNumber"));
                        intent.putExtra("check", true);

                    } else if (getIntent().getStringExtra("position").equals("write")){

                        intent = new Intent(getBaseContext(), WriteActivity.class);

                        Log.d("하이", "어디서 왔나 : " + "write");

                        intent.putExtra("user_profile", profileUrl);
                        intent.putExtra("userId", userId);
                        intent.putExtra("name", userName);
                        intent.putExtra("password", password);
                        intent.putExtra("check", true);

                    } else if (getIntent().getStringExtra("position").equals("main")) {

                        intent = new Intent(getBaseContext(), MainActivity.class);

                        Log.d("하이", "어디서 왔나 : " + "main");

                        intent.putExtra("user_profile", profileUrl);
                        intent.putExtra("userId", userId);
                        intent.putExtra("name", userName);
                        intent.putExtra("password", password);
                        intent.putExtra("position", "main");
                        intent.putExtra("check", true);

                    }

                    Toast.makeText(getApplicationContext(), userName + "님. 반갑습니다.", Toast.LENGTH_SHORT).show();
                    setLayoutText();
                    startActivity(intent);
                    finish();

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "아이디 또는 패스워드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void isFacebookLogin() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookcallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(mFacebookcallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("하이", "페이스북 토큰 " + loginResult.getAccessToken().getToken());
                Log.d("하이", "페이스북 Id " + loginResult.getAccessToken().getUserId());
                userId = loginResult.getAccessToken().getUserId().toString();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null) {
                            Log.d("하이", "에러 : " + response.getError().getErrorMessage());
                        } else {
                            try {
//                                userId = object.getString("email");      // 페이스북의 키 값
                                userName = object.getString("name");
                                profileUrl = "https://graph.facebook.com/" + userId + "/picture?type=large";

                                setLayoutText();

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

                                            writer.write( "mode=joinProcess"
                                                    + "&name=" + userName
                                                    + "&email=" + userId
                                                    + "&photo=" + profileUrl
                                                    + "&method=facebook" );
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

                                PropertyManager.getInstance().setUserProfile(profileUrl);
                                PropertyManager.getInstance().setUserName(userName);
                                PropertyManager.getInstance().setUserId(userId);
                                PropertyManager.getInstance().setPassword(password);
                                PropertyManager.getInstance().setUserLoginType("facebook");


                                Toast.makeText(getApplicationContext(), userName + "님. 반갑습니다.", Toast.LENGTH_SHORT).show();

                                if (getIntent().getStringExtra("position").equals("cmt")) {

                                    Intent intent = new Intent(getBaseContext(), CommentListActivity.class);

                                    intent.putExtra("user_profile", profileUrl);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("name", userName);
                                    intent.putExtra("password", password);
                                    intent.putExtra("prayNumber", getIntent().getStringExtra("prayNumber"));
                                    intent.putExtra("check", true);

                                    startActivity(intent);

                                } else if (getIntent().getStringExtra("position").equals("write")) {
                                    Intent intent = new Intent(getBaseContext(), WriteActivity.class);

                                    intent.putExtra("user_profile", profileUrl);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("name", userName);
                                    intent.putExtra("password", password);
                                    intent.putExtra("check", true);

                                    startActivity(intent);
                                } else if (getIntent().getStringExtra("position").equals("main")) {

                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);

                                    Log.d("하이", "어디서 왔나 : " + "main");

                                    intent.putExtra("user_profile", profileUrl);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("name", userName);
                                    intent.putExtra("password", password);
                                    intent.putExtra("position", "main");
                                    intent.putExtra("check", true);

                                    startActivity(intent);
                                }

                                finish();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });
    }


    private void isKakaoLogin() {
        mKakaocallback = new SessionCallback();
        com.kakao.auth.Session.getCurrentSession().addCallback(mKakaocallback);
        Log.d("하이", "addCallback(mKakaocallback)");
        com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen();
        Log.d("하이", "checkAndImplicitOpen");
        com.kakao.auth.Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, SignInActivity.this);
        Log.d("하이", "getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, SignInActivity.this)");
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d("하이", "세션오픈 됨");
            KakaoRequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Log.d("하이", exception.getMessage());
            }
        }
    }

    protected void KakaoRequestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("하이", "오류");
            }

            @Override
            public void onNotSignedUp() {
                Log.d("하이", "onNotSignedUp");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                profileUrl = userProfile.getProfileImagePath();
                userId = String.valueOf(userProfile.getId());
                userName = userProfile.getNickname();

                PropertyManager.getInstance().setUserProfile(profileUrl);
                PropertyManager.getInstance().setUserName(userName);
                PropertyManager.getInstance().setUserId(userId);
                PropertyManager.getInstance().setUserLoginType("kakao");

                setLayoutText();

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

                            writer.write( "mode=joinProcess"
                                    + "&name=" + userName
                                    + "&email=" + userId
                                    + "&photo=" + profileUrl
                                    + "&method=kakao" );
                            writer.flush();
                            writer.close();
                            out.close();

                            Log.d("하이", "이름 : " + userName);
                            Log.d("하이", "이메일 : " + userId);

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

                PropertyManager.getInstance().setUserProfile(profileUrl);
                PropertyManager.getInstance().setUserName(userName);
                PropertyManager.getInstance().setUserId(userId);
                PropertyManager.getInstance().setPassword(password);
                PropertyManager.getInstance().setUserLoginType("kakao");

                Log.d("하이", "그래서 Id는 : " + userId);
                Toast.makeText(getApplicationContext(), userName + "님. 반갑습니다.", Toast.LENGTH_SHORT).show();

                if (getIntent().getStringExtra("position").equals("cmt")) {

                    Intent intent = new Intent(getBaseContext(), CommentListActivity.class);

                    intent.putExtra("user_profile", profileUrl);
                    intent.putExtra("userId", userId);
                    intent.putExtra("name", userName);
                    intent.putExtra("password", password);
                    intent.putExtra("prayNumber", getIntent().getStringExtra("prayNumber"));
                    intent.putExtra("check", true);
                    startActivity(intent);


                } else if (getIntent().getStringExtra("position").equals("write")) {
                    Intent intent = new Intent(getBaseContext(), WriteActivity.class);

                    intent.putExtra("user_profile", profileUrl);
                    intent.putExtra("userId", userId);
                    intent.putExtra("name", userName);
                    intent.putExtra("password", password);
                    intent.putExtra("check", true);
                    startActivity(intent);

                } else if (getIntent().getStringExtra("position").equals("main")) {

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);

                    Log.d("하이", "어디서 왔나 : " + "main");

                    intent.putExtra("user_profile", profileUrl);
                    intent.putExtra("userId", userId);
                    intent.putExtra("name", userName);
                    intent.putExtra("password", password);
                    intent.putExtra("position", "main");
                    intent.putExtra("check", true);

                    startActivity(intent);
                }

                finish();
            }
        });
    }

    private void setLayoutText() {

        Log.d("하이", "이미지 : " + PropertyManager.getInstance().getUserProfile());
        Log.d("하이", "아이디 : " + PropertyManager.getInstance().getUserId());
        Log.d("하이", "이름 : " + PropertyManager.getInstance().getUserName());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
        mFacebookcallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mKakaocallback);
    }
}
