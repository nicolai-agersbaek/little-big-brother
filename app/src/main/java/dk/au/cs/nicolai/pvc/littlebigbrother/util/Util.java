package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseGeoPoint;

import java.util.Calendar;
import java.util.Collection;

import dk.au.cs.nicolai.pvc.littlebigbrother.database.DateTime;

/**
 * Created by Nicolai on 04-10-2015.
 */
public final class Util {
    public static final String LATITUDE_LONGITUDE_SEPARATOR = ":";

    public static void setDateText(TextView view, Calendar calendar) {
        view.setText(DateTime.getDateString(calendar));
    }

    public static void setDateText(Button view, Calendar calendar) {
        view.setText(DateTime.getDateString(calendar));
    }

    public static void setTimeText(TextView view, Calendar calendar) {
        view.setText(DateTime.getTimeString(calendar));
    }

    public static void setTimeText(Button view, Calendar calendar) {
        view.setText(DateTime.getTimeString(calendar));
    }

    public static String getLocationString(Location location) {
        return location.getLatitude() + LATITUDE_LONGITUDE_SEPARATOR + location.getLongitude();
    }

    public static String getLocationString(ParseGeoPoint location) {
        return location.getLatitude() + LATITUDE_LONGITUDE_SEPARATOR + location.getLongitude();
    }

    public static void setLocationText(TextView view, Location location) {
        view.setText(getLocationString(location));
    }

    public static void setLocationText(Button view, Location location) {
        view.setText(getLocationString(location));
    }

    public static void setLocationText(TextView view, ParseGeoPoint location) {
        view.setText(getLocationString(location));
    }

    public static void setLocationText(Button view, ParseGeoPoint location) {
        view.setText(getLocationString(location));
    }

    public static void hideViews(View... views) {
        for (View view :
                views) {
            hideView(view);
        }
    }

    public static void showViews(View... views) {
        for (View view :
                views) {
            showView(view);
        }
    }

    public static <T extends Collection<View>> void hideViews(T views) {
        for (View view :
                views) {
            hideView(view);
        }
    }

    public static <T extends Collection<View>> void showViews(T views) {
        for (View view :
                views) {
            showView(view);
        }
    }

    public static void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    public static void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void showView(View view, boolean show) {
        view.setVisibility(
                (show ? View.VISIBLE : View.GONE)
        );
    }

    /**
     * Shows the progress UI and hides given Views.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void animatedShowView(Context context, final boolean show, final View view) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            view.setVisibility(show ? View.VISIBLE : View.GONE);
            view.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static <T extends Collection<View>> void animatedShowViews(Context context, final boolean show, T views) {
        for (final View view :
                views) {
            animatedShowView(context, show, view);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void animatedShowViews(Context context, final boolean show, View... views) {
        for (final View view :
                views) {
            animatedShowView(context, show, view);
        }
    }

    public static void showNotYetImplementedAlert(Context context, String srcName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // Set title
        alertDialogBuilder.setTitle("Error");

        // Set dialog message
        alertDialogBuilder
                .setMessage(srcName + " not yet implemented!")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close dialog, do nothing
                        dialog.cancel();
                    }
                });

        // Create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Show it
        alertDialog.show();
    }
}
