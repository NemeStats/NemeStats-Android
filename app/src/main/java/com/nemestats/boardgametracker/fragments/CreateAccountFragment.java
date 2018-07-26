package com.nemestats.boardgametracker.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.nemestats.boardgametracker.viewModels.CreateAccountFragmentViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * Created by mehegeo on 9/23/17.
 */

@EFragment(R.layout.fragment_create_account)
public class CreateAccountFragment extends Fragment {

    private static final String TAG = CreateAccountFragment.class.getSimpleName();
    private OnBtnReturnToLoginScreenClickListener mOnBtnReturnToLoginScreenClickListener;

    @ViewById(R.id.edt_password)
    EditText mEdtPassword;

    @ViewById(R.id.edt_confirm_password)
    EditText mEdtConfirmPassword;

    @ViewById(R.id.edt_user_name)
    EditText mEdtUserName;

    @ViewById(R.id.edt_email)
    EditText mEdtEmail;

    @Inject
    CreateAccountFragmentViewModel mViewModel;

    private MaterialDialog mProgressDialog;

    private CreateAccountFragmentViewModel.OnCreateAccountListener mOnCreateAccountListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);

        mProgressDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.txt_please_wait)
                .content(R.string.txt_creating_your_account)
                .progress(true, 100)
                .cancelable(false)
                .progressIndeterminateStyle(false)
                .build();

        mOnCreateAccountListener = new CreateAccountFragmentViewModel.OnCreateAccountListener() {
            @Override
            public void onCreateAccountSuccess() {

            }

            @Override
            public void onCreateAccountFail(String errorMessage) {
                if (getActivity() != null) {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    new MaterialDialog.Builder(getActivity())
                            .title(getString(R.string.txt_error))
                            .content(errorMessage)
                            .positiveText(getString(R.string.txt_ok))
                            .build()
                            .show();
                }
            }

            @Override
            public void onInitialSyncSuccess() {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).logEventSuccessfullySignedIn(AnalyticsActionLocation.CREATE_ACCOUNT_SCREEN);
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                openAppIfUserIsLoggedIn();
            }

            @Override
            public void onInitialSyncFail() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        };

        mViewModel.setOnCreateAccountListener(mOnCreateAccountListener);
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

    @Click(R.id.btn_back_return_to_login)
    public void onBtnBackReturnToLogin() {
        onBtnReturnToLoginClick();
    }

    @Click(R.id.btn_sign_up)
    public void onBtnSignUpClick() {
        String email = mEdtEmail.getText().toString();
        String userName = mEdtUserName.getText().toString();
        String password = mEdtPassword.getText().toString();
        String confirmationPassword = mEdtConfirmPassword.getText().toString();
        mProgressDialog.show();
        mViewModel.createAccount(email, userName, password, confirmationPassword);
    }

    @Click(R.id.btn_return_to_login)
    public void onBtnReturnToLoginClick() {
        if (mOnBtnReturnToLoginScreenClickListener != null) {
            mOnBtnReturnToLoginScreenClickListener.onBtnReturnToLoginScreenClick();
        }
    }


    @AfterViews
    public void setupViews() {
        if (getActivity() != null) {
            mEdtEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_outline_email), null, null, null);
            mEdtUserName.setCompoundDrawablesRelativeWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_person_outline), null, null, null);
            mEdtPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_lock_outline), null, null, null);
            mEdtConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_lock_outline), null, null, null);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mOnBtnReturnToLoginScreenClickListener = (OnBtnReturnToLoginScreenClickListener) context;
        }
    }

    public static CreateAccountFragment newInstance() {
        return new CreateAccountFragment_();
    }


    public interface OnBtnReturnToLoginScreenClickListener {
        void onBtnReturnToLoginScreenClick();
    }
}
