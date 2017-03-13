package org.kccc.prayer111;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
        return mUserName;
    }

    public void setPassword(String UserPassword) {
        mUserPassword = UserPassword;
        mEditor.putString(KEY_USER_PASSWORD, UserPassword);
        mEditor.commit();
    }

    private static final String KEY_USER_EMAIL = "email";
    private String mUserEmail;

    public String getUserEmail() {
        if (mUserEmail == null) {
            mUserEmail = mPrefs.getString(KEY_USER_EMAIL, "");
        }
        return mUserEmail;
    }

    public void setUserEmail(String UserEmail) {
        mUserEmail = UserEmail;
        mEditor.putString(KEY_USER_EMAIL, UserEmail);
        mEditor.commit();
    }

    private static final String KEY_USER_PROFILE = "profile";
    private String mUserProfile;

    public String getUserProfile() {
        if (mUserProfile == null) {
            mUserProfile = mPrefs.getString(KEY_USER_PASSWORD, "");
        }
        return mUserName;
    }

    public void setUserProfile(String UserProfile) {
        mUserProfile = UserProfile;
        mEditor.putString(KEY_USER_PROFILE, UserProfile);
        mEditor.commit();
    }

}
