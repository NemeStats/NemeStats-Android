package com.nemestats.boardgametracker.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.nemestats.boardgametracker.fragments.recordPlayedGame.ChooseDateFragment;
import com.nemestats.boardgametracker.fragments.recordPlayedGame.SelectGameFragment;
import com.nemestats.boardgametracker.fragments.recordPlayedGame.SelectPlayersFragment;
import com.nemestats.boardgametracker.fragments.recordPlayedGame.SetResultGameFragment;

/**
 * Created by geomehedeniuc on 3/13/18.
 */

public class RecordPlayedGameAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private ChooseDateFragment mChooseDateFragment;
    private SelectGameFragment mSelectGameFragment;
    private SelectPlayersFragment mSelectPlayersFragment;
    private SetResultGameFragment mSetResultRankedGameFragment;

    public static final int POSITION_CHOOSE_DATE = 0;
    public static final int POSITION_SELECT_GAME = 1;
    public static final int POSITION_SELECT_PLAYERS = 2;
    public static final int POSITION_SET_RESULTS = 3;

    public RecordPlayedGameAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case POSITION_CHOOSE_DATE:
                if (mChooseDateFragment == null) {
                    mChooseDateFragment = ChooseDateFragment.newInstance();
                }
                return mChooseDateFragment;
            case POSITION_SELECT_GAME:
                if (mSelectGameFragment == null) {
                    mSelectGameFragment = SelectGameFragment.newInstance();
                }
                return mSelectGameFragment;
            case POSITION_SELECT_PLAYERS:
                if (mSelectPlayersFragment == null) {
                    mSelectPlayersFragment = SelectPlayersFragment.newInstance();
                }
                return mSelectPlayersFragment;
            case POSITION_SET_RESULTS:
                if (mSetResultRankedGameFragment == null) {
                    mSetResultRankedGameFragment = SetResultGameFragment.newInstance();
                }
                return mSetResultRankedGameFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mChooseDateFragment.onPageSelected(position == POSITION_CHOOSE_DATE);
        mSelectGameFragment.onPageSelected(position == POSITION_SELECT_GAME);
        mSelectPlayersFragment.onPageSelected(position == POSITION_SELECT_PLAYERS);
        mSetResultRankedGameFragment.onPageSelected(position == POSITION_SET_RESULTS);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
