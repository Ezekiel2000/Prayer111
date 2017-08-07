package org.kccc.prayer111;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by ezekiel on 2017. 2. 27..
 */

// SharedPreferences 를 통해 각 정보들을 저장
public class PropertyManager {
    private static PropertyManager instance;

    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    private PropertyManager() {

        mPrefs = PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getmContext());
        mEditor = mPrefs.edit();
    }

    private static final String KEY_USER_NAME = "username";
    private String mUserName;

    // 이름을 불러오기 위한 매소드
    public String getUserName() {
        if (mUserName == null) {
            mUserName = mPrefs.getString(KEY_USER_NAME, "");
        }
        return mUserName;
    }

    // 이름을 저장하기 위한 매소드
    public void setUserName(String userName) {
        mUserName = userName;
        mEditor.putString(KEY_USER_NAME, userName);
        mEditor.commit();
    }

    private static final String KEY_USER_PASSWORD = "password";
    private String mUserPassword;

    // 패읏워드를 불러오기 위한 매소드
    public String getPassword() {
        if (mUserPassword == null) {
            mUserPassword = mPrefs.getString(KEY_USER_PASSWORD, "");
        }
        return mUserPassword;
    }

    // 패스워드를 저장하기 위한 매소드
    public void setPassword(String UserPassword) {
        mUserPassword = UserPassword;
        mEditor.putString(KEY_USER_PASSWORD, UserPassword);
        mEditor.commit();
    }

    private static final String KEY_USER_ID = "id";
    private String mUserId;

    // 아이디를 불러오기 위한 매소드
    public String getUserId() {
        if (mUserId == null) {
            mUserId = mPrefs.getString(KEY_USER_ID, "");
        }
        return mUserId;
    }

    // 아이디를 저장하기 위한 매소드
    public void setUserId(String UserId) {
        mUserId = UserId;
        mEditor.putString(KEY_USER_ID, UserId);
        mEditor.commit();
    }

    private static final String KEY_USER_PROFILE = "profile";
    private String mUserProfile;

    // 프로필 사진을 불러오기 위한 매소드
    public String getUserProfile() {
        if (mUserProfile == null) {
            mUserProfile = mPrefs.getString(KEY_USER_PROFILE, "");
        }
        return mUserProfile;
    }

    // 프로필 사진을 저장하기 위한 매소드
    public void setUserProfile(String UserProfile) {
        mUserProfile = UserProfile;
        mEditor.putString(KEY_USER_PROFILE, UserProfile);
        mEditor.commit();
    }

    private static final String KEY_USER_LOGIN_TYPE = "type";
    private String mUserType;

    // 로그인 타입을 불러오기 위한 매소드
    public String getUserLoginType() {
        if (mUserType == null) {
            mUserType = mPrefs.getString(KEY_USER_LOGIN_TYPE, "");
        }
        return mUserType;
    }

    // 로그인 타입을 저장하기 위한 매소드
    public void setUserLoginType(String UserLoginType) {
        mUserType = UserLoginType;
        mEditor.putString(KEY_USER_LOGIN_TYPE, UserLoginType);
        mEditor.commit();
    }

    private static final String KEY_LOGIN_CHECK = "login";
    private boolean mLoginCheck = false;

    // 로그인 상태를 불러오기 위한 매소드
    public boolean getLoginCheck() {
        if (!mLoginCheck) {
            mLoginCheck = mPrefs.getBoolean(KEY_LOGIN_CHECK, false);
        }
        return mLoginCheck;
    }

    // 로그인 상티를 저장하기 위한 매소드
    public void setLoginCheck(boolean LoginCheck) {
        mLoginCheck = LoginCheck;
        mEditor.putBoolean(KEY_REMEMBER_CHECK, LoginCheck);
        mEditor.commit();
        Log.d("하이", "로그인 결과" + LoginCheck);
    }

    private static final String KEY_REMEMBER_CHECK = "remember";
    private boolean mUserCheck = false;

    // 이메일 로그인시 이메일 패스워드 기억을 여부를 체크하기 위한 메소드
    public boolean getUserRememberCheck() {
        if (!mUserCheck) {
            mUserCheck = mPrefs.getBoolean(KEY_REMEMBER_CHECK, false);
        }
        return mUserCheck;
    }

    // 이메일 로그인시 이메일 패스워드 기억을 여부를 저장하기 위한 메소드
    public void setUserRememberCheck(boolean UserCheck) {
        mUserCheck = UserCheck;
        mEditor.putBoolean(KEY_REMEMBER_CHECK, UserCheck);
        mEditor.commit();
        Log.d("하이", "set 결과" + UserCheck);
    }

    // 저장기록들을 지우기 위한 매소드
    public void remove() {
        mEditor.clear();
    }

}
