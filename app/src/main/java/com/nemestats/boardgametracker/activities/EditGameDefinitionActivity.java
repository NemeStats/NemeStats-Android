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
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.viewModels.EditGameDefinitionActivityViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * Created by geomehedeniuc on 5/20/18.
 */

@EActivity(R.layout.activity_edit_game_definition)
public class EditGameDefinitionActivity extends AppCompatActivity {


    public static final String EXTRA_GAME_DEFINITION = "extraGameDefinition";

    @ViewById(R.id.edt_game_name)
    EditText mEdtGameName;

    @ViewById(R.id.active_check_box)
    AppCompatCheckBox mActiveCheckBox;

    private MaterialDialog mPopupSavingGameDefinition;

    @Inject
    EditGameDefinitionActivityViewModel mViewModel;

    private EditGameDefinitionActivityViewModel.OnGameDetailsListener mOnGameDetailsListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);
        if (getIntent().hasExtra(EXTRA_GAME_DEFINITION)) {
            mViewModel.setGameDefinition((GameDefinition) getIntent().getSerializableExtra(EXTRA_GAME_DEFINITION));
            mViewModel.setInEditMode(mViewModel.getGameDefinition().getServerId() != 0);
        }
    }

    @AfterViews
    public void setupViews() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (mViewModel.isInEditMode()) {
                getSupportActionBar().setTitle(R.string.activity_edit_game_definition);
            } else {
                getSupportActionBar().setTitle(R.string.activity_create_game);
            }
        }

        if (mViewModel.isInEditMode()) {
            mActiveCheckBox.setVisibility(View.VISIBLE);
        } else {
            mActiveCheckBox.setVisibility(View.GONE);
        }

        mOnGameDetailsListener = new EditGameDefinitionActivityViewModel.OnGameDetailsListener() {
            @Override
            public void onCreateGameDefinitionSuccess() {
                mPopupSavingGameDefinition.dismiss();
                Toast.makeText(EditGameDefinitionActivity.this, R.string.game_saved, Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_GAME_DEFINITION, mViewModel.getGameDefinition());
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onCreateGameDefinitionFailed(String errorMessage) {
                mPopupSavingGameDefinition.dismiss();
                new MaterialDialog.Builder(EditGameDefinitionActivity.this)
                        .title(R.string.failed)
                        .content(errorMessage)
                        .cancelable(true)
                        .positiveText(R.string.txt_ok)
                        .build()
                        .show();
            }
        };
        mViewModel.setOnGameDetailsListener(mOnGameDetailsListener);

        GameDefinition gameDefinition = mViewModel.getGameDefinition();

        if (gameDefinition != null) {
            mEdtGameName.setText(gameDefinition.getGameDefinitionName());
            mEdtGameName.setSelection(gameDefinition.getGameDefinitionName().length());

            if (gameDefinition.isActive()) {
                mActiveCheckBox.setChecked(true);
            } else {
                mActiveCheckBox.setChecked(false);
            }
        }

    }

    @Override
    public void onBackPressed() {
        mViewModel.deleteSelectedGameDefinitionIfNoServerId();
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                saveGameDefinition();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private void saveGameDefinition() {
        mViewModel.getGameDefinition().setGameDefinitionName(String.valueOf(mEdtGameName.getText()));

        if (mViewModel.isInEditMode()) {
            mViewModel.getGameDefinition().setActive(mActiveCheckBox.isChecked());
        } else {
            mViewModel.getGameDefinition().setActive(true);
        }

        if (mPopupSavingGameDefinition != null && mPopupSavingGameDefinition.isShowing()) {
            mPopupSavingGameDefinition.dismiss();
        }

        mPopupSavingGameDefinition = new MaterialDialog.Builder(this)
                .content(R.string.please_wait_while_we_save_the_game)
                .cancelable(false)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        mViewModel.saveGameDefinition();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_game_definition_menu, menu);
        menu.findItem(R.id.action_save).getIcon().setAlpha(255);
        return true;
    }

}
