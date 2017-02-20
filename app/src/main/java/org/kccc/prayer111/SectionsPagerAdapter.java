package org.kccc.prayer111;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by ezekiel on 2017. 2. 1..
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.

        Log.d("하이", "플래그먼트 인스턴스" + position );

        switch (position) {
            case 0:
                return TodayFragment.newInstance();
            case 1:
                return MonthFragment.newInstance();
            case 2:
                return IntercessionFragment.newInstance();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {



        switch (position) {
            case 0:
                return "오늘의 기도";
            case 1:
                return "이달의 기도";
            case 2:
                return "중보기도";
        }
        return null;
    }
}
