package com.nemestats.boardgametracker.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.broadcasts.DataSyncBroadcastReceiver;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.utils.PicassoRoundedCornerTransformation;
import com.nemestats.boardgametracker.viewModels.GameDefinitionDetailsActivityViewModel;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import javax.inject.Inject;

/**
 * Created by mehegeo on 10/11/17.
 */

@EActivity(R.layout.activity_game_definition_details)
public class GameDefinitionDetailsActivity extends SessionExpiredBaseActivity {

    public static final String EXTRA_GAME_DEFINITION_SERVER_ID = "extraGameDefinitionId";
    public static final String EXTRA_GAME_DEFINITION_LOCAL_ID = "extraGameDefinitionLocalId";
    private static final int REQUEST_CODE_EDIT_GAME_DEFINITION = 5299;

    @ViewById(R.id.img_thumbnail_game_definition)
    ImageView mImgGameDefinition;

    @ViewById(R.id.txt_game_definition_name)
    TextView mTxtGameDefinitionName;

    @ViewById(R.id.txt_number_players)
    TextView mTxtNumberOfPlayers;

    @ViewById(R.id.txt_average_play_time)
    TextView mTxtAveragePlayTime;

    @ViewById(R.id.txt_year_published)
    TextView mTxtYearPublished;

    @ViewById(R.id.txt_categories)
    TextView mTxtCategories;

    @ViewById(R.id.txt_mechanics)
    TextView mTxtMechanics;

    @ViewById(R.id.webview_game_description)
    WebView mWebViewGameDescription;

    @Inject
    GameDefinitionDetailsActivityViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);
        if (getIntent().hasExtra(EXTRA_GAME_DEFINITION_SERVER_ID)) {
            mViewModel.getGameDefinitionByServerId(getIntent().getIntExtra(EXTRA_GAME_DEFINITION_SERVER_ID, -1));
        } else if (getIntent().hasExtra(EXTRA_GAME_DEFINITION_LOCAL_ID)) {
            mViewModel.getGameDefinitionByLocalId(getIntent().getIntExtra(EXTRA_GAME_DEFINITION_LOCAL_ID, -1));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_details_menu, menu);
        return true;
    }

    @AfterViews
    public void setupViews() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_game_info_title);


        if (mViewModel.getGameDefinition().getThumbnailLocalPath() != null) {
            Picasso.with(this)
                    .load(new File(mViewModel.getGameDefinition().getThumbnailLocalPath()))
                    .transform(new PicassoRoundedCornerTransformation())
                    .placeholder(R.drawable.placeholder_image)
                    .into(mImgGameDefinition);
        } else {
            Picasso.with(this)
                    .load(R.drawable.placeholder_image)
                    .transform(new PicassoRoundedCornerTransformation())
                    .into(mImgGameDefinition);
        }

        setupPlayers();
        setupAveragePlayTime();
        setupYearPublished();
        setupCategories();
        setupMechanics();

        mTxtGameDefinitionName.setText(mViewModel.getGameDefinition().getGameDefinitionName());

        mWebViewGameDescription.loadDataWithBaseURL("", mViewModel.getGameDefinition().getDescription(), "text/html", "UTF-8", "");
    }

    private void setupMechanics() {
        if (mViewModel.getGameDefinition().getMechanics() != null) {
            mTxtMechanics.setText(mViewModel.getGameDefinition().getMechanics());
        } else {
            ((View) mTxtMechanics.getParent()).setVisibility(View.GONE);
        }
    }

    private void setupCategories() {
        if (mViewModel.getGameDefinition().getCategories() != null) {
            mTxtCategories.setText(mViewModel.getGameDefinition().getCategories());
        } else {
            ((View) mTxtCategories.getParent()).setVisibility(View.GONE);
        }
    }

    private void setupYearPublished() {
        String yearPublished = mViewModel.getGameDefinition().getYearPublished();
        if (yearPublished != null) {
            mTxtYearPublished.setText(getString(R.string.txt_published_in) + " " + yearPublished);
        } else {
            ((View) mTxtYearPublished.getParent()).setVisibility(View.GONE);
        }
    }

    private void setupAveragePlayTime() {
        float min = mViewModel.getGameDefinition().getMinPlayTime();
        float max = mViewModel.getGameDefinition().getMaxPlayTime();
        float average = (max + min) / 2f;
        if (average != 0.0f) {
            mTxtAveragePlayTime.setText(getString(R.string.txt_average) + " " + (int) average + " " + getString(R.string.txt_minutes));
        } else {
            ((View) mTxtAveragePlayTime.getParent()).setVisibility(View.GONE);
        }
    }

    private void setupPlayers() {
        int min = mViewModel.getGameDefinition().getMinPlayers();
        int max = mViewModel.getGameDefinition().getMaxPlayers();

        if (min != 0 && max != 0) {
            mTxtNumberOfPlayers.setText(min + " - " + max + " " + getString(R.string.txt_players));
        } else if (min != 0) {
            mTxtNumberOfPlayers.setText(min + " " + getString(R.string.txt_players));
        } else if (max != 0) {
            mTxtNumberOfPlayers.setText(max + " " + getString(R.string.txt_players));
        } else {
            ((View) mTxtNumberOfPlayers.getParent()).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, EditGameDefinitionActivity_.class);
            intent.putExtra(EditGameDefinitionActivity.EXTRA_GAME_DEFINITION, mViewModel.getGameDefinition());
            startActivityForResult(intent, REQUEST_CODE_EDIT_GAME_DEFINITION);
        }
        if (item.getItemId() == R.id.action_view_on_nemestats_dot_com) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mViewModel.getGameDefinition().getNemestatsUrl()));
            startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mViewModel.isGameDefinitionEdited()) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_GAME_DEFINITION) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    GameDefinition gameDefinition = (GameDefinition) data.getSerializableExtra(EditGameDefinitionActivity.EXTRA_GAME_DEFINITION);
                    mViewModel.setGameDefinition(gameDefinition);
                    mViewModel.setGameDefinitionEdited(true);
                    setupViews();
                }
            }
        }
    }
}
