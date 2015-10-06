package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget;

import android.app.Activity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class SeekBarWidget extends Widget implements InteractiveWidget {

    private SeekBar seekBar;
    private TextView label;

    private int[] VALUES;
    private int DEFAULT_VALUE = -1;
    private int MAX_PROGRESS = -1;
    private int DEFAULT_PROGRESS = -1;

    private String DEFAULT_LABEL_TEXT;

    public SeekBarWidget(Activity context, View container, SeekBar seekBar, TextView label) {
        super(context, container);

        this.seekBar = seekBar;
        this.label = label;

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

    public SeekBarWidget(Activity context, int containerResId, int seekBarResId, int labelResId) {
        this(context, context.findViewById(containerResId), (SeekBar) context.findViewById(seekBarResId), (TextView) context.findViewById(labelResId));
    }

    public void setValues(int[] values) {
        VALUES = values;
        MAX_PROGRESS = VALUES.length - 1;

        if (DEFAULT_VALUE != -1) {
            DEFAULT_VALUE = VALUES[MAX_PROGRESS / 2];
            setValue(DEFAULT_VALUE);
        }
    }

    public void setDefaultValue(int defaultValue) {
        DEFAULT_VALUE = defaultValue;
        setValue(DEFAULT_VALUE);
    }

    private void progressChanged(int progress) {
        int radiusValue = VALUES[progress];
        String radius = LittleBigBrother.distanceAsString(radiusValue);
        label.setText(radius);
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

    public final void reset() {
        seekBar.setProgress(DEFAULT_PROGRESS);
    }
}
