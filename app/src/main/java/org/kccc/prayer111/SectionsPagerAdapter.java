package org.kccc.prayer111;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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

    // 매개변수에 따라 페이지 제목값을 리턴함
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
