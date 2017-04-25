package org.kccc.prayer111;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by ezekiel on 2017. 2. 10..
 */

// 백 키를 두번 눌렀을 경우에만 종료시키는 클래스
public class BackPressCloseHandler {

    private long backKeyPressTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity activity) {
        this.activity = activity;
    }

    // 대신 2초 이내에 백 키를 2번 눌렀을 경우에만 종료시킴
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressTime + 2000) {
            backKeyPressTime = System.currentTimeMillis();
            showGuide();
            return;
        } else {
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, "한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
