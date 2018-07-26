package com.nemestats.boardgametracker.customViews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class LinearLayoutWithKeyboardListener extends LinearLayout {
    public static final String TAG = LinearLayoutWithKeyboardListener.class.getSimpleName();

    public interface IKeyboardChanged {
        void onKeyboardShown(int actualHeight, int proposedheight);

        void onKeyboardHidden(int actualHeight, int proposedheight);
    }

    private ArrayList<IKeyboardChanged> keyboardListener = new ArrayList<IKeyboardChanged>();

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LinearLayoutWithKeyboardListener(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LinearLayoutWithKeyboardListener(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutWithKeyboardListener(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LinearLayoutWithKeyboardListener(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // TODO Auto-generated constructor stub
    }

    public void addKeyboardStateChangedListener(IKeyboardChanged listener) {
        keyboardListener.add(listener);
    }

    public void removeKeyboardStateChangedListener(IKeyboardChanged listener) {
        keyboardListener.remove(listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int proposedheight = View.MeasureSpec.getSize(heightMeasureSpec);
        final int actualHeight = getHeight();

        if (actualHeight > proposedheight) {
            notifyKeyboardShown(actualHeight, proposedheight);
        } else if (actualHeight < proposedheight) {
            notifyKeyboardHidden(actualHeight, proposedheight);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void notifyKeyboardHidden(int actualHeight, int proposedheight) {
        Log.e(TAG, "notifyKeyboardHidden: ");
        for (IKeyboardChanged listener : keyboardListener) {
            listener.onKeyboardHidden(actualHeight, proposedheight);
        }
    }

    private void notifyKeyboardShown(int actualHeight, int proposedheight) {
        Log.e(TAG, "notifyKeyboardShown: ");
        for (IKeyboardChanged listener : keyboardListener) {
            listener.onKeyboardShown(actualHeight, proposedheight);
        }
    }

}
