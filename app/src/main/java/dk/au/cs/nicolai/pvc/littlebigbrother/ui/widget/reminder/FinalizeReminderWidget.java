package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import dk.au.cs.nicolai.pvc.littlebigbrother.ui.Widget;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class FinalizeReminderWidget extends Widget {

    private Button saveButton;
    private Button createButton;
    private Button cancelButton;

    private OnButtonClickedCallback onButtonClickedCallback;

    public FinalizeReminderWidget(Activity context, int containerResId, int saveButtonResId, int createButtonResId, int cancelButtonResId) {
        super(context, containerResId);

        saveButton = (Button) context.findViewById(saveButtonResId);
        createButton = (Button) context.findViewById(createButtonResId);
        cancelButton = (Button) context.findViewById(cancelButtonResId);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickedCallback.save();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickedCallback.create();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickedCallback.cancel();
            }
        });
    }

    public void setOnButtonClickedCallback(OnButtonClickedCallback callback) {
        this.onButtonClickedCallback = callback;
    }

    public abstract static class OnButtonClickedCallback {
        public abstract void save();
        public abstract void create();
        public abstract void cancel();
    }
}