package org.kccc.prayer111;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.PushManager;
import com.pushwoosh.fragment.PushEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PushEventListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private BackPressCloseHandler backPressCloseHandler;

    private boolean today_checked;

    String today_pray_content = null;
    String month_pray_content = null;

    String day;

    String userId;
    String name;
    String password;
    String profile;
    String email;

    Boolean loginCheck = false;

    SharedPreferences mPref;
    SharedPreferences dayCheck;
    SharedPreferences.Editor dayEditor;
    Boolean bFirst;

    public static final int REQUEST_MAIN = 2501;

    private static final int REQUEST_CODE_EXTERNAL_STORAGE_CONTACTS = 251;

    private static String[] PERMISSION_NEEDED = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_CONTACTS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pushwoosh 기본 세팅
        registerReceivers();
        PushManager pushManager = PushManager.getInstance(this);

        pushManager.setNotificationFactory(new NotificationFactory());


        processPermissions();

        try {
            pushManager.onStartup(this);
        } catch (Exception e) {
            Log.d("하이", e.getLocalizedMessage());
        }
        pushManager.registerForPushNotifications();
        checkMessage(getIntent());

        // 시스템바 색상 변경 단 API Level 21이상일때만 변경
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }
        today_checked = false;

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat dd = new SimpleDateFormat("d", Locale.KOREA);
        day = dd.format(date);

        Log.d("하이", "시스템바 변경");

//        Intent intent = new Intent(MainActivity.this, NotiService.class);
//        startService(intent);

//        new Thread() {
//            @Override
//            public void run() {
//
//                try {
//                    SendPushNotification sendPushNotification = new SendPushNotification();
//                    sendPushNotification.SendPush();
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();

        // 첫 실행 판단
        try {

            mPref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
            dayCheck = getSharedPreferences("check", Activity.MODE_PRIVATE);
            bFirst = mPref.getBoolean("isFirst", false);
            dayEditor = dayCheck.edit();
            if (day.equals("1")) {
                dayEditor.clear();

            }

        } catch (Exception e) {

        }

        // 백버튼 두번 눌러서 꺼지게 하기 위해 핸들러 생성하여 던짐
        backPressCloseHandler = new BackPressCloseHandler(this);

        // 타이틀 바 세팅
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        Log.d("하이", "타이틀바 변경");

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // 탭바 세팅 및 색상
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(0xFFa096db, 0xFF6b58ca);
        tabLayout.setSelectedTabIndicatorColor(0xFF6b58ca);
        Log.d("하이", "탭바 변경");

        CheckBox checked = (CheckBox) findViewById(R.id.checkBox);

        // 페이스북 공유를 위한 sdk선언 및 다이얼로그 박스
        FacebookSdk.sdkInitialize(getApplicationContext());
        CallbackManager callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);

        FloatingActionButton fab_pray = (FloatingActionButton) findViewById(R.id.fab_check_today);
        if (mViewPager.getCurrentItem() == 0) {
            fab_pray.setVisibility(View.VISIBLE);
            Log.d("하이", "나타났음 : " + mViewPager.getCurrentItem());
        }



        fab_pray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Log.d("하이", "클릭된 날짜 : " + day);
