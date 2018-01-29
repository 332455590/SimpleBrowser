package com.renny.simplebrowser.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by renjianan on 2016/9/13.
 */
public class WebViewFragmentAdapter extends FragmentPagerAdapter{

    private List<Fragment> mFragments;
    private List<String> titles;
    public WebViewFragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.mFragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments != null && mFragments.size() > 0 ? mFragments.size() : 0;
    }


    @Override
    public int getItemPosition(Object object) {
        return WebViewFragmentAdapter.POSITION_NONE;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }


}