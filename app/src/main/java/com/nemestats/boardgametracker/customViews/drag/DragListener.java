package com.nemestats.boardgametracker.customViews.drag;

import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.customViews.PlayerView;
import com.nemestats.boardgametracker.customViews.RankBoxView;
import com.nemestats.boardgametracker.domain.Player;

/**
 * Created by geomehedeniuc on 3/18/18.
 */

public class DragListener implements View.OnDragListener {
    public static final String TAG = DragListener.class.getSimpleName();

    private OnViewDropListener mOnViewDropListener;

    public DragListener(OnViewDropListener onViewDropListener) {
        mOnViewDropListener = onViewDropListener;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        PlayerView playerView;

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                Log.e(TAG, "ACTION_DRAG_STARTED");
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                ViewCompat.animate(v).scaleX(1.05f).scaleY(1.05f).setDuration(180).setInterpolator(new OvershootInterpolator()).start();
                Log.e(TAG, "ACTION_DRAG_ENTERED");
                return true;

            case DragEvent.ACTION_DROP:
                Log.e(TAG, "ACTION_DROP");

                //TODO remove View
                playerView = (PlayerView) event.getLocalState();
                ViewGroup owner = (ViewGroup) playerView.getParent();
                owner.removeView(playerView);

                // TODO: 3/18/18 Add view to new Container
                RankBoxView rankBox = (RankBoxView) v;
                LinearLayout rankboxPlayersContainer = rankBox.findViewById(R.id.container_players);
                rankboxPlayersContainer.addView(playerView, rankboxPlayersContainer.getChildCount());
                playerView.setVisibility(View.VISIBLE);
                if (mOnViewDropListener != null) {
                    mOnViewDropListener.onPlayersDroppedInRankBox(rankBox, playerView);
                }
                ViewCompat.animate(v).scaleX(1f).scaleY(1f).setDuration(180).setInterpolator(new DecelerateInterpolator()).start();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                Log.e(TAG, "ACTION_DRAG_LOCATION");
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                ViewCompat.animate(v).scaleX(1f).scaleY(1f).setDuration(180).setInterpolator(new DecelerateInterpolator()).start();
                Log.e(TAG, "ACTION_DRAG_EXITED");
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                Log.e(TAG, "ACTION_DRAG_ENDED");
                playerView = (PlayerView) event.getLocalState();
                playerView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        return true;
    }

    public interface OnViewDropListener {
        void onPlayersDroppedInRankBox(RankBoxView rankBoxView, PlayerView player);
    }
}
