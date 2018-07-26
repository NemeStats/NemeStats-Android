package com.nemestats.boardgametracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.viewModels.CreatePlayerActivityViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * Created by geomehedeniuc on 4/6/18.
 */

@EActivity(R.layout.activity_edit_player)
public class EditPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_PLAYER = "extraPlayer";

    private MaterialDialog mPopupSavingPlayer;

    private CreatePlayerActivityViewModel.OnCreatePlayerListener mOnCreatePlayerListener;

    @ViewById(R.id.player_name)
    EditText mEdtPlayerName;

    @ViewById(R.id.player_email)
    EditText mEdtPlayerEmail;

    @ViewById(R.id.active_check_box)
    AppCompatCheckBox mActiveCheckBox;

    @Inject
    CreatePlayerActivityViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);
        if (getIntent().hasExtra(EXTRA_PLAYER)) {
            mViewModel.setPlayer((Player) getIntent().getSerializableExtra(EXTRA_PLAYER));
            mViewModel.setEditMode(true);
        } else {
            mViewModel.initializeNewPlayer();
            mViewModel.setEditMode(false);
        }
    }

    @AfterViews
    public void setupViews() {

        if (mViewModel.isEditMode()) {
            setupPlayerDetails();
        } else {
            mActiveCheckBox.setVisibility(View.GONE);
        }

        mOnCreatePlayerListener = new CreatePlayerActivityViewModel.OnCreatePlayerListener() {
            @Override
            public void onPlayerUpdatedSuccess() {
                if (mPopupSavingPlayer != null && mPopupSavingPlayer.isShowing()) {
                    mPopupSavingPlayer.dismiss();
                }
                Toast.makeText(EditPlayerActivity.this, R.string.player_updated, Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_PLAYER, mViewModel.getPlayer());
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onPlayerCreatedSuccess() {
                if (mPopupSavingPlayer != null && mPopupSavingPlayer.isShowing()) {
                    mPopupSavingPlayer.dismiss();
                }
                Toast.makeText(EditPlayerActivity.this, R.string.player_saved, Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_PLAYER, mViewModel.getPlayer());
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onUpdatePlayerFailed(String errorMessage) {
                if (mPopupSavingPlayer != null && mPopupSavingPlayer.isShowing()) {
                    mPopupSavingPlayer.dismiss();
                }

                new MaterialDialog.Builder(EditPlayerActivity.this)
                        .title(R.string.failed)
                        .content(errorMessage)
                        .cancelable(true)
                        .positiveText(R.string.txt_ok)
                        .build()
                        .show();
            }

            @Override
            public void onCreatePlayerFailed(String errorMessage) {

                if (mPopupSavingPlayer != null && mPopupSavingPlayer.isShowing()) {
                    mPopupSavingPlayer.dismiss();
                }

                new MaterialDialog.Builder(EditPlayerActivity.this)
                        .title(R.string.failed)
                        .content(errorMessage)
                        .cancelable(true)
                        .positiveText(R.string.txt_ok)
                        .build()
                        .show();
            }
        };

        mViewModel.setOnGameDetailsListener(mOnCreatePlayerListener);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.activity_create_player);
        }
    }

    private void setupPlayerDetails() {
        mActiveCheckBox.setVisibility(View.VISIBLE);
        mActiveCheckBox.setChecked(mViewModel.getPlayer().isActive());
        mEdtPlayerEmail.setVisibility(View.GONE);
        mEdtPlayerName.setText(mViewModel.getPlayer().getPlayerName());
        mEdtPlayerName.setSelection(mViewModel.getPlayer().getPlayerName().length());
        mEdtPlayerName.requestFocus();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_game_definition_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                if (mPopupSavingPlayer != null && mPopupSavingPlayer.isShowing()) {
                    mPopupSavingPlayer.dismiss();
                }
                if (mViewModel.isEditMode()) {
                    mPopupSavingPlayer = new MaterialDialog.Builder(this)
                            .content(R.string.please_wait_while_we_update_the_player)
                            .cancelable(false)
                            .progress(true, 0)
                            .progressIndeterminateStyle(true)
                            .show();
                    mViewModel.updatePlayer(mEdtPlayerName.getText().toString(), mActiveCheckBox.isChecked());
                } else {
                    mPopupSavingPlayer = new MaterialDialog.Builder(this)
                            .content(R.string.please_wait_while_we_save_the_player)
                            .cancelable(false)
                            .progress(true, 0)
                            .progressIndeterminateStyle(true)
                            .show();
                    mViewModel.createPlayer(mEdtPlayerName.getText().toString(), mEdtPlayerEmail.getText().toString());
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
