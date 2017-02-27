package org.kccc.prayer111;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private BackPressCloseHandler backPressCloseHandler;

    private boolean today_checked;

    String today_pray_content = null;
    String month_pray_content = null;


    public static final int REQUEST_MAIN = 2501;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 시스템바 색상 변경 단 API Level 21이상일때만 변경
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF5f4fb2);
        }
        today_checked = false;

        Log.d("하이", "시스템바 변경");

        // 로딩 화면 띄우기
        Intent loadingIntent = new Intent(this, LoadingActivity.class);
        loadingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loadingIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        startActivity(loadingIntent);

        Log.d("하이", "로딩");

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

        String name = PropertyManager.getInstance().getUserName();

        // 페이스북 공유를 위한 sdk선언 및 다이얼로그 박스
        FacebookSdk.sdkInitialize(getApplicationContext());
        CallbackManager callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);

        // 카카오톡 공유 펩버튼 및 클릭시
        FloatingActionButton fab_kakao = (FloatingActionButton) findViewById(R.id.fab_share_kakao);
        fab_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mViewPager.getCurrentItem() == 0) {


                    // 카카오 링크 사용할 경우(웹사이트 링크는 도메인이 없기 때문에 어려움)
//                    try {
//
//                        Uri uri = Uri.parse("R.drawable.app_icon");
//
//                        KakaoLink kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
//                        KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
//                        kakaoTalkLinkMessageBuilder
//                                .addText("[오늘의 기도]\n" + today_pray_content)
//                                .addImage("uri", 100, 100)
//                                .addWebButton("페이지로 이동", "http://www.kakao.com/services/8")
//                                .build();
//                        kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, v.getContext());
//
//                    } catch (KakaoParameterException e) {
//                        e.printStackTrace();
//                        Toast.makeText(MainActivity.this, "카카오톡이 설치가 안되어있습니다.", Toast.LENGTH_SHORT).show();
//                    }

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

                if (!name.equals("")) {
                    String password = PropertyManager.getInstance().getPassword();
                    String profile = PropertyManager.getInstance().getUserProfile();
                    if (!password.equals("")) {
                        Intent writeIntent = new Intent(MainActivity.this, SignInActivity.class);
                        Toast.makeText(MainActivity.this, "자동로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                        writeIntent.putExtra("name", name);
                        writeIntent.putExtra("user_profile", profile);
                        startActivity(writeIntent);
                    }
                } else {
                    Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(signInIntent);
                }

            }
        });

        Log.d("하이", "팹버튼 변경");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

            return true;
        } else if (id == R.id.action_calender) {

            Intent calenderIntent = new Intent(this, CalendarActivity.class);
            calenderIntent.putExtra("checked", today_checked);
            startActivityForResult(calenderIntent, REQUEST_MAIN);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("하이", "Main onStart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("하이", "Main onResume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("하이", "Main onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("하이", "Main onStop");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("하이", "Main onDestroy");
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
