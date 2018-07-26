package com.nemestats.boardgametracker.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.utils.PicassoRoundedCornerTransformation;
import com.nemestats.boardgametracker.viewModels.PlayerDetailsActivityViewModel;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * Created by geomehedeniuc on 5/19/18.
 */

@EActivity(R.layout.activity_player_details)
public class PlayerDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_PLAYER = "extraPlayer";
    public static final int REQUEST_CODE_EDIT_PLAYER = 1529;

    @ViewById(R.id.txt_account_badge)
    TextView mTxtAccountBadge;

    @ViewById(R.id.txt_player_name)
    TextView mTxtPlayerName;

    @ViewById(R.id.player_avatar)
    ImageView mPlayerAvatar;


    @Inject
    PlayerDetailsActivityViewModel mViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);
        if (getIntent().hasExtra(EXTRA_PLAYER)) {
            mViewModel.setPlayer((Player) getIntent().getSerializableExtra(EXTRA_PLAYER));
        }
    }

    @AfterViews
    public void setupViews() {
        String avatarUrl = mViewModel.getPlayer().getRemoteAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Picasso.with(this)
                    .load(avatarUrl)
                    .transform(new PicassoRoundedCornerTransformation(1f))
                    .placeholder(R.drawable.placeholder_image)
                    .into(mPlayerAvatar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.activity_player_details);
        }

        if (mViewModel.getPlayer() != null) {
            mTxtAccountBadge.setText(String.valueOf(mViewModel.getPlayer().getPlayerName().charAt(0)));
            mTxtPlayerName.setText(mViewModel.getPlayer().getPlayerName());
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(this, EditPlayerActivity_.class);
                intent.putExtra(EditPlayerActivity.EXTRA_PLAYER, mViewModel.getPlayer());
                startActivityForResult(intent, REQUEST_CODE_EDIT_PLAYER);
                return true;
            case R.id.action_view_on_nemestats_dot_com:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mViewModel.getPlayer().getNemestatsUrl()));
                startActivity(browserIntent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.player_details_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mViewModel.isPlayerEdited()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_PLAYER, mViewModel.getPlayer());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_PLAYER) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Player player = (Player) data.getSerializableExtra(EditPlayerActivity.EXTRA_PLAYER);
                    mViewModel.setPlayer(player);
                    mViewModel.setPlayerEdited(true);
                    setupViews();
                }
            }
        }
    }

}
