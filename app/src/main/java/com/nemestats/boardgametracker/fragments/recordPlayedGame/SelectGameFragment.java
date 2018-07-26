package com.nemestats.boardgametracker.fragments.recordPlayedGame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.activities.SearchGameDefinitionActivity;
import com.nemestats.boardgametracker.activities.SearchGameDefinitionActivity_;
import com.nemestats.boardgametracker.adapters.GameDefinitionsAdapter;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.interfaces.OnCreatePlayedGameEventsListener;
import com.nemestats.boardgametracker.viewModels.RecordPlayedGameViewModel;
import com.nemestats.boardgametracker.viewModels.UIViewModel.GameDefinitionViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;


/**
 * Created by geomehedeniuc on 3/13/18.
 */

@EFragment(R.layout.fragment_select_game)
public class SelectGameFragment extends Fragment {

    private static final int REQUEST_CODE_CREATE_GAME_DEFINITION = 7291;
    @ViewById(R.id.game_definitions_list)
    RecyclerView mGameDefinitionsList;

    @ViewById(R.id.edt_search_games)
    EditText mEdtSearchGames;

    @ViewById(R.id.txt_empty_view)
    View mEmptyView;

    @ViewById(R.id.btn_next)
    View mBtnNext;

    @ViewById(R.id.btn_previous)
    View mBtnPrevious;

    private GameDefinitionsAdapter mGameDefinitionsAdapter;
    private GameDefinitionsAdapter.OnItemClickListener mOnItemClickListener;

    RecordPlayedGameViewModel mViewModel;
    private BehaviorSubject<String> mEdtSearchGamesBehaviorSubject;

    private OnCreatePlayedGameEventsListener mOnCreatePlayedGameEventsListener;

    public static SelectGameFragment newInstance() {
        return new SelectGameFragment_();
    }

    public void onPageSelected(boolean isThisCurrentPage) {
        if (isThisCurrentPage) {
            hideKeyboard();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = RecordPlayedGameViewModel.getInstance();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCreatePlayedGameEventsListener) {
            mOnCreatePlayedGameEventsListener = (OnCreatePlayedGameEventsListener) context;
        }
    }


    @Click(R.id.btn_previous)
    public void onBtnPreviousClick() {
        if (mOnCreatePlayedGameEventsListener != null) {
            mOnCreatePlayedGameEventsListener.onBtnPreviousClick();
        }
    }

    @Click(R.id.btn_next)
    public void onBtnNextClick() {
        if (mOnCreatePlayedGameEventsListener != null) {
            mOnCreatePlayedGameEventsListener.onBtnNextClick();
        }
    }

    @Click(R.id.btn_create_game_definition)
    public void onBtnCreateGameDefinition() {
        Intent intent = new Intent(getActivity(), SearchGameDefinitionActivity_.class);
        startActivityForResult(intent, REQUEST_CODE_CREATE_GAME_DEFINITION);
    }

    private void updateGameList(boolean shouldScrollToPosition) {
        mGameDefinitionsAdapter.notifyDataSetChanged();
        if (shouldScrollToPosition) {
            int index = mViewModel.getIndexOfSelectedGame();
            if (index != -1) {
                mGameDefinitionsList.smoothScrollToPosition(index);
            }
        }
    }


    @AfterViews
    public void setupViews() {


        mEdtSearchGames.setCompoundDrawablesRelativeWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_search), null, null, null);

        mEdtSearchGamesBehaviorSubject = BehaviorSubject.create();

        mEdtSearchGamesBehaviorSubject
                .debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->
                        mViewModel.searchLocalGames(String.valueOf(s))
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DisposableCompletableObserver() {
                                    @Override
                                    public void onComplete() {
                                        updateGamesList();
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }
                                }));

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


        mOnItemClickListener = gameDefinitionViewModel -> {
            mViewModel.setSelectedGameDefinitionViewModel(gameDefinitionViewModel);
            updateGameList(false);
            goToNextPage();
        };

        mGameDefinitionsAdapter = new GameDefinitionsAdapter(getActivity(), mViewModel.getGameDefinitionListSortedByPlayedGames(), mOnItemClickListener);
        mGameDefinitionsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGameDefinitionsList.setAdapter(mGameDefinitionsAdapter);
        mGameDefinitionsList.setItemAnimator(new DefaultItemAnimator());
        mGameDefinitionsList.setHasFixedSize(true);
        updateGamesList();
    }

    public void updateGamesList() {
        if (mViewModel.getGameDefinitionListSortedByPlayedGames().isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        mGameDefinitionsAdapter.notifyDataSetChanged();
    }

    @Background(delay = 300)
    public void goToNextPage() {
        getActivity().runOnUiThread(this::onBtnNextClick);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_GAME_DEFINITION) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    GameDefinition extraGameDefinition = (GameDefinition) data.getSerializableExtra(SearchGameDefinitionActivity.EXTRA_GAME_DEFINITION);
                    GameDefinitionViewModel gameDefinitionViewModel = new GameDefinitionViewModel(extraGameDefinition);
                    gameDefinitionViewModel.setNumberOfPlayedGames(0);
                    mViewModel.addCreatedGameDefiniton(gameDefinitionViewModel);
                    mViewModel.setSelectedGameDefinitionViewModel(gameDefinitionViewModel);
                    updateGameList(true);
                    goToNextPage();
                }
            }
        }
    }

    public void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && this.getActivity().getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

}
