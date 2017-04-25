package org.kccc.prayer111;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.kakao.auth.KakaoSDK;

/**
 * Created by ezekiel on 2017. 2. 15..
 */

public class GlobalApplication extends Application {

    private static GlobalApplication mInstance;
    private static volatile Activity currentActivity = null;
    private static Context mContext;

    public static Activity getCurrentAcitivty() {
        return currentActivity;
    }

    public static void setCurrentAcitivty(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }


    public static GlobalApplication getGlobalApplicationContext() {
        if (mInstance == null) {
            throw new IllegalStateException("this, application dose not inherit GlobalApplication");
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        KakaoSDK.init(new KaKaoSDKAdapter());
        mContext = this;
    }

    public static Context getmContext() {
        return mContext;
    }
}
