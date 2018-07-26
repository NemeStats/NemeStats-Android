package com.nemestats.boardgametracker.customViews.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.domain.PlayedGame;

/**
 * Created by geomehedeniuc on 4/26/18.
 */

public class PlayedGameEditNotesDialog extends MaterialDialog {

    private final PlayedGame mPlayedGame;

    public PlayedGameEditNotesDialog(Builder builder, PlayedGame playedGame) {
        super(builder);
        mPlayedGame = playedGame;
        initViews();
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setDimAmount(.7f);
        }
    }

    private void initViews() {
        View customView = getCustomView();
        bindViews(customView);
    }

    private void bindViews(View customView) {
        View btnSave = customView.findViewById(R.id.btn_save);

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> dismiss());
        }

        EditText editTextGameNotes = customView.findViewById(R.id.edt_game_notes);

        if (mPlayedGame.getNotes() != null) {
            editTextGameNotes.setText(mPlayedGame.getNotes());
        } else {
            editTextGameNotes.setText("");
        }

        editTextGameNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlayedGame.setNotes(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
