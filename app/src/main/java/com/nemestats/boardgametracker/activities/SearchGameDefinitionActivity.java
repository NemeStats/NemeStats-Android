package com.nemestats.boardgametracker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.adapters.RemoteGamesSearchResults;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.viewModels.SearchGameDefinitionActivityViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by geomehedeniuc on 3/24/18.
 */

@EActivity(R.layout.activity_search_game_definition)
public class SearchGameDefinitionActivity extends SessionExpiredBaseActivity {

    public static final String EXTRA_GAME_DEFINITION = "ExtraGameDefinition";
    private static final int REQUEST_CODE_EDIT_GAME_DEFINITION = 2951;

    @ViewById(R.id.autocomplete_search_games)
    EditText mEdtSearchGames;

    @ViewById(R.id.search_results_list)
    RecyclerView mSearchResultsList;

    @ViewById(R.id.progress_bar)
    ProgressBar mProgressBar;

    @ViewById(R.id.empty_view)
    View mEmptyView;

    @Inject
    SearchGameDefinitionActivityViewModel mViewModel;

    private BehaviorSubject<String> mEdtSearchGamesBehaviorSubject;

    RemoteGamesSearchResults mRemoteGamesSearchResults;
    private RemoteGamesSearchResults.OnItemClickListener mOnItemClickListener;
    private SearchGameDefinitionActivityViewModel.OnGameDetailsListener mOnGameDetailsListener;

    private MaterialDialog mPopupGameDetailsProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);
    }

    @AfterViews
    public void setupViews() {
        mEdtSearchGames.setCompoundDrawablesRelativeWithIntrinsicBounds(AppCompatResources.getDrawable(this, R.drawable.ic_search), null, null, null);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.activity_search_games);
        }
        mOnItemClickListener = searchResultBoardGame -> {
            mViewModel.setSelectedGame(searchResultBoardGame);
            mViewModel.requestGameDetails();
        };

        mOnGameDetailsListener = new SearchGameDefinitionActivityViewModel.OnGameDetailsListener() {
            @Override
            public void onGameDetailsRetrieveStarted() {
                mPopupGameDetailsProgressBar = new MaterialDialog.Builder(SearchGameDefinitionActivity.this)
                        .content(R.string.getting_game_details)
                        .cancelable(false)
                        .progress(true, 0)
                        .progressIndeterminateStyle(true)
                        .show();

            }

            @Override
            public void onGameDetailsRetrievedSuccess() {
                if (mPopupGameDetailsProgressBar != null && mPopupGameDetailsProgressBar.isShowing()) {
                    mPopupGameDetailsProgressBar.dismiss();
                }

                startEditGameDefinitionActivity();
            }

            @Override
            public void onGameDetailsRetrievedFailed() {
                if (mPopupGameDetailsProgressBar != null && mPopupGameDetailsProgressBar.isShowing()) {
                    mPopupGameDetailsProgressBar.dismiss();
                }
            }
        };

        mViewModel.setOnGameDetailsListener(mOnGameDetailsListener);

        mRemoteGamesSearchResults = new RemoteGamesSearchResults(this, mViewModel.getSearchResultList(), mOnItemClickListener);
        mSearchResultsList.setAdapter(mRemoteGamesSearchResults);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsList.setItemAnimator(new DefaultItemAnimator());
        mSearchResultsList.setHasFixedSize(true);
        mEdtSearchGamesBehaviorSubject = BehaviorSubject.create();

        mEdtSearchGamesBehaviorSubject
                .debounce(400, TimeUnit.MILLISECONDS)
                .filter(s -> s.length() >= 3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    onSearchStarted();
                    mViewModel.searchForGames(String.valueOf(s))
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DisposableCompletableObserver() {
                                @Override
                                public void onComplete() {
                                    onSearchEnded();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    onSearchEnded();
                                }
                            });
                });

        mEdtSearchGames.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                mEdtSearchGamesBehaviorSubject.onNext(String.valueOf(text));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void startEditGameDefinitionActivity() {
        GameDefinition gameDefinition = mViewModel.getSelectedGameDefinition();
        Intent intent = new Intent(this, EditGameDefinitionActivity_.class);
        intent.putExtra(EditGameDefinitionActivity.EXTRA_GAME_DEFINITION, gameDefinition);
        startActivityForResult(intent, REQUEST_CODE_EDIT_GAME_DEFINITION);
    }


    @UiThread
    public void onSearchStarted() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRemoteGamesSearchResults.notifyDataSetChanged();
    }

    @UiThread
    public void onSearchEnded() {
        if (mViewModel.getSearchResultList().isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        mProgressBar.setVisibility(View.GONE);
        mRemoteGamesSearchResults.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_GAME_DEFINITION) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    GameDefinition gameDefinition = (GameDefinition) data.getSerializableExtra(EditGameDefinitionActivity.EXTRA_GAME_DEFINITION);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_GAME_DEFINITION, gameDefinition);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
