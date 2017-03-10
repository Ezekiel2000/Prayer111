package org.kccc.prayer111;

import android.content.Intent;
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
    private String email;
    private String password;

    private SessionCallback mKakaocallback;

    private CallbackManager mFacebookcallbackManager;
    private AccessToken mToken = null;


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

        if (checkBox.isChecked()) {
            text_input_email.setText(PropertyManager.getInstance().getUserName());
            text_input_password.setText(PropertyManager.getInstance().getPassword());
        }

        // 일반적인 이메일 로그인 버튼 클릭 시
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (text_input_email.getText().length() != 0 && text_input_password.getText().length() != 0) {

                    if (checkBox.isChecked()) {

                        PropertyManager.getInstance().setUserName(text_input_email.getText().toString());
                        PropertyManager.getInstance().setPassword(text_input_password.getText().toString());

                    }

                    // 서버에서 이메일과 비밀번호를 검색해서 일치되는 것이 있을 경우 로그인
                    // 일치되는 것이 없을 경우 Toast를 사용하여 다시 입력하

                    Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                    setLayoutText();

                    Intent intent;

                    if (getIntent().getStringExtra("position").equals("cmt")) {

                        intent = new Intent(getBaseContext(), CommentListActivity.class);

                        intent.putExtra("user_profile", profileUrl);
                        intent.putExtra("userId", userId);
                        intent.putExtra("name", userName);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);

                    } else {

                        intent = new Intent(getBaseContext(), WriteActivity.class);

                        intent.putExtra("user_profile", profileUrl);
                        intent.putExtra("userId", userId);
                        intent.putExtra("name", userName);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);

                    }


                    startActivity(intent);
                    finish();


                } else {
                    Toast.makeText(getApplicationContext(), "빈칸을 입력하세요", Toast.LENGTH_SHORT).show();
                }


            }
        });

        signtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signIntent = new Intent(getApplicationContext(), SignUpActivity.class);
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
                                email = object.getString("email");      // 페이스북의 키 값
                                userName = object.getString("name");
                                profileUrl = "https://graph.facebook.com/" + userId + "/picture";

                                setLayoutText();

                                if (getIntent().getStringExtra("position").equals("cmt")) {

                                    Intent intent = new Intent(getBaseContext(), CommentListActivity.class);

                                    intent.putExtra("user_profile", profileUrl);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("name", userName);
                                    intent.putExtra("email", email);
                                    intent.putExtra("password", password);
                                    startActivity(intent);

                                } else {
                                    Intent intent = new Intent(getBaseContext(), WriteActivity.class);

                                    intent.putExtra("user_profile", profileUrl);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("name", userName);
                                    intent.putExtra("email", email);
                                    intent.putExtra("password", password);
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

                setLayoutText();

                if (getIntent().getStringExtra("position").equals("cmt")) {

                    Intent intent = new Intent(getBaseContext(), CommentListActivity.class);

                    intent.putExtra("user_profile", profileUrl);
                    intent.putExtra("userId", userId);
                    intent.putExtra("name", userName);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getBaseContext(), WriteActivity.class);

                    intent.putExtra("user_profile", profileUrl);
                    intent.putExtra("userId", userId);
                    intent.putExtra("name", userName);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    startActivity(intent);
                }

                finish();
            }
        });
    }

    private void setLayoutText() {

        Log.d("하이", "이미지 : " + profileUrl);
        Log.d("하이", "아이디 : " + userId);
        Log.d("하이", "이름 : " + userName);
        Log.d("하이", "이메일 : " + email);


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
