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
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mehegeo on 9/23/17.
 */

public class LoginFragmentViewModel {

    private AccountManager mAccountManager;
    private SyncManager mSyncManager;
    private OnLoginListener mOnLoginListener;
    private DisposableObserver mLoginDisposableObserver;
    private DisposableCompletableObserver mInitialSyncDisposableObserver;
    private Context mContext;

    @Inject
    public LoginFragmentViewModel(Context context, AccountManager accountManager, SyncManager syncManager) {
        mContext = context;
        mAccountManager = accountManager;
        mSyncManager = syncManager;
    }

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        mOnLoginListener = onLoginListener;
    }

    public void createUserSession(String userName, String password) {
        if (mOnLoginListener != null) {
            mOnLoginListener.onLoginStarted();
        }
        if (userName.isEmpty() || password.isEmpty()) {
            if (mOnLoginListener != null) {
                mOnLoginListener.onLoginFailed(mContext.getString(R.string.txt_empty_username_or_password));
            }
            return;
        }

        mLoginDisposableObserver = new DisposableObserver() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                performLogout();
                if (mOnLoginListener != null) {
                    mOnLoginListener.onLoginFailed(GenericRXErrorHandling.extractErrorMessage(mContext, e));
                }
            }

            @Override
            public void onComplete() {
                if (mOnLoginListener != null) {
                    mOnLoginListener.onLoginSuccess();
                }
            }
        };

        mAccountManager.createUserSession(userName, password)
                .doOnComplete(this::startInitialSync)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mLoginDisposableObserver);

    }

    public void startInitialSync() {
        mInitialSyncDisposableObserver = new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                if (mOnLoginListener != null) {
                    mOnLoginListener.onInitialSyncSuccess();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                performLogout();
                if (mOnLoginListener != null) {
                    mOnLoginListener.onInitialSyncFail();
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

    public boolean isUserLoggedIn() {
        return mAccountManager.isUserLoggedIn();
    }

    public interface OnLoginListener {
        void onLoginStarted();

        void onLoginSuccess();

        void onLoginFailed(String errorMessage);

        void onInitialSyncSuccess();

        void onInitialSyncFail();
    }
}
