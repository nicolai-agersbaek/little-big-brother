package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;
import dk.au.cs.nicolai.pvc.littlebigbrother.database.ReminderWithRadius;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.Widget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.OnReminderDataChangedCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.OnReminderRadiusChangedCallback;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class SeekBarWidget extends Widget implements InteractiveWidget {

    private OnReminderRadiusChangedCallback radiusChangedCallback;
    private OnReminderDataChangedCallback dataChangedCallback;

    private SeekBar seekBar;
    private TextView label;

    private int[] VALUES;
    private int DEFAULT_VALUE = -1;
    private int MAX_PROGRESS = -1;
    private int DEFAULT_PROGRESS = -1;

    private String DEFAULT_LABEL_TEXT;

    public SeekBarWidget(Activity context, int containerResId, int seekBarResId, int labelResId) {
        super(context, containerResId);

        this.seekBar = (SeekBar) context.findViewById(seekBarResId);
        this.label = (TextView) context.findViewById(labelResId);

        DEFAULT_LABEL_TEXT = (String) label.getText();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    public void setOnReminderDataChangedCallback(OnReminderDataChangedCallback dataChangedCallback) {
        this.dataChangedCallback = dataChangedCallback;
    }

    public void setOnReminderRadiusChangedCallback(OnReminderRadiusChangedCallback radiusChangedCallback) {
        this.radiusChangedCallback = radiusChangedCallback;
    }

    public void setValues(int[] values) {
        VALUES = values;
        MAX_PROGRESS = VALUES.length - 1;
        seekBar.setMax(MAX_PROGRESS);

        if (DEFAULT_VALUE != -1) {
            DEFAULT_VALUE = VALUES[MAX_PROGRESS / 2];
            setValue(DEFAULT_VALUE);
        }
    }

    public void setDefaultValue(int defaultValue) {
        DEFAULT_VALUE = defaultValue;
        setValue(DEFAULT_VALUE);
        setLabelTextFromValue(DEFAULT_VALUE);
    }

    private void setLabelTextFromValue(int value) {
        String radius = LittleBigBrother.distanceAsString(value);
        label.setText(radius);
    }

    private void progressChanged(int progress) {
        int value = VALUES[progress];

        setLabelTextFromValue(value);

        if (radiusChangedCallback != null) {
            radiusChangedCallback.onRadiusChanged(value);
            dataChangedCallback.onRadiusChanged(value);
        }
    }

    private int getProgressFromValue(int value) {
        int progress = 0;

        for (int i = 0; i <= MAX_PROGRESS; i++) {
            if (VALUES[i] == value) {
                progress = i;
            }
        }

        return progress;
    }

    public int getValue() {
        int currentProgress = seekBar.getProgress();
        return VALUES[currentProgress];
    }

    public void setValue(int value) {
        int progress = getProgressFromValue(value);

        seekBar.setProgress(progress);
    }

    public void setValue(ReminderWithRadius reminder) {
        if (reminder.getRadius() != null) {
            setValue(reminder.getRadius());
        }
    }

    public final void reset() {
        seekBar.setProgress(DEFAULT_PROGRESS);
    }
}
