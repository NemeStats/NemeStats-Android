package com.nemestats.boardgametracker.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.nemestats.boardgametracker.fragments.CreateAccountFragment;
import com.nemestats.boardgametracker.fragments.LoginFragment;

/**
 * Created by mehegeo on 9/23/17.
 */

public class LoginCreateAccountViewPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private LoginFragment mLoginFragment;
    private CreateAccountFragment mCreateAccountFragment;

    public LoginCreateAccountViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mLoginFragment = LoginFragment.newInstance();
        mCreateAccountFragment = CreateAccountFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return mLoginFragment;
        } else if (position == 1) {
            return mCreateAccountFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
