package com.nemestats.boardgametracker.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.managers.RateReminderManager;

/**
 * Created by mehegeo on 11/24/17.
 */

public class RateReminderUtils {

    public static void showRateDialog(final Context context, final RateReminderManager rateReminderManager) {


        AlertDialog.Builder stepOneDialog = new AlertDialog.Builder(context);
        stepOneDialog.setCancelable(false);
//        stepOneDialog.setIcon(R.drawable.ic_launcher_2);

        stepOneDialog.setTitle(context.getResources().getString(R.string.do_you_enjoy_nemestats));
        stepOneDialog.setMessage(" ");

        stepOneDialog.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                AlertDialog.Builder stepTwoPositive = new AlertDialog.Builder(context);
                stepTwoPositive.setCancelable(false);
//                stepTwoPositive.setIcon(R.drawable.ic_launcher_2);
                stepTwoPositive.setTitle(context.getResources().getString(R.string.how_about_a_rating_on_google_play));
                stepTwoPositive.setMessage(" ");

                stepTwoPositive.setPositiveButton(R.string.txt_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rateReminderManager.setAppRated(true);
                        openPlayStore(context, rateReminderManager);
                    }
                });

                stepTwoPositive.setNeutralButton(context.getResources().getString(R.string.maybe_later), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog stepTwoPositiveAlertDialog = stepTwoPositive.create();
                stepTwoPositiveAlertDialog.show();
                stepTwoPositiveAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(context, R.color.color_material_gray_500));
                stepTwoPositiveAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        });


        stepOneDialog.setNeutralButton(context.getResources().getString(R.string.txt_not_really), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder stepTwoNegative = new AlertDialog.Builder(context);

                stepTwoNegative.setCancelable(false);
//                stepTwoNegative.setIcon(R.drawable.ic_launcher_2);
                stepTwoNegative.setTitle(context.getResources().getString(R.string.can_we_make_it_better));
                stepTwoNegative.setMessage(context.getResources().getString(R.string.would_you_mind_giving_us_some_feedback));

                stepTwoNegative.setPositiveButton(context.getResources().getString(R.string.txt_sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rateReminderManager.setAppRated(true);
                        openPlayStore(context, rateReminderManager);
                    }
                });

                stepTwoNegative.setNeutralButton(context.getResources().getString(R.string.maybe_later), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog stepTwoNegativeAlertDialog = stepTwoNegative.create();
                stepTwoNegativeAlertDialog.show();
                stepTwoNegativeAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(context, R.color.color_material_gray_500));
                stepTwoNegativeAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        });


        AlertDialog alertDialog = stepOneDialog.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(context, R.color.color_material_gray_500));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    public static void openPlayStore(Context context, RateReminderManager rateReminderManager) {
        rateReminderManager.setAppRated(true);
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

}
