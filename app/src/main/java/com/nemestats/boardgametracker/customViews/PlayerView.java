package com.nemestats.boardgametracker.customViews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayerViewModel;

/**
 * Created by geomehedeniuc on 3/17/18.
 */

public class PlayerView extends LinearLayout {

    private TextView mTxtPlayerName;
    private View mBtnRemovePlayer;

    private PlayerViewModel mPlayerViewModel;
    private OnButtonRemoveListener mOnButtonRemoveListener;
    private boolean mHasRemoveButton;
    private boolean mIsSelectionStep;
    private CardView mCardView;

    public PlayerView(Context context, PlayerViewModel playerViewModel, OnButtonRemoveListener onButtonRemoveListener, boolean hasRemoveButton, boolean isSelectionStep) {
        super(context);
        mPlayerViewModel = playerViewModel;
        mOnButtonRemoveListener = onButtonRemoveListener;
        mHasRemoveButton = hasRemoveButton;
        mIsSelectionStep = isSelectionStep;
        init();
    }


    private void init() {
        if (mIsSelectionStep) {
            inflate(getContext(), R.layout.item_selected_player_selection_step, this);
        } else {
            inflate(getContext(), R.layout.item_selected_player_set_ranked_results_step, this);
            mCardView = findViewById(R.id.card_view);
            assert mCardView.findViewById(R.id.img_drag_handle) != null;
            animateElevation();
        }

        mTxtPlayerName = findViewById(R.id.txt_player_name);
        mBtnRemovePlayer = findViewById(R.id.btn_remove_player);
        if (mBtnRemovePlayer != null) {
            if (mHasRemoveButton) {
                mBtnRemovePlayer.setVisibility(VISIBLE);
            } else {
                mBtnRemovePlayer.setVisibility(GONE);
            }

            mBtnRemovePlayer.setOnClickListener(v -> {
                if (mOnButtonRemoveListener != null) {
                    mOnButtonRemoveListener.onRemoveClick(mPlayerViewModel);
                }
            });
        }

        mTxtPlayerName.setText(mPlayerViewModel.getPlayer().getPlayerName());
    }


    private void animateElevation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mCardView.getCardElevation(), mCardView.getCardElevation() * 2);
        int mDuration = 450; //in millis
        valueAnimator.setDuration(mDuration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> mCardView.setCardElevation((float) animation.getAnimatedValue()));
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();
    }

    public PlayerViewModel getPlayerViewModel() {
        return mPlayerViewModel;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setScaleX(0f);
        setScaleY(0f);
        ViewCompat.animate(this)
                .scaleX(1f).scaleY(1f)
                .setDuration(250).setInterpolator(new OvershootInterpolator())
                .start();

    }

    public interface OnButtonRemoveListener {
        void onRemoveClick(PlayerViewModel playerViewModel);
    }


}