//
//                dayEditor.putString(day, day);
//
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);

                        SimpleDateFormat dd = new SimpleDateFormat("d", Locale.KOREA);
                        day = dd.format(date);

                Toast.makeText(MainActivity.this, day + "일, 오늘 기도를 하였습니다.", Toast.LENGTH_SHORT).show();

            }
        });

        // 카카오톡 공유 펩버튼 및 클릭시
        FloatingActionButton fab_kakao = (FloatingActionButton) findViewById(R.id.fab_share_kakao);
        fab_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mViewPager.getCurrentItem() == 0) {

                    // 뷰페이저가 0일 경우(오늘의 기도) 인텐트를 통해 카카오톡 공유 실행
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "[오늘의 기도]\n");

                    intent.putExtra(Intent.EXTRA_TEXT, today_pray_content);
                    intent.setPackage("com.kakao.talk");

                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "카카오톡이 설치가 안되어있습니다.", Toast.LENGTH_SHORT).show();
                    }

                } else if (mViewPager.getCurrentItem() == 1) {

                    // 뷰페이저가 1일 경우(이달의 기도) 인텐트를 통해 카카오톡 공유 실행
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "[이달의 기도]\n");

                    intent.putExtra(Intent.EXTRA_TEXT, month_pray_content);
                    intent.setPackage("com.kakao.talk");

                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "카카오톡이 설치가 안되어있습니다.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        FloatingActionButton fab_facebook = (FloatingActionButton) findViewById(R.id.fab_share_facebook);
        fab_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("하이", "지금 현재" + mViewPager.getCurrentItem());

                if (mViewPager.getCurrentItem() == 0) {

                    Toast.makeText(getApplicationContext(), "페이스북 오늘의 기도 공유하기 성공", Toast.LENGTH_SHORT).show();

                    Log.d("하이", "기도 : " + today_pray_content);

                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentTitle("오늘의 기도")
                            .setContentDescription(
                                    today_pray_content)
                            .setContentUrl(Uri.parse("https://www.facebook.com/111Pray/posts/1737817673214781"))
                            .setShareHashtag(new ShareHashtag.Builder()
                                    .setHashtag("#111기도")
                                    .build())
                            .build();

                    try {
                        shareDialog.show(content);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "패이스북 설치가 안되어있습니다.", Toast.LENGTH_SHORT).show();
                    }


                } else if (mViewPager.getCurrentItem() == 1) {

                    Toast.makeText(getApplicationContext(), "페이스북 이달의 기도 공유하기 성공", Toast.LENGTH_SHORT).show();

                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentTitle("이달의 기도")
                            .setContentDescription(
                                    month_pray_content)
                            .setContentUrl(Uri.parse("https://www.facebook.com/111Pray/posts/1737817673214781"))
                            .setShareHashtag(new ShareHashtag.Builder()
                                    .setHashtag("#111기도")
                                    .build())
                            .build();

                    try {
                        shareDialog.show(content);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "패이스북 설치가 안되어있습니다.", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        FloatingActionButton fab_write = (FloatingActionButton) findViewById(R.id.fab_write);
        fab_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(name)) {

                    Intent writeIntent = new Intent(MainActivity.this, WriteActivity.class);
                    Toast.makeText(MainActivity.this, "자동로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                    writeIntent.putExtra("userId", userId);
                    writeIntent.putExtra("name", name);
                    writeIntent.putExtra("password", password);
                    writeIntent.putExtra("user_profile", profile);
                    Log.d("하이", "들어갔음?");
                    startActivity(writeIntent);
                } else {

                    Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
                    signInIntent.putExtra("position", "write");
                    Log.d("하이", "안들어갔음?");
                    startActivity(signInIntent);
                }

            }
        });

        Log.d("하이", "팹버튼 변경");

