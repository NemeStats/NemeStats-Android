package com.nemestats.boardgametracker.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.adapters.NemeStatsMainViewPagerAdapter;
import com.nemestats.boardgametracker.broadcasts.DataSyncBroadcastReceiver;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.googleAnalytics.FirebaseAnalyticsLogger;
import com.nemestats.boardgametracker.googleAnalytics.action.AnalyticsAction;
import com.nemestats.boardgametracker.googleAnalytics.action.AnalyticsActionTrigger;
import com.nemestats.boardgametracker.googleAnalytics.category.AnalyticsCategory;
import com.nemestats.boardgametracker.googleAnalytics.category.AnalyticsCategoryDescription;
import com.nemestats.boardgametracker.googleAnalytics.category.AnalyticsCategoryType;
import com.nemestats.boardgametracker.googleAnalytics.label.AnalyticsLabel;
import com.nemestats.boardgametracker.interfaces.OnDataChangedListener;
import com.nemestats.boardgametracker.managers.RateReminderManager;
import com.nemestats.boardgametracker.utils.PicassoRoundedCornerTransformation;
import com.nemestats.boardgametracker.utils.RateReminderUtils;
import com.nemestats.boardgametracker.viewModels.NemeStatsMainActivityViewModel;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by mehegeo on 9/24/17.
 */

@EActivity(R.layout.activity_nemestats_main_activity)
public class NemeStatsMainActivity extends SessionExpiredBaseActivity implements OnDataChangedListener {

    @ViewById(R.id.view_pager)
    ViewPager mTabsViewPager;

