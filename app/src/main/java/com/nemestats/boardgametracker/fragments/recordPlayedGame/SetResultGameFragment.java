package com.nemestats.boardgametracker.fragments.recordPlayedGame;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.customViews.dialogs.PlayedGameEditNotesDialog;
import com.nemestats.boardgametracker.interfaces.OnCreatePlayedGameEventsListener;
import com.nemestats.boardgametracker.viewModels.RecordPlayedGameViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by geomehedeniuc on 3/13/18.
 */

@EFragment(R.layout.fragment_set_result_game)
public class SetResultGameFragment extends Fragment {

    @ViewById(R.id.tab_layout)
    TabLayout mTabLayout;

    @ViewById(R.id.btn_next)
    View mBtnNext;

    @ViewById(R.id.btn_edit_notes)
    View mBtnEditNote;

    private RecordPlayedGameViewModel mViewModel;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentFragment;
    private OnCreatePlayedGameEventsListener mOnCreatePlayedGameEventsListener;

    public static SetResultGameFragment newInstance() {
        return new SetResultGameFragment_();
    }

    public void onPageSelected(boolean isThisCurrentPage) {
        if (isThisCurrentPage) {
            hideKeyboard();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mCurrentFragment != null) {
            mCurrentFragment.setUserVisibleHint(isVisibleToUser);
        }
    }

    @Click(R.id.btn_previous)
    public void onBtnPreviousClick() {
        if (mOnCreatePlayedGameEventsListener != null) {
            mOnCreatePlayedGameEventsListener.onBtnPreviousClick();
        }
    }

    @Click(R.id.btn_edit_notes)
    public void onBtnEditNotesClick() {
        if (getActivity() != null) {
            MaterialDialog.Builder playedGameEditNotesDialog = new MaterialDialog.Builder(getActivity())
                    .customView(R.layout.layout_edit_notes, true)
                    .positiveText("Save");
            new PlayedGameEditNotesDialog(playedGameEditNotesDialog, mViewModel.getPlayedGame()).show();
        }
    }

    @AfterViews
    public void setupViews() {
        mBtnNext.setVisibility(View.GONE);
        mBtnEditNote.setVisibility(View.VISIBLE);
        showFragment(0);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        TabLayout.Tab firstTab = null;

        firstTab = mTabLayout.getTabAt(0);
        if (firstTab != null) {
            firstTab.select();
        }
    }

    private void showFragment(int position) {

        if (mFragmentManager == null) {
            mFragmentManager = getChildFragmentManager();
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        if (mCurrentFragment != null) {
            mCurTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            mCurTransaction.detach(mCurrentFragment);
        }

        String fragmentName = "";
        switch (position) {
            case 0:
                fragmentName = ResultRankedGameFragment.class.getSimpleName();
                break;
            case 1:
                fragmentName = ResultScoredGameFragment.class.getSimpleName();
                break;
            case 2:
                fragmentName = ResultCooperativeGameFragment.class.getSimpleName();
                break;
        }

        String fragmentTag = makeFragmentName(fragmentName, position);
        Fragment fragment = mFragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            mCurrentFragment = fragment;
            mCurTransaction.attach(fragment);
        } else {
            switch (position) {
                case 0:
                    mCurrentFragment = ResultRankedGameFragment.newInstance();
                    break;
                case 1:
                    mCurrentFragment = ResultScoredGameFragment.newInstance();
                    break;
                case 2:
                    mCurrentFragment = ResultCooperativeGameFragment.newInstance();
                    break;
            }
            fragment = mCurrentFragment;
            mCurTransaction.add(R.id.container_fragments_set_game_result, fragment, fragmentTag);
        }

        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    private static String makeFragmentName(String fragmentName, long id) {
        return "android:switcher:" + fragmentName + ":" + id;
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
