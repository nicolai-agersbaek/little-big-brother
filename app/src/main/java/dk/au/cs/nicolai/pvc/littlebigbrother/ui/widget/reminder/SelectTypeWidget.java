package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import dk.au.cs.nicolai.pvc.littlebigbrother.database.ReminderType;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.Widget;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class SelectTypeWidget extends Widget {

    private TypeSelectButton locationButton;
    private TypeSelectButton targetUserButton;
    private TypeSelectButton dateTimeButton;

    private OnTypeSelectedCallback onTypeSelectedCallback;

    public SelectTypeWidget(Activity context, int containerResId, int locationButtonResId, int targetUserButtonResId, int dateTimeButtonResId) {
        super(context, containerResId);

        locationButton   = new TypeSelectButton(locationButtonResId, ReminderType.LOCATION);
        targetUserButton = new TypeSelectButton(targetUserButtonResId, ReminderType.TARGET_USER);
        dateTimeButton   = new TypeSelectButton(dateTimeButtonResId, ReminderType.DATE_TIME);
    }

    public void setOnTypeSelectedCallback(OnTypeSelectedCallback callback) {
        onTypeSelectedCallback = callback;
    }

    private OnTypeSelectedCallback getOnTypeSelectedCallback() {
        return onTypeSelectedCallback;
    }

    private class TypeSelectButton {

        private Button button;
        private ReminderType type;

        public TypeSelectButton(int layoutResId, ReminderType type) {
            button = (Button) getContext().findViewById(layoutResId);
            this.type = type;

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOnTypeSelectedCallback().done(getType());
                }
            });
        }

        private ReminderType getType() {
            return type;
        }
    }

    public static abstract class OnTypeSelectedCallback {
        public abstract void done(ReminderType type);
    }
}
