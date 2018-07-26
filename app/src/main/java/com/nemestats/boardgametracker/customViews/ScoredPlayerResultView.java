package com.nemestats.boardgametracker.customViews;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayerViewModel;

/**
 * Created by geomehedeniuc on 4/12/18.
 */

public class ScoredPlayerResultView extends LinearLayout {

    private PlayerViewModel mPlayerViewModel;
    private EditText mEditPointsScored;
    private TextView mTxtPlayerBadge;
    private TextView mTxtPlayerName;


    public ScoredPlayerResultView(Context context, PlayerViewModel playerViewModel) {
        super(context);
        mPlayerViewModel = playerViewModel;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_scored_player_result, this);
        mTxtPlayerBadge = findViewById(R.id.txt_badge);
        mTxtPlayerName = findViewById(R.id.txt_player_name);
        mEditPointsScored = findViewById(R.id.edt_points_scored);

        mTxtPlayerName.setText(mPlayerViewModel.getPlayer().getPlayerName());
        mTxtPlayerBadge.setText(mPlayerViewModel.getPlayer().getPlayerName().substring(0, 1));

        if (mPlayerViewModel.getPointsScored() != -1) {
            mEditPointsScored.setText(String.valueOf(mPlayerViewModel.getPointsScored()));
        }

        mEditPointsScored.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    mPlayerViewModel.setPointsScored(Float.parseFloat(s.toString()));
                } else {
                    mPlayerViewModel.setPointsScored(-1f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}
