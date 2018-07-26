package com.nemestats.boardgametracker.viewModels.UIViewModel;

import com.nemestats.boardgametracker.domain.Player;

/**
 * Created by geomehedeniuc on 3/17/18.
 */

public class PlayerViewModel {

    private Player mPlayer;
    private boolean mSelected;
    private int mAssignedRank = -1;
    private boolean mRankAssigned;
    private float mPointsScored = -1;

    public PlayerViewModel(Player player) {
        mPlayer = player;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public void setPlayer(Player player) {
        mPlayer = player;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public int getAssignedRank() {
        return mAssignedRank;
    }

    public void setRank(int assignedRank) {
        mAssignedRank = assignedRank;
    }

    public boolean isRankAssigned() {
        return mRankAssigned;
    }

    public void setRankAssigned(boolean rankAssigned) {
        mRankAssigned = rankAssigned;
    }

    public float getPointsScored() {
        return mPointsScored;
    }

    public void setPointsScored(float pointsScored) {
        mPointsScored = pointsScored;
    }
}
