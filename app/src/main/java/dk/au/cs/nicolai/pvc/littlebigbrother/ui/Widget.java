package dk.au.cs.nicolai.pvc.littlebigbrother.ui;

import android.app.Activity;
import android.view.View;

import dk.au.cs.nicolai.pvc.littlebigbrother.util.ViewUtil;

/**
 * Created by Nicolai on 06-10-2015.
 */
public abstract class Widget {

    private Activity context;
    private View container;

    protected Widget(Activity context, int containerResId) {
        this.context = context;
        this.container = context.findViewById(containerResId);
    }

    public void show() {
        ViewUtil.showView(container);
    }

    public void hide() {
        ViewUtil.hideView(container);
    }

    protected final Activity getContext() {
        return context;
    }

    protected final View getContainer() {
        return container;
    }
}
