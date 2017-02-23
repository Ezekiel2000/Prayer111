package org.kccc.prayer111;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;

import static org.kccc.prayer111.R.string.pray;

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

    String pray_content = null;

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
        Intent signInIntent = new Intent(this, SignInActivity.class);
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


        Log.d("하이", "기도" + pray_content);

        setTitle();

        FacebookSdk.sdkInitialize(getApplicationContext());

        CallbackManager callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);


        FloatingActionButton fab_kakao = (FloatingActionButton) findViewById(R.id.fab_share_kakao);
        fab_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mViewPager.getCurrentItem() == 0) {
                    Toast.makeText(getApplicationContext(), "카카오톡 오늘의 기도 공유하기 성공", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "[오늘의 기도]\n");

                    getPrayContent();

                    intent.putExtra(Intent.EXTRA_TEXT, "기도" + pray);
                    intent.setPackage("com.kakao.talk");
                    startActivity(intent);

                } else if (mViewPager.getCurrentItem() == 1) {
                    Toast.makeText(getApplicationContext(), "카카오톡 이달의 기도 공유하기 성공", Toast.LENGTH_SHORT).show();
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

//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "[오늘의 기도]\n");
//                    intent.putExtra(Intent.EXTRA_TEXT, "기도" + pray);
//                    intent.setPackage("com.facebook.katana");
//                    startActivity(intent);


                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentTitle("오늘의 기도")
                            .setContentDescription(
                                    "테스트중입니다")
                            .setContentUrl(Uri.parse("https://www.facebook.com/111Pray/"))
                            .setShareHashtag(new ShareHashtag.Builder()
                                    .setHashtag("#111기도")
                                    .build())
                            .build();
                    shareDialog.show(content);


                } else if (mViewPager.getCurrentItem() == 1) {

                    Toast.makeText(getApplicationContext(), "페이스북 이달의 기도 공유하기 성공", Toast.LENGTH_SHORT).show();

                }





            }
        });

        FloatingActionButton fab_write = (FloatingActionButton) findViewById(R.id.fab_write);
        fab_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signInIntent);
            }
        });

        Log.d("하이", "팹버튼 변경");

    }

    private void setTitle() {
        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceSize = new Point();
        display.getSize(deviceSize);
        float scale = getResources().getDisplayMetrics().density;

        Log.d("하이", String.format("[%sx%s]px %sx -> [%sx%s]dp", deviceSize.x, deviceSize.y, scale, (int)(deviceSize.x/scale), (int)(deviceSize.y/scale)));
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

    public void getPrayContent() {

        TodayFragment fragment = (TodayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_today);
        fragment = new TodayFragment();

        String pray = fragment.today_pray;

        Log.d("하이", "기도내용" + pray);

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
