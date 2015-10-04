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

/**
 * Created by Nicolai on 01-10-2015.
 */
public class ReminderListArrayAdapter extends ArrayAdapter<Reminder> {
    private static final int LIST_ROW_LAYOUT = R.layout.reminder_list_row;
    private static final int TITLE_TEXT_VIEW = R.id.reminderTitle;
    private static final int DETAILS_TEXT_VIEW = R.id.reminderTypeDetail;
    private static final int ICON_VIEW = R.id.reminderTypeIconView;
    private static final int EXPIRES_TEXT_VIEW = R.id.reminderExpiresText;

    private final Context context;
    private final ArrayList<Reminder> reminders;

    static class ViewHolder {
        protected TextView titleTextView;
        protected TextView expiresInTextView;
        protected IconicsImageView typeIconView;
        protected TextView typeDetailsTextView;
    }

    public ReminderListArrayAdapter(Context context, ArrayList<Reminder> reminders) {
        super(context, LIST_ROW_LAYOUT,
                reminders);
        this.context = context;
        this.reminders = reminders;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(LIST_ROW_LAYOUT, parent, false);

            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.titleTextView = (TextView) view.findViewById(TITLE_TEXT_VIEW);
            viewHolder.expiresInTextView = (TextView) view.findViewById(EXPIRES_TEXT_VIEW);
            viewHolder.typeDetailsTextView = (TextView) view.findViewById(DETAILS_TEXT_VIEW);
            viewHolder.typeIconView = (IconicsImageView) view.findViewById(ICON_VIEW);

            Reminder reminder = reminders.get(position);

            viewHolder.titleTextView.setText(reminder.getTitle());
            viewHolder.typeDetailsTextView.setText(reminder.typeDetails());
            viewHolder.expiresInTextView.setText(reminder.getExpiresInString());
            viewHolder.typeIconView.setIcon(reminder.icon());

            view.setTag(viewHolder);
        } else { // Can reuse view
            // Need to set view details from tag
            view = convertView;
            view.setTag(reminders.get(position));
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        //ParseUser user = ParseUser.getCurrentUser();
        Reminder reminder = reminders.get(position);

        // Fill in data
        holder.titleTextView.setText(reminder.getTitle());
        holder.typeDetailsTextView.setText(reminder.typeDetails());
        holder.expiresInTextView.setText(reminder.getExpiresInString());
        holder.typeIconView.setIcon(reminder.icon());

        return view;
    }
}