//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    SendPushNotification sendPushNotification = new SendPushNotification();
//                    sendPushNotification.SendPush();
//                } catch (MalformedURLException e) {
//
//                } catch (JSONException e) {
//
//                }
//            }
//        }.start();


        try {
            String position = getIntent().getStringExtra("position");
            if (position.equals("cmt") || position.equals("write")) {
                mViewPager.setCurrentItem(2);
                fab_write.setVisibility(View.VISIBLE);
                findViewById(R.id.multiple_action).setVisibility(View.GONE);

            } else if (position.equals("main")){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_MAIN) {

                today_checked = data.getBooleanExtra("checked", today_checked);

            }
        }

        if (resultCode == Activity.RESULT_CANCELED) {

            today_checked = false;

        }

    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkMessage(intent);
        }
    };

    private BroadcastReceiver mReceiver = new BasePushMessageReceiver() {
        @Override
        protected void onMessageReceive(Intent intent) {
            doOnMessageReceive(intent.getExtras().getString(JSON_DATA_KEY));
        }
    };

    public void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");
        registerReceiver(mReceiver, intentFilter, getPackageName() + ".permission.C2D_MESSAGE", null);
        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unregisterReceivers() {
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {

        }

        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("하이", "onResume");
        registerReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("하이", "onPause");
        unregisterReceivers();
    }

    @Override
    protected void onStart() {
        Log.d("하이", "onStart");

        loginCheck = getIntent().getBooleanExtra("check", false);

        userId = getIntent().getStringExtra("userId");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        profile = getIntent().getStringExtra("user_profile");
        Log.d("하이", "이름 : " + name);
        Log.d("하이", "Id : " + userId);
        Log.d("하이", "email : " + email);
        Log.d("하이", "pw : " + password);
        Log.d("하이", "photo : " + profile);
        Log.d("하이", "check : " + loginCheck );

        Log.d("하이", "이미지 : " + PropertyManager.getInstance().getUserProfile());
        Log.d("하이", "아이디 : " + PropertyManager.getInstance().getUserId());
        Log.d("하이", "이름 : " + PropertyManager.getInstance().getUserName());
        Log.d("하이", "이메일 : " + PropertyManager.getInstance().getUserId());

        super.onStart();

    }

    @Override
    protected void onRestart() {
        Log.d("하이", "onRestart");
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!loginCheck) {
            menu.removeItem(R.id.action_logout);
        } else {
            menu.removeItem(R.id.action_login);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_intro) {

            Intent introduceIntent = new Intent(this, IntroduceActivity.class);
            startActivity(introduceIntent);

        } else if (id == R.id.action_calender) {

            Intent calenderIntent = new Intent(this, CalendarActivity.class);
            calenderIntent.putExtra("checked", today_checked);
            calenderIntent.putExtra("day", day);
            startActivityForResult(calenderIntent, REQUEST_MAIN);

        } else if (id == R.id.action_logout) {

            Intent infoIntent = new Intent(this, ProfileActivity.class);
            infoIntent.putExtra("name", name);
            infoIntent.putExtra("userId", userId);
            infoIntent.putExtra("user_profile", profile);
            startActivity(infoIntent);

        } else if (id == R.id.action_licence) {

            Intent licenceIntent = new Intent(this, LicenceActivity.class);
            startActivity(licenceIntent);

        } else if (id == R.id.action_login) {

            Intent loginIntent = new Intent(this, SignInActivity.class);
            loginIntent.putExtra("position", "main");
            loginIntent.putExtra("check", loginCheck);
            startActivity(loginIntent);

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        checkMessage(intent);
    }

    @Override
    public void doOnRegistered(String s) {
        Log.i("하이", "Registered for pushes: " + s);
    }

    @Override
    public void doOnRegisteredError(String s) {
        Log.e("하이", "Failed to register for pushes: " + s);
    }

    @Override
    public void doOnMessageReceive(String s) {
        Log.i("하이", "Notification opened: " + s);
    }

    @Override
    public void doOnUnregistered(String s) {
        Log.i("하이", "Unregistered from pushes: " + s);
    }

    @Override
    public void doOnUnregisteredError(String s) {
        Log.e("하이", "Failed to unregister from pushes: " + s);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private void checkMessage(Intent intent)
    {
        if (null != intent)
        {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
            {
                doOnMessageReceive(intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
            }
            else if (intent.hasExtra(PushManager.REGISTER_EVENT))
            {
                doOnRegistered(intent.getExtras().getString(PushManager.REGISTER_EVENT));
            }
            else if (intent.hasExtra(PushManager.UNREGISTER_EVENT))
            {
                doOnUnregistered(intent.getExtras().getString(PushManager.UNREGISTER_EVENT));
            }
            else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
            {
                doOnRegisteredError(intent.getExtras().getString(PushManager.REGISTER_ERROR_EVENT));
            }
            else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
            {
                doOnUnregisteredError(intent.getExtras().getString(PushManager.UNREGISTER_ERROR_EVENT));
            }

            resetIntentValues();
        }
    }

    private void resetIntentValues()
    {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }

    public void  processPermissions() {

        if (!hasStorageGranted() || !hasContactsGranted() || !hasNotificationGranted()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                            this, android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE)) {
                Toast.makeText(this, "설정에서 '저장소와 '알림 받기'권한을 모두 승인해주세요", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(
                    this,
                    PERMISSION_NEEDED,
                    REQUEST_CODE_EXTERNAL_STORAGE_CONTACTS
            );
        }
    }

    private boolean hasStorageGranted() {
        int permissionStorage = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);

        return permissionStorage == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasContactsGranted() {
        int permissionContacts = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);

        return permissionContacts == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasNotificationGranted() {
        int permissionNotification = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE);

        return permissionNotification == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_EXTERNAL_STORAGE_CONTACTS:

                Log.d("하이", "grantResults : " + grantResults.length);

                Log.d("하이", "grantResults : " + grantResults[0] + grantResults[1] + grantResults[2]);

                if (bFirst == false) {
                    Log.d("하이", "first");

                    if (grantResults.length == 3 && grantResults[0] + grantResults[1] + grantResults[2]
                            == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this,
                                "'저장소 읽기'와 '알림 받기'가 모두 승인되었습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                "'저장소 읽기'와 '알림 받기'요청을 모두 혹은 일부 거부하셨습니다", Toast.LENGTH_SHORT).show();
                    }

                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putBoolean("isFirst", true);
                    editor.commit();
                }

                if (bFirst == true) {
                    Log.d("하이", "not first");
                }

                break;

            default:
                break;
        }
    }

}
