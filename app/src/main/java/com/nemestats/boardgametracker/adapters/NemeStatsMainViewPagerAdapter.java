package com.nemestats.boardgametracker.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.fragments.GameDefinitionsFragment;
import com.nemestats.boardgametracker.fragments.PlayedGamesFragment;
import com.nemestats.boardgametracker.fragments.GamingGroupsFragment;
import com.nemestats.boardgametracker.fragments.PlayersInGamingGroupFragment;

/**
 * Created by mehegeo on 9/24/17.
 */

public class NemeStatsMainViewPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    GameDefinitionsFragment mGameDefinitionsFragment;
    PlayedGamesFragment mPlayedGamesFragment;
    PlayersInGamingGroupFragment mPlayersInGamingGroupFragment;

    public NemeStatsMainViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mGameDefinitionsFragment = GameDefinitionsFragment.newInstance();
        mPlayedGamesFragment = PlayedGamesFragment.newInstance();
        mPlayersInGamingGroupFragment = PlayersInGamingGroupFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return mGameDefinitionsFragment;
        }
        if (position == 1) {
            return mPlayedGamesFragment;
        }

        return mPlayersInGamingGroupFragment;
    }


    public void refreshDisplayedData(GamingGroup gamingGroup) {
        mGameDefinitionsFragment.refreshDisplayedData(gamingGroup);
        mPlayedGamesFragment.refreshDisplayedData(gamingGroup);
        mPlayersInGamingGroupFragment.refreshDisplayedData(gamingGroup);
    }

    public void onGameDefinitionUpdated(GameDefinition gameDefinition) {
        mGameDefinitionsFragment.onGameDefinitionUpdated(gameDefinition);
        mPlayedGamesFragment.onGameDefinitionUpdated(gameDefinition);
        mPlayersInGamingGroupFragment.onGameDefinitionUpdated(gameDefinition);
    }

    @Override
    public int getCount() {
        return 3;
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

    public void refreshFragmentsOnPlayerUpdate(GamingGroup selectedGamingGroup) {
        mPlayedGamesFragment.refreshDisplayedData(selectedGamingGroup);
    }
}
