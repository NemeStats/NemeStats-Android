package com.nemestats.boardgametracker.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.adapters.LoginCreateAccountViewPagerAdapter;
import com.nemestats.boardgametracker.customViews.FadeInOutViewPagerTransformation;
import com.nemestats.boardgametracker.customViews.NonSwipeableViewPager;
import com.nemestats.boardgametracker.fragments.CreateAccountFragment;
import com.nemestats.boardgametracker.fragments.LoginFragment;
import com.nemestats.boardgametracker.googleAnalytics.FirebaseAnalyticsLogger;
import com.nemestats.boardgametracker.googleAnalytics.action.AnalyticsAction;
import com.nemestats.boardgametracker.googleAnalytics.action.AnalyticsActionLocation;
import com.nemestats.boardgametracker.googleAnalytics.action.AnalyticsActionTrigger;
import com.nemestats.boardgametracker.googleAnalytics.category.AnalyticsCategory;
import com.nemestats.boardgametracker.googleAnalytics.category.AnalyticsCategoryDescription;
import com.nemestats.boardgametracker.googleAnalytics.category.AnalyticsCategoryType;
import com.nemestats.boardgametracker.googleAnalytics.label.AnalyticsLabel;
import com.nemestats.boardgametracker.googleAnalytics.label.AnalyticsLabelConstants;
import com.nemestats.boardgametracker.managers.AccountManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.inject.Inject;

/**
 * Created by mehegeo on 9/23/17.
 */

@EActivity(R.layout.main_activity)
public class MainActivity extends AppCompatActivity implements LoginFragment.OnBtnCreateAccountClickListener, CreateAccountFragment.OnBtnReturnToLoginScreenClickListener {

    @ViewById(R.id.view_pager)
    NonSwipeableViewPager mViewPager;

    @Inject
    FirebaseAnalyticsLogger mFirebaseAnalyticsLogger;

    @Inject
    AccountManager mAccountManager;

    private LoginCreateAccountViewPagerAdapter mViewPagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);
    }

    @AfterViews
    public void setupViews() {
        mViewPagerAdapter = new LoginCreateAccountViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setPageTransformer(false, new FadeInOutViewPagerTransformation());
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    public void logEventSuccessfullySignedIn(String location) {
        mFirebaseAnalyticsLogger.logEvent(
                new AnalyticsCategory(AnalyticsCategoryType.P, AnalyticsCategoryDescription.SIGN_IN),
                new AnalyticsAction(location, AnalyticsActionTrigger.SIGN_IN),
                new AnalyticsLabel(null)
        );
    }

    @Override
    public void onBtnCreateAccountClick() {
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void onBtnReturnToLoginScreenClick() {
        mViewPager.setCurrentItem(0);
    }
}