    @ViewById(R.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @ViewById(R.id.dropdown_arrow)
    ImageView mImgArrow;

    @ViewById(R.id.title)
    TextView mTxtSelectedGamingGroup;

    @ViewById(R.id.txt_account_badge)
    TextView mTxtAccountBadge;

    @ViewById(R.id.txt_account_user_name)
    TextView mTxtAccountUserName;

    @ViewById(R.id.player_avatar)
    ImageView mPlayerAvatar;

    private boolean mIsExpanded;

    @Inject
    NemeStatsMainActivityViewModel mViewModel;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private NemeStatsMainViewPagerAdapter mNemeStatsMainViewPagerAdapter;

    private DataSyncBroadcastReceiver.OnDataSyncFinishedListener mOnDataSyncFinishedListener;
    private DataSyncBroadcastReceiver mDataSyncBroadcastReceiver;

    @Inject
    RateReminderManager mRateReminderManager;

    @Inject
    FirebaseAnalyticsLogger mFirebaseAnalyticsLogger;

    @Override
    public void onGameDefinitionUpdated(GameDefinition gameDefinition) {
        mNemeStatsMainViewPagerAdapter.onGameDefinitionUpdated(gameDefinition);
    }


    public void logReloadContentEvent(String location) {
        mFirebaseAnalyticsLogger.logEvent(
                new AnalyticsCategory(AnalyticsCategoryType.P, AnalyticsCategoryDescription.RELOAD_CONTENT),
                new AnalyticsAction(location, AnalyticsActionTrigger.SWIPE_GESTURE_MADE),
                new AnalyticsLabel(null)
        );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NemeStatsApplication.getAppGraph().inject(this);

        mOnNavigationItemSelectedListener = item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTabsViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    mTabsViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_user_account:
                    mTabsViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        };

        mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
                        break;
                    case 1:
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
                        break;
                    case 2:
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_user_account);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        registerDataSyncBroadcastReceiver();
    }

    private void registerDataSyncBroadcastReceiver() {
        mOnDataSyncFinishedListener = new DataSyncBroadcastReceiver.OnDataSyncFinishedListener() {
            @Override
            public void onDataSyncFinishedSuccess() {
                refreshFragmentsData(mViewModel.getSelectedGamingGroup());
                Toast.makeText(NemeStatsMainActivity.this, "Sync Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataSyncFinishedFail() {
                refreshFragmentsData(mViewModel.getSelectedGamingGroup());
                Toast.makeText(NemeStatsMainActivity.this, "Sync Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onForceUpdateData() {
                refreshFragmentsData(mViewModel.getSelectedGamingGroup());
            }
        };

        mDataSyncBroadcastReceiver = new DataSyncBroadcastReceiver(mOnDataSyncFinishedListener);

        IntentFilter cloudSyncIntents = new IntentFilter();
        cloudSyncIntents.addAction(DataSyncBroadcastReceiver.DATA_SYNC_FINISHED_FAIL);
        cloudSyncIntents.addAction(DataSyncBroadcastReceiver.DATA_SYNC_FINISHED_SUCCESS);
        cloudSyncIntents.addAction(DataSyncBroadcastReceiver.FORCE_UPDATE_DATA);
        LocalBroadcastManager.getInstance(this).registerReceiver(mDataSyncBroadcastReceiver, cloudSyncIntents);
    }


    private void unregisterDataSyncBroadcastReceiver() {
        if (mDataSyncBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataSyncBroadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.startDataSync();
    }

    @Click(R.id.btn_open_drawer)
    public void onBtnOpenDrawerClick() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Click(R.id.choose_gaming_group_button)
    public void onSelectGroupClick() {
        List<GamingGroup> gamingGroupList = mViewModel.getAllGamingGroups();

        new MaterialDialog.Builder(this)
                .title(R.string.choose_gaming_group)
                .items(gamingGroupList)
                .alwaysCallSingleChoiceCallback()
                .dismissListener(dialog -> {
                    toogleArrowIcon();
                })
                .itemsCallbackSingleChoice(gamingGroupList.indexOf(mViewModel.getSelectedGamingGroup()), (dialog, view, which, text) -> {
                    onGamingGroupSelected(mViewModel.getAllGamingGroups().get(which));
                    dialog.dismiss();
                    return true;
                })
                .positiveText(R.string.txt_ok)
                .show();

        toogleArrowIcon();
    }

    private void toogleArrowIcon() {
        if (mIsExpanded) {
            ViewCompat.animate(mImgArrow).rotation(0).start();
        } else {
            ViewCompat.animate(mImgArrow).rotation(180).start();
        }
        mIsExpanded = !mIsExpanded;
    }

    private void onGamingGroupSelected(GamingGroup gamingGroup) {
        mTxtSelectedGamingGroup.setText(gamingGroup.getGroupName());
        mViewModel.setSelectedGamingGroup(gamingGroup);
        refreshFragmentsData(mViewModel.getSelectedGamingGroup());
    }

    private void refreshFragmentsData(GamingGroup gamingGroup) {
        mNemeStatsMainViewPagerAdapter.refreshDisplayedData(gamingGroup);
    }

    @AfterViews
    public void setupViews() {
        setSupportActionBar(mToolbar);

        mNemeStatsMainViewPagerAdapter = new NemeStatsMainViewPagerAdapter(getSupportFragmentManager());
        mTabsViewPager.setOffscreenPageLimit(3);
        mTabsViewPager.setAdapter(mNemeStatsMainViewPagerAdapter);

        mTabsViewPager.setCurrentItem(1);
        mBottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);

        mTxtSelectedGamingGroup.setText(mViewModel.getSelectedGamingGroup().getGroupName());

        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mTabsViewPager.addOnPageChangeListener(mOnPageChangeListener);

        setupAccountUI();

        checkAndIncrementRateStatus();
        checkAndDisplayAvatar();
    }

    private void checkAndDisplayAvatar() {
        String avatarUrl = mViewModel.getAvatarForCurrentUser();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Picasso.with(this)
                    .load(avatarUrl)
                    .transform(new PicassoRoundedCornerTransformation(1f))
                    .placeholder(R.drawable.placeholder_image)
                    .into(mPlayerAvatar);
        }
    }

    private void setupAccountUI() {
        String accountUserName = mViewModel.getAccountUserName();
        if (accountUserName != null && !accountUserName.isEmpty()) {
            mTxtAccountUserName.setText(accountUserName);
            mTxtAccountBadge.setText(String.valueOf(accountUserName.charAt(0)));
        }
    }


    @Background
    public void checkAndIncrementRateStatus() {
        mRateReminderManager.incrementCountAppOpened();
        if (!mRateReminderManager.getAppRated()) {
            int appOpenedCount = mRateReminderManager.getAppOpenedCount();
            if (appOpenedCount % 40 == 0) {
                showRateDialog();
            }
        }
    }

    @UiThread
    public void showRateDialog() {
        RateReminderUtils.showRateDialog(this, mRateReminderManager);
    }


    @Click(R.id.btn_rate_us)
    public void onBtnRateUsClick() {
        RateReminderUtils.openPlayStore(this, mRateReminderManager);
    }

    @Click(R.id.btn_spread_the_love)
    public void onBtnSpreadTheLoveClick() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.invitation_message) + " http://play.google.com/store/apps/details?id=" + getPackageName());
        sendIntent.setType("text/plain");
        sendIntent.removeExtra("key");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.spread_the_love)));

    }

    @Click(R.id.btn_send_feedback)
    public void onBtnSendFeedbackClick() {
        mRateReminderManager.setAppRated(true);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_developer)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.nemestats_feedback));
        startActivity(Intent.createChooser(intent, getString(R.string.send_feedback)));
    }

    @Click(R.id.btn_bgg_credit)
    public void onBtnBGGCreditClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.board_game_geek_page_url)));
        startActivity(browserIntent);
    }

    @Click(R.id.btn_my_gaming_groups)
    public void onMyGamingGroupsClick() {
        Intent intent = new Intent(this, MyGamingGroupsActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.btn_logout)
    public void onBtnLogoutClick() {

        new MaterialDialog.Builder(this)
                .content(R.string.are_you_sure_you_want_to_logout)
                .positiveText(getString(R.string.yes))
                .negativeText(getString(R.string.no))
                .onPositive((dialog, which) -> {
                    mViewModel.logout();
                    finishActivityAndRedirectToLogin();
                })
                .build()
                .show();
    }

    @Click(R.id.btn_privacy_notice)
    public void onPrivacyNoticeClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.nemestats_privacy_notice_url)));
        startActivity(browserIntent);
    }

    @Click(R.id.btn_about_nemestats)
    public void onBtnAboutNemestatsClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.nemestats_about_url)));
        startActivity(browserIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterDataSyncBroadcastReceiver();
    }
}
