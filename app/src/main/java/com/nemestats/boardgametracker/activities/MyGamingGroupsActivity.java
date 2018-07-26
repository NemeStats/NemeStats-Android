package com.nemestats.boardgametracker.activities;


import android.view.MenuItem;

import com.nemestats.boardgametracker.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by geomehedeniuc on 5/10/18.
 */

@EActivity(R.layout.activity_my_gaming_groups)
public class MyGamingGroupsActivity extends SessionExpiredBaseActivity {

    @AfterViews
    public void setupViews() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_gaming_groups);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
