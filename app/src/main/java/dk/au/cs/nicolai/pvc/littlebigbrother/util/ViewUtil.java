package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

import java.util.Collection;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class ViewUtil {

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
}
