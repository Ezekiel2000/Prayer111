package org.kccc.prayer111;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by ezekiel on 2017. 2. 27..
 */

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

    public String getUserName() {
        if (mUserName == null) {
            mUserName = mPrefs.getString(KEY_USER_NAME, "");
        }
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
        mEditor.putString(KEY_USER_NAME, userName);
        mEditor.commit();
    }

    private static final String KEY_USER_PASSWORD = "password";
    private String mUserPassword;

    public String getPassword() {
        if (mUserPassword == null) {
            mUserPassword = mPrefs.getString(KEY_USER_PASSWORD, "");
        }
        return mUserPassword;
    }

    public void setPassword(String UserPassword) {
        mUserPassword = UserPassword;
        mEditor.putString(KEY_USER_PASSWORD, UserPassword);
        mEditor.commit();
    }

    private static final String KEY_USER_ID = "id";
    private String mUserId;

    public String getUserId() {
        if (mUserId == null) {
            mUserId = mPrefs.getString(KEY_USER_ID, "");
        }
        return mUserId;
    }

    public void setUserId(String UserId) {
        mUserId = UserId;
        mEditor.putString(KEY_USER_ID, UserId);
        mEditor.commit();
    }

    private static final String KEY_USER_PROFILE = "profile";
    private String mUserProfile;

    public String getUserProfile() {
        if (mUserProfile == null) {
            mUserProfile = mPrefs.getString(KEY_USER_PROFILE, "");
        }
        return mUserProfile;
    }

    public void setUserProfile(String UserProfile) {
        mUserProfile = UserProfile;
        mEditor.putString(KEY_USER_PROFILE, UserProfile);
        mEditor.commit();
    }

    private static final String KEY_USER_LOGIN_TYPE = "type";
    private String mUserType;

    public String getUserLoginType() {
        if (mUserType == null) {
            mUserType = mPrefs.getString(KEY_USER_LOGIN_TYPE, "");
        }
        return mUserType;
    }

    public void setUserLoginType(String UserLoginType) {
        mUserType = UserLoginType;
        mEditor.putString(KEY_USER_LOGIN_TYPE, UserLoginType);
        mEditor.commit();
    }

    private static final String KEY_LOGIN_CHECK = "login";
    private boolean mLoginCheck = false;

    public boolean getLoginCheck() {
        if (!mLoginCheck) {
            mLoginCheck = mPrefs.getBoolean(KEY_LOGIN_CHECK, false);
        }
        return mLoginCheck;
    }

    public void setLoginCheck(boolean LoginCheck) {
        mLoginCheck = LoginCheck;
        mEditor.putBoolean(KEY_REMEMBER_CHECK, LoginCheck);
        mEditor.commit();
        Log.d("하이", "로그인 결과" + LoginCheck);
    }

    private static final String KEY_REMEMBER_CHECK = "remember";
    private boolean mUserCheck = false;

    public boolean getUserRememberCheck() {
        if (!mUserCheck) {
            mUserCheck = mPrefs.getBoolean(KEY_REMEMBER_CHECK, false);
        }
        return mUserCheck;
    }

    public void setUserRememberCheck(boolean UserCheck) {
        mUserCheck = UserCheck;
        mEditor.putBoolean(KEY_REMEMBER_CHECK, UserCheck);
        mEditor.commit();
        Log.d("하이", "set 결과" + UserCheck);
    }


    public void remove() {
        mEditor.clear();
    }

}
