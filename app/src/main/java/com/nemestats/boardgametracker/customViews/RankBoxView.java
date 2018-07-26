package com.nemestats.boardgametracker.customViews;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.utils.OrdinalIndicatorUtils;


/**
 * Created by geomehedeniuc on 3/18/18.
 */

public class RankBoxView extends LinearLayout {

    private TextView mTxtRank;
    private int mRank;

    public RankBoxView(Context context, int rank) {
        super(context);
        mRank = rank;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_rank_box, this);
        mTxtRank = findViewById(R.id.txt_box_badge_rank);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTxtRank.setText(Html.fromHtml(OrdinalIndicatorUtils.formatOrdinalIndicatorForNumber(mRank + 1), android.text.Html.FROM_HTML_MODE_LEGACY));
        } else {
            mTxtRank.setText(Html.fromHtml(OrdinalIndicatorUtils.formatOrdinalIndicatorForNumber(mRank + 1)));
        }
    }

    public int getRank() {
        return mRank;
    }
}
