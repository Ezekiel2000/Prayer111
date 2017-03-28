package org.kccc.prayer111;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LicenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);

        // 시스템바 색상 변경 단 API Level 21이상일때만 변경
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }

    }
}
