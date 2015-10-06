package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget;

import android.app.Activity;
import android.widget.TextView;

import dk.au.cs.nicolai.pvc.littlebigbrother.ui.Widget;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ViewUtil;

/**
 * Created by Nicolai on 06-10-2015.
 *
 * TODO: Enable determinate and linear progress bar styles.
 */
public class ProgressWidget extends Widget {

    //private ProgressBar progressBar;
    private TextView label;

    public ProgressWidget(Activity context, int containerResId) {
        super(context, containerResId);
    }

    public ProgressWidget(Activity context, int containerResId, int labelResId) {
        this(context, containerResId);

        this.label = (TextView) context.findViewById(labelResId);
    }

    @Override
    public void show() {
        ViewUtil.animatedShowView(getContext(), true, getContainer());
    }

    @Override
    public void hide() {
        ViewUtil.animatedShowView(getContext(), false, getContainer());
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
