package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.mikepenz.iconics.view.IconicsImageView;

import java.util.Calendar;

import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.SimpleDateTime;

/**
 * Created by Nicolai on 06-10-2015.
 */
public final class SelectDateTimeWidget extends Widget implements InteractiveWidget {

    private static final String DATE_SEPARATOR = "/";
    private static final String TIME_SEPARATOR = ":";

    private Button selectDateButton;
    private Button selectTimeButton;
    private IconicsImageView resetButton;

    private String DATE_BUTTON_DEFAULT_VALUE;
    private String TIME_BUTTON_DEFAULT_VALUE;

    private SimpleDateTime dateTime;

    public SelectDateTimeWidget(Activity context, View container, Button selectDateButton, Button selectTimeButton) {
        super(context, container);

        dateTime = new SimpleDateTime();

        this.selectDateButton = selectDateButton;
        this.selectTimeButton = selectTimeButton;

        DATE_BUTTON_DEFAULT_VALUE = (String) selectDateButton.getText();
        TIME_BUTTON_DEFAULT_VALUE = (String) selectTimeButton.getText();

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
    }

    public SelectDateTimeWidget(Activity context, int containerResId, int selectDateButtonResId, int selectTimeButtonResId) {
        this(context, context.findViewById(containerResId), (Button) context.findViewById(selectDateButtonResId), (Button) context.findViewById(selectTimeButtonResId));
    }

    public void setResetButton(IconicsImageView resetButton) {
        this.resetButton = resetButton;
        enableResetButton();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    public void setResetButton(int resetButtonResId) {
        setResetButton((IconicsImageView) getContext().findViewById(resetButtonResId));
        disableResetButton();
    }

    private void enableResetButton() {
        resetButton.setEnabled(true);
    }

    private void disableResetButton() {
        resetButton.setEnabled(false);
    }

    public void resetDateButton() {
        selectDateButton.setText(DATE_BUTTON_DEFAULT_VALUE);
    }

    public void resetTimeButton() {
        selectTimeButton.setText(TIME_BUTTON_DEFAULT_VALUE);
    }

    public void setDate(int year, int month, int day) {
        dateTime.setDate(year, month, day);

        updateSelectDateButtonText();
    }

    public void setDate(SimpleDateTime dateTime) {
        this.dateTime.setDate(dateTime);

        updateSelectDateButtonText();
    }

    public void setTime(int hour, int minute) {
        dateTime.setTime(hour, minute);

        updateSelectTimeButtonText();
    }

    public void setTime(SimpleDateTime dateTime) {
        this.dateTime.setTime(dateTime);

        updateSelectTimeButtonText();
    }

    public void setDateTime(SimpleDateTime dateTime) {
        setDate(dateTime);
        setTime(dateTime);
    }

    public void setDateTime(Calendar calendar) {
        dateTime = new SimpleDateTime(calendar);

        update();
    }

    private void updateSelectDateButtonText() {
        selectDateButton.setText(dateTime.dateString());
    }

    private void updateSelectTimeButtonText() {
        selectTimeButton.setText(dateTime.timeString());
    }

    private void update() {
        updateSelectDateButtonText();
        updateSelectTimeButtonText();

        if (resetButton != null) {
            enableResetButton();
        }
    }

    public void reset() {
        dateTime = new SimpleDateTime();
        resetDateButton();
        resetTimeButton();

        if (resetButton != null) {
            enableResetButton();
        }
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getContext().getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getContext().getFragmentManager(), "timePicker");
    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment() {}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            SimpleDateTime dateTime = new SimpleDateTime(year, month, day);

            Log.debug(this, "Date selected: " + dateTime.dateString());
            setDate(dateTime);
        }
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public TimePickerFragment() {}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            SimpleDateTime dateTime = new SimpleDateTime(hourOfDay, minute);

            Log.debug(this, "Time selected: " + dateTime.timeString());
            setTime(dateTime);
        }
    }
}
