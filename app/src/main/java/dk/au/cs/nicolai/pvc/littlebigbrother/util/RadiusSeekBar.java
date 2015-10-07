package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;
import dk.au.cs.nicolai.pvc.littlebigbrother.R;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class RadiusSeekBar {
    private Context context;
    private View container;
    private SeekBar seekBar;

    private int[] RADIUS_SEEKBAR_VALUES;
    private int RADIUS_SEEKBAR_DEFAULT_VALUE;
    private int RADIUS_SEEKBAR_MAX_PROGRESS;

    public RadiusSeekBar(Context context, View container, SeekBar seekBar) {
        this.context = context;
        this.container = container;
        this.seekBar = seekBar;

        init();
    }

    private void init() {
        RADIUS_SEEKBAR_VALUES = context.getResources().getIntArray(R.array.reminderRadiusSeekBarValues);
        RADIUS_SEEKBAR_DEFAULT_VALUE = context.getResources().getInteger(R.integer.reminderRadiusSeekBarDefaultValue);
        RADIUS_SEEKBAR_MAX_PROGRESS = RADIUS_SEEKBAR_VALUES.length - 1;
    }

    private void setRadiusSeekBarOnChangeListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusSelectionChanged(progress);
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

    private void radiusSelectionChanged(int progress) {
        int radiusValue = RADIUS_SEEKBAR_VALUES[progress];
        String radius = LittleBigBrother.distanceAsString(radiusValue);
        //mRadiusValueTextView.setText(radius);
    }
}
