package com.nemestats.boardgametracker.customViews.drag;

import android.content.ClipData;
import android.content.ClipDescription;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by geomehedeniuc on 3/18/18.
 */

public class DragTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent event) {
//        ClipData.Item item = new ClipData.Item((CharSequence) String.valueOf(view.getTag()));
//        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
//
//        ClipData dragData = new ClipData(view.getTag().toString(), mimeTypes, item);
//        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);
//
//        view.startDrag(dragData, myShadow, null, 0);
//        return true;


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");

            //            ClipData.Item item = new ClipData.Item(view.getTag());
//            ClipData dragData = new ClipData(view.getTag(), ClipDescription.MIMETYPE_TEXT_PLAIN, item);
//            DragShadowBuilder dragShadowBuilder = new DragShadowBuilder(view);

            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }

}