package com.nemestats.boardgametracker.fragments.recordPlayedGame;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.interfaces.OnCreatePlayedGameEventsListener;
import com.nemestats.boardgametracker.viewModels.RecordPlayedGameViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by geomehedeniuc on 3/12/18.
 */

@EFragment(R.layout.fragment_choose_date)
public class ChooseDateFragment extends Fragment {

    @ViewById(R.id.txt_game_played_date)
    TextView mTxtDataGamePlayed;

    @ViewById(R.id.btn_custom_date)
    View mCustomDateButton;

    @ViewById(R.id.btn_today)
    View mBtnToday;

    @ViewById(R.id.btn_yesterday)
    View mBtnYesterday;

    @ViewById(R.id.label_yesterday)
    TextView mTxtYesterday;

    @ViewById(R.id.label_today)
    TextView mTxtToday;

    @ViewById(R.id.label_custom_date)
    TextView mTxtCustomDate;

    @ViewById(R.id.txt_game_played_date)
    TextView mTxtGamePlayedDate;

    @ViewById(R.id.btn_next)
    View mBtnNext;

    @ViewById(R.id.btn_previous)
    View mBtnPrevious;

    private DateTimeFormatter mDateTimeFormatter;
    private RecordPlayedGameViewModel mViewModel;

    private OnCreatePlayedGameEventsListener mOnCreatePlayedGameEventsListener;

    public static ChooseDateFragment newInstance() {
        return new ChooseDateFragment_();
    }

    public void onPageSelected(boolean isThisCurrentPage) {
        if (isThisCurrentPage) {
            hideKeyboard();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = RecordPlayedGameViewModel.getInstance();
        mDateTimeFormatter = DateTimeFormat.forPattern("dd MMM yyyy");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCreatePlayedGameEventsListener) {
            mOnCreatePlayedGameEventsListener = (OnCreatePlayedGameEventsListener) context;
        }
    }

    @Click(R.id.btn_today)
    public void onBtnTodayClick() {
        mViewModel.setDatePlayed(DateTime.now());
        setupUI();
        goToNextPage();
    }

    @Click(R.id.btn_yesterday)
    public void onBtnYesterdayClick() {
        mViewModel.setDatePlayed(DateTime.now().minusDays(1));
        setupUI();
        goToNextPage();
    }

    @Click(R.id.btn_next)
    public void onBtnNextClick() {
        if (mOnCreatePlayedGameEventsListener != null) {
            mOnCreatePlayedGameEventsListener.onBtnNextClick();
        }
    }

    @Click(R.id.btn_custom_date)
    public void onBtnCustomDateClick() {
        if (getActivity() != null) {
            DateTime date = DateTime.now();

            if (mViewModel.getPlayedGame().getDatePlayed() != null) {
                date = mViewModel.getPlayedGame().getDatePlayed();
            }

            DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, dayOfMonth) -> {
                DateTime dateTime = new DateTime(year, month + 1, dayOfMonth, 0, 0);
                mViewModel.setDatePlayed(dateTime);
                setupUI();
                goToNextPage();
            };


            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), onDateSetListener, date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());

            DatePicker.OnDateChangedListener onDateChangedListener = (view, year, monthOfYear, dayOfMonth) -> {
                datePickerDialog.dismiss();
                DateTime dateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                mViewModel.setDatePlayed(dateTime);
                setupUI();
                goToNextPage();
            };

            datePickerDialog.getDatePicker().init(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth(), onDateChangedListener);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
    }

    @AfterViews
    public void setupViews() {
        setupUI();
    }

    private void setupUI() {
        if (getActivity() != null) {
            mBtnPrevious.setVisibility(View.GONE);
            int textColorUnselected = ContextCompat.getColor(getActivity(), R.color.color_game_definition_name);
            int textColorSelected = ContextCompat.getColor(getActivity(), R.color.color_fab_blue_normal);

            mTxtGamePlayedDate.setText(mDateTimeFormatter.print(mViewModel.getPlayedGame().getDatePlayed()));

            mTxtToday.setTextColor(textColorUnselected);
            mTxtYesterday.setTextColor(textColorUnselected);
            mTxtCustomDate.setTextColor(textColorUnselected);

            mBtnToday.setSelected(false);
            mBtnYesterday.setSelected(false);
            mCustomDateButton.setSelected(false);

            if (mViewModel.isToday()) {
                mTxtToday.setTextColor(textColorSelected);
                mBtnToday.setSelected(true);
            } else if (mViewModel.isYesterday()) {
                mTxtYesterday.setTextColor(textColorSelected);
                mBtnYesterday.setSelected(true);
            } else {
                mTxtCustomDate.setTextColor(textColorSelected);
                mCustomDateButton.setSelected(true);
            }
        }
    }

    @Background(delay = 300)
    public void goToNextPage() {
        getActivity().runOnUiThread(this::onBtnNextClick);
    }

    private void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && this.getActivity().getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}
