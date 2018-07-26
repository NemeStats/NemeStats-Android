package com.nemestats.boardgametracker.viewModels;

import android.content.Context;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.managers.AccountManager;
import com.nemestats.boardgametracker.managers.SyncManager;
import com.nemestats.boardgametracker.webservices.errorHandling.GenericRXErrorHandling;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mehegeo on 9/23/17.
 */

public class CreateAccountFragmentViewModel {

    private OnCreateAccountListener mOnCreateAccountListener;
    private AccountManager mAccountManager;
    private Context mContext;
    private DisposableCompletableObserver mCreateAccountDisposableObserver;
    private SyncManager mSyncManager;
    private DisposableCompletableObserver mInitialSyncDisposableObserver;

    @Inject
    public CreateAccountFragmentViewModel(Context context, SyncManager syncManager, AccountManager accountManager) {
        mContext = context;
        mSyncManager = syncManager;
        mAccountManager = accountManager;
    }

    public void createAccount(String email, String userName, String password, String confirmationPassword) {
        try {
            validateCreateAccountInput(email, userName, password, confirmationPassword);
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnCreateAccountListener != null) {
                mOnCreateAccountListener.onCreateAccountFail(e.getMessage());
            }
            return;
        }

        mCreateAccountDisposableObserver = new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                if (mOnCreateAccountListener != null) {
                    mOnCreateAccountListener.onCreateAccountSuccess();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                performLogout();
                if (mOnCreateAccountListener != null) {
                    mOnCreateAccountListener.onCreateAccountFail(GenericRXErrorHandling.extractErrorMessage(mContext, e));
                }
            }
        };

        mAccountManager.createAccount(email, userName, password, confirmationPassword)
                .doOnComplete(this::startInitialSync)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCreateAccountDisposableObserver);

    }

    public void startInitialSync() {
        mInitialSyncDisposableObserver = new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                if (mOnCreateAccountListener != null) {
                    mOnCreateAccountListener.onInitialSyncSuccess();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                performLogout();
                if (mOnCreateAccountListener != null) {
                    mOnCreateAccountListener.onInitialSyncFail();
                }

            }
        };

        mSyncManager.triggerSyncData(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mInitialSyncDisposableObserver);
    }

    private void performLogout() {
        mAccountManager.deleteAllUserData();
    }


    private void validateCreateAccountInput(String email, String userName, String password, String confirmationPassword) throws Exception {
        String errorMessage = "";
        if (email == null || email.isEmpty()) {
            errorMessage += mContext.getString(R.string.txt_email_required) + "\n";
        }
        if (userName == null || userName.isEmpty()) {
            errorMessage += mContext.getString(R.string.txt_username_required) + "\n";
        }
        if (password == null || password.isEmpty()) {
            errorMessage += mContext.getString(R.string.txt_password_required) + "\n";
        }

        if (confirmationPassword == null || confirmationPassword.isEmpty()) {
            errorMessage += mContext.getString(R.string.txt_confirmation_password_required) + "\n";
        }

        if (!password.equalsIgnoreCase(confirmationPassword)) {
            errorMessage += mContext.getString(R.string.txt_passwords_do_not_match) + "\n";
        }
        if (!errorMessage.isEmpty()) {
            throw new Exception(errorMessage);
        }
    }

    public void setOnCreateAccountListener(OnCreateAccountListener onCreateAccountListener) {
        mOnCreateAccountListener = onCreateAccountListener;
    }

    public boolean isUserLoggedIn() {
        return mAccountManager.isUserLoggedIn();
    }

    public interface OnCreateAccountListener {

        void onCreateAccountSuccess();

        void onCreateAccountFail(String errorMessage);

        void onInitialSyncSuccess();

        void onInitialSyncFail();
    }
}
