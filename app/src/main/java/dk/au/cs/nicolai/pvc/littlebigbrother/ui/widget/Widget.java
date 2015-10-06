package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget;

import android.app.Activity;
import android.view.View;

import dk.au.cs.nicolai.pvc.littlebigbrother.util.Util;

/**
 * Created by Nicolai on 06-10-2015.
 */
public abstract class Widget {

    private Activity context;
    private View container;

    protected Widget(Activity context, View container) {
        this.context = context;
        this.container = container;
    }

    protected Widget(Activity context, int containerResId) {
        this(context, context.findViewById(containerResId));
    }

    public void show() {
        Util.showView(container);
    }

    public void hide() {
        Util.hideView(container);
    }

    protected final Activity getContext() {
        return context;
    }

    protected final View getContainer() {
        return container;
    }
}
