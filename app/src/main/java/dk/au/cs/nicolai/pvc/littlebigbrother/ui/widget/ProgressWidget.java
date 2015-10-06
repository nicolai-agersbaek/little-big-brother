package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import dk.au.cs.nicolai.pvc.littlebigbrother.util.Util;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class ProgressWidget extends Widget {

    private ProgressBar progressBar;
    private TextView label;

    public ProgressWidget(Activity context, View container) {
        super(context, container);
    }

    public ProgressWidget(Activity context, int containerResId) {
        super(context, containerResId);
    }

    public ProgressWidget(Activity context, View container, TextView label) {
        this(context, container);

        this.label = label;
    }

    public ProgressWidget(Activity context, int containerResId, int labelResId) {
        this(context, context.findViewById(containerResId), (TextView) context.findViewById(labelResId));
    }

    @Override
    public void show() {
        Util.animatedShowView(getContext(), true, getContainer());
    }

    @Override
    public void hide() {
        Util.animatedShowView(getContext(), false, getContainer());
    }

    public void setLabel(int stringResId) {
        if (label != null) {
            label.setText(getContext().getString(stringResId));
        }
    }

    public void setLabel(String labelText) {
        if (label != null) {
            label.setText(labelText);
        }
    }
}
