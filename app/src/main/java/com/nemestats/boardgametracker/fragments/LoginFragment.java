package com.nemestats.boardgametracker.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.activities.MainActivity;
import com.nemestats.boardgametracker.activities.NemeStatsMainActivity_;
import com.nemestats.boardgametracker.googleAnalytics.action.AnalyticsActionLocation;
import com.nemestats.boardgametracker.viewModels.LoginFragmentViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * Created by mehegeo on 9/23/17.
 */

@EFragment(R.layout.fragment_login)
public class LoginFragment extends Fragment {

    private static final String TAG = LoginFragment.class.getSimpleName();

    private OnBtnCreateAccountClickListener mOnBtnCreateAccountClickListener;

    @ViewById(R.id.edt_user_name)
    EditText mEdtUserName;

    @ViewById(R.id.edt_password)
    EditText mEdtPassword;

    @Inject
    LoginFragmentViewModel mViewModel;

    private MaterialDialog mProgressBar;

    private LoginFragmentViewModel.OnLoginListener mOnLoginListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);

        openAppIfUserIsLoggedIn();

        mOnLoginListener = new LoginFragmentViewModel.OnLoginListener() {
            @Override
            public void onLoginStarted() {
                if (mProgressBar != null && mProgressBar.isShowing()) {
                    mProgressBar.dismiss();
                    mProgressBar = null;
                }

                mProgressBar = new MaterialDialog.Builder(getActivity())
                        .content("Please wait...")
                        .cancelable(false)
                        .progress(true, 0)
                        .progressIndeterminateStyle(true)
                        .show();
            }

            @Override
            public void onLoginSuccess() {

            }

            @Override
            public void onLoginFailed(String errorMessage) {
                mProgressBar.dismiss();
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.txt_error)
                        .content(errorMessage)
                        .positiveText(getString(R.string.txt_ok))
                        .build()
                        .show();
            }

            @Override
            public void onInitialSyncSuccess() {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).logEventSuccessfullySignedIn(AnalyticsActionLocation.LOGIN_SCREEN);
                }
                mProgressBar.dismiss();
                openAppIfUserIsLoggedIn();
            }

            @Override
            public void onInitialSyncFail() {
                mProgressBar.dismiss();
            }
        };
        mViewModel.setOnLoginListener(mOnLoginListener);
    }


    private void openAppIfUserIsLoggedIn() {
        if (mViewModel.isUserLoggedIn()) {
            Intent intent = new Intent(getActivity(), NemeStatsMainActivity_.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            Log.e(TAG, "openAppIfUserIsLoggedIn: Some error occured");
        }
    }

    @Click(R.id.btn_forgot_password)
    public void onBtnForgotPasswordClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_forgot_password)));
        startActivity(browserIntent);
    }

    @Click(R.id.btn_login)
    public void onBtnLoginClick() {
        mViewModel.createUserSession(mEdtUserName.getText().toString(), mEdtPassword.getText().toString());
    }

    @Click(R.id.btn_go_to_create_account)
    public void onBtnCreateAccountClick() {
        if (mOnBtnCreateAccountClickListener != null) {
            mOnBtnCreateAccountClickListener.onBtnCreateAccountClick();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mOnBtnCreateAccountClickListener = (OnBtnCreateAccountClickListener) context;
        }
    }

    @AfterViews
    public void setupViews() {
        if (getActivity() != null) {
            mEdtUserName.setCompoundDrawablesRelativeWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_person_outline), null, null, null);
            mEdtPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_lock_outline), null, null, null);
        }
    }

    public static LoginFragment newInstance() {
        return new LoginFragment_();
    }


    public interface OnBtnCreateAccountClickListener {
        void onBtnCreateAccountClick();
    }
}
