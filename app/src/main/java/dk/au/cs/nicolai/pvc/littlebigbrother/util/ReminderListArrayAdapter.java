package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;

import dk.au.cs.nicolai.pvc.littlebigbrother.R;
import dk.au.cs.nicolai.pvc.littlebigbrother.database.Reminder;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.OnReminderListItemLongClickCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.ReminderListWidget;

/**
 * Created by Nicolai on 01-10-2015.
 */
public class ReminderListArrayAdapter extends ArrayAdapter<Reminder> {
    private static final int LIST_ROW_LAYOUT = R.layout.reminder_list_row;
    private static final int TITLE_TEXT_VIEW = R.id.reminderTitle;
    private static final int DETAILS_TEXT_VIEW = R.id.reminderTypeDetail;
    private static final int ICON_VIEW = R.id.reminderTypeIconView;
    private static final int EXPIRES_TEXT_VIEW = R.id.reminderExpiresText;
    private static final int DELETE_BUTTON = R.id.deleteReminderButton;

    private ReminderListWidget.OnDeleteReminderButtonClickedCallback deleteCallback;
    private OnReminderListItemLongClickCallback longClickCallback;

    private final Context context;
    private final ArrayList<Reminder> reminders;

    public ReminderListArrayAdapter(Context context, ArrayList<Reminder> reminders) {
        super(context, LIST_ROW_LAYOUT,
                reminders);
        this.context = context;
        this.reminders = reminders;
    }

    public void setOnDeleteReminderButtonClickedCallback(ReminderListWidget.OnDeleteReminderButtonClickedCallback callback) {
        deleteCallback = callback;
    }

    public void setOnReminderListItemLongClickCallback(OnReminderListItemLongClickCallback callback) {
        longClickCallback = callback;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(LIST_ROW_LAYOUT, parent, false);

        TextView titleTextView = (TextView) view.findViewById(TITLE_TEXT_VIEW);
        TextView expiresInTextView = (TextView) view.findViewById(EXPIRES_TEXT_VIEW);
        TextView typeDetailsTextView = (TextView) view.findViewById(DETAILS_TEXT_VIEW);
        IconicsImageView typeIconView = (IconicsImageView) view.findViewById(ICON_VIEW);
        IconicsImageView deleteButton = (IconicsImageView) view.findViewById(DELETE_BUTTON);

        final Reminder reminder = reminders.get(position);

        titleTextView.setText(reminder.getTitle());
        typeDetailsTextView.setText(reminder.typeDetails());
        expiresInTextView.setText(reminder.getExpiresInString());
        typeIconView.setIcon(reminder.icon());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCallback.delete(reminder);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickCallback.call(reminder);
                return false;
            }
        });

        return view;
    }
}
