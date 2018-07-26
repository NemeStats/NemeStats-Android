package com.nemestats.boardgametracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.adapters.RecordPlayedGameAdapter;
import com.nemestats.boardgametracker.customViews.dialogs.PlayedGamePreviewDialog;
import com.nemestats.boardgametracker.interfaces.OnCreatePlayedGameEventsListener;
import com.nemestats.boardgametracker.viewModels.RecordPlayedGameViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

/**
 * Created by geomehedeniuc on 3/12/18.
 */

@EActivity(R.layout.activity_record_played_game)
public class RecordPlayedGameActivity extends SessionExpiredBaseActivity implements OnCreatePlayedGameEventsListener {

    private static final String CHECK_GAME_RESULT_VALIDITY_TASK = "checkGameResultValidity";
    public static final String EXTRA_PLAYED_GAME = "extraPlayedGame";

    @ViewById(R.id.record_game_steps_view_pager)
    ViewPager mRecordGameStepsViewPager;

    private MaterialDialog mPopupSavePlayedGame;

    RecordPlayedGameViewModel mViewModel;

    private RecordPlayedGameAdapter mRecordPlayedGameAdapter;
    private Menu mMenu;

    private RecordPlayedGameViewModel.OnSaveGameListener mOnSaveGameListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = RecordPlayedGameViewModel.getInstance();
        mViewModel.initializeNewGame();
    }

    @AfterViews
    public void setupViews() {

        mOnSaveGameListener = new RecordPlayedGameViewModel.OnSaveGameListener() {
            @Override
            public void onSaveGameStarted() {

                runOnUiThread(() -> {
                    if (mPopupSavePlayedGame != null && mPopupSavePlayedGame.isShowing()) {
                        mPopupSavePlayedGame.dismiss();
                        mPopupSavePlayedGame = null;
                    }

                    mPopupSavePlayedGame = new MaterialDialog.Builder(RecordPlayedGameActivity.this)
                            .content(R.string.saving_played_game)
                            .cancelable(false)
                            .progress(true, 0)
                            .progressIndeterminateStyle(true)
                            .show();
                });
            }

            @Override
            public void onSaveGameSuccess() {
                mPopupSavePlayedGame.dismiss();
                Toast.makeText(RecordPlayedGameActivity.this, R.string.played_game_saved, Toast.LENGTH_LONG).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_PLAYED_GAME, mViewModel.getPlayedGame());
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onSaveGameFailed(String errorMessage) {
                mPopupSavePlayedGame.dismiss();

                new MaterialDialog.Builder(RecordPlayedGameActivity.this)
                        .title(R.string.failed)
                        .content(errorMessage)
                        .positiveText(R.string.txt_ok)
                        .show();
            }
        };

        mViewModel.setOnSaveGameListener(mOnSaveGameListener);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear);
            getSupportActionBar().setTitle(String.format(getString(R.string.activity_record_played_game_title), 0));
        }

        setupActivitySubtitle(0);

        mRecordPlayedGameAdapter = new RecordPlayedGameAdapter(getSupportFragmentManager());
        mRecordGameStepsViewPager.setAdapter(mRecordPlayedGameAdapter);

        int viewPagerPadding = (int) getResources().getDimension(R.dimen.padding_record_game_view_pager);
        mRecordGameStepsViewPager.setPadding(viewPagerPadding, viewPagerPadding / 2, viewPagerPadding, viewPagerPadding / 2);
        mRecordGameStepsViewPager.setClipToPadding(false);
        mRecordGameStepsViewPager.setPageMargin(viewPagerPadding / 2);
        mRecordGameStepsViewPager.setOffscreenPageLimit(5);
        mRecordGameStepsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setupActivitySubtitle(position);
                mRecordPlayedGameAdapter.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.record_played_game_menu, menu);
        mMenu = menu;
        checkGameResultValidity();
        return true;
    }


    @Background(id = CHECK_GAME_RESULT_VALIDITY_TASK)
    public void checkGameResultValidity() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            int progress = mViewModel.getPlayedGameInsertedDetailsProgress();
            updateActivityTitleAndMenuItems(progress);
        }
    }

    @UiThread
    public void updateActivityTitleAndMenuItems(int progress) {
        boolean valid = progress == 100;
        getSupportActionBar().setTitle(String.format(getString(R.string.activity_record_played_game_title), progress));
        mMenu.findItem(R.id.action_preview_game).setVisible(valid);
        mMenu.findItem(R.id.action_preview_game).setEnabled(valid);

        if (valid) {
            mMenu.findItem(R.id.action_save).getIcon().setAlpha(255);
        } else {
            mMenu.findItem(R.id.action_save).getIcon().setAlpha(100);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                if (mViewModel.getPlayedGameInsertedDetailsProgress() == 100) {
                    mViewModel.saveGame();
                } else {
                    String missingInformation = mViewModel.getMissingInformation();

                    new MaterialDialog.Builder(this)
                            .title(R.string.failed)
                            .content(missingInformation)
                            .positiveText(R.string.txt_ok)
                            .build()
                            .show();
                }
                return true;
            case R.id.action_preview_game:
                mViewModel.initPlayerGameResults();
                MaterialDialog.Builder playedGamePreview = new MaterialDialog.Builder(this).customView(R.layout.layout_played_game_summary, true);
                new PlayedGamePreviewDialog(playedGamePreview, mViewModel.getPlayedGame(), mViewModel.getSelectedGameDefinitionViewModel().getGameDefinition()).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (mViewModel.getPlayedGameInsertedDetailsProgress() > 25) {
            new MaterialDialog.Builder(this)
                    .title(R.string.exit)
                    .content(R.string.are_you_sure_you_want_to_discard_your_changes)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive((dialog, which) -> finish())
                    .build()
                    .show();
        } else {
            finish();
        }
    }

    private void setupActivitySubtitle(int pagePosition) {
        if (getSupportActionBar() != null) {
            if (pagePosition == RecordPlayedGameAdapter.POSITION_CHOOSE_DATE) {
                getSupportActionBar().setSubtitle(getString(R.string.step_1_choose_date));
            } else if (pagePosition == RecordPlayedGameAdapter.POSITION_SELECT_GAME) {
                getSupportActionBar().setSubtitle(getString(R.string.step_2_select_game));
            } else if (pagePosition == RecordPlayedGameAdapter.POSITION_SELECT_PLAYERS) {
                getSupportActionBar().setSubtitle(getString(R.string.step_3_select_players));
            } else if (pagePosition == RecordPlayedGameAdapter.POSITION_SET_RESULTS) {
                getSupportActionBar().setSubtitle(getString(R.string.step_4_set_results));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BackgroundExecutor.cancelAll(CHECK_GAME_RESULT_VALIDITY_TASK, true);
        mViewModel.destroyInstance();
    }

    @Override
    public void onBtnNextClick() {
        mRecordGameStepsViewPager.setCurrentItem(mRecordGameStepsViewPager.getCurrentItem() + 1);
    }

    @Override
    public void onBtnPreviousClick() {
        mRecordGameStepsViewPager.setCurrentItem(mRecordGameStepsViewPager.getCurrentItem() - 1);
    }
}
