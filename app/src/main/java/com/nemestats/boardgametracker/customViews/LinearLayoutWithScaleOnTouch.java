package com.nemestats.boardgametracker.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class LinearLayoutWithScaleOnTouch extends LinearLayout implements View.OnTouchListener {

    private final float START_SCALE = 1.0f;
    private final float END_SCALE = 0.95f;
    private final int ANIMATION_DURATION = 110;
    private Animation mScaleDownAnimation;
    private Animation mScaleUpAnimation;


    public LinearLayoutWithScaleOnTouch(Context context) {
        super(context);
        setOnTouchListener(this);
        setupAnimations();
    }

    public LinearLayoutWithScaleOnTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        setupAnimations();
    }

    public LinearLayoutWithScaleOnTouch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
        setupAnimations();
    }

    private void setupAnimations() {

        mScaleDownAnimation = new ScaleAnimation(START_SCALE, END_SCALE, START_SCALE, END_SCALE,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleDownAnimation.setFillAfter(true);
        mScaleDownAnimation.setInterpolator(new OvershootInterpolator());
        mScaleDownAnimation.setDuration(ANIMATION_DURATION);

        mScaleUpAnimation = new ScaleAnimation(END_SCALE, START_SCALE, END_SCALE, START_SCALE,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleUpAnimation.setFillAfter(true);
        mScaleUpAnimation.setInterpolator(new OvershootInterpolator());
        mScaleUpAnimation.setDuration(ANIMATION_DURATION);
    }

    public void scaleViewUp(ViewGroup v) {
        final View child = v.getChildAt(0);
        if (mScaleDownAnimation.hasStarted() && mScaleDownAnimation.hasEnded()) {
            child.startAnimation(mScaleUpAnimation);
            return;
        }
        if (mScaleDownAnimation.hasStarted()) {
            mScaleDownAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    child.startAnimation(mScaleUpAnimation);
                    mScaleDownAnimation.setAnimationListener(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    public void scaleViewDown(ViewGroup v) {
        View child = v.getChildAt(0);
        child.startAnimation(mScaleDownAnimation);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case ACTION_DOWN:
                scaleViewDown(this);
                break;
            case ACTION_UP:
                scaleViewUp(this);
                break;
            case ACTION_CANCEL:
                scaleViewUp(this);
                break;
        }

        return false;
    }
}
