package com.ffx.novelreader.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by TwoFlyLiu on 2019/8/6.
 */

public class MainFragmentAdapter extends FragmentPagerAdapter {
    public static final int PAGE_COUNT = 3;
    public static String[] titleArray = new String[]{"书架", "搜索", "下载"};

    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
       if (0 == position) {
           return BookShelfFragment.newInstance();
       } else if (1 == position) {
           return MainSearchFragment.newInstance();
       } else {
           return DownloadFragment.newInstance();
       }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position < PAGE_COUNT) {
            return titleArray[position];
        }
        return "书架";
    }
}
