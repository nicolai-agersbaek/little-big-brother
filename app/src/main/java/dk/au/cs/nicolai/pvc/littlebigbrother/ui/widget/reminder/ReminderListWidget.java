package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder;

import android.app.Activity;
import android.database.DataSetObserver;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;

import dk.au.cs.nicolai.pvc.littlebigbrother.R;
import dk.au.cs.nicolai.pvc.littlebigbrother.database.Reminder;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.Widget;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ReminderListArrayAdapter;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ViewUtil;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class ReminderListWidget extends Widget {

    private int SNACKBAR_UNDO_DELETE_DURATION = Snackbar.LENGTH_SHORT;
    private int SNACKBAR_UNDO_DELETE_NOTICE = R.string.notice_reminderDeleted;
    private int SNACKBAR_UNDO_DELETE_ACTION = R.string.action_undo;

    private Snackbar undoDeleteSnackbar;

    private Reminder deletedReminder;

    protected ReminderCallback reminderCallback;

    protected ReminderListArrayAdapter adapter;
    protected ListView listView;

    private NoRemindersWidget noRemindersWidget;

    public ReminderListWidget(Activity context, int containerResId, int listViewResId) {
        super(context, containerResId);

        adapter = new ReminderListArrayAdapter(context, new ArrayList<Reminder>());
        adapter.setNotifyOnChange(true);
        adapter.setOnDeleteReminderButtonClickedCallback(new OnDeleteReminderButtonClickedCallback());

        listView = (ListView) context.findViewById(listViewResId);
        if (adapter != null) {
            listView.setAdapter(adapter);
        }

        setSnackbar();
        setEmptyDataObserver();
    }

    private void setSnackbar() {
        undoDeleteSnackbar = Snackbar.make(getContainer(), SNACKBAR_UNDO_DELETE_NOTICE, SNACKBAR_UNDO_DELETE_DURATION);
        undoDeleteSnackbar.setAction(SNACKBAR_UNDO_DELETE_ACTION, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deletedReminder != null) {
                    add(deletedReminder);
                    deletedReminder = null;
                }
            }
        });
        undoDeleteSnackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);

                switch (event) {
                    case DISMISS_EVENT_ACTION:
                        // UNDO selected
                        break;
                    case DISMISS_EVENT_TIMEOUT:
                        // Timed out
                        reminderCallback.delete(deletedReminder);
                        break;
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
            }
        });
    }

    private void setEmptyDataObserver() {
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                noRemindersWidget.show(adapter.isEmpty());
            }
        });
    }

    @Override
    public void show() {
        if (adapter.isEmpty()) {
            ViewUtil.hideView(listView);
            noRemindersWidget.show();
        } else {
            ViewUtil.showView(listView);
            noRemindersWidget.hide();
        }

        super.show();
    }

    public void setNoRemindersWidget(int containerResId, int addReminderButtonResId) {
        noRemindersWidget = new NoRemindersWidget(getContext(), containerResId, addReminderButtonResId);
    }

    private class NoRemindersWidget extends Widget {
        private Button addReminderButton;

        public NoRemindersWidget(Activity context, int containerResId, int addReminderButtonResId) {
            super(context, containerResId);

            addReminderButton = (Button) context.findViewById(addReminderButtonResId);

            addReminderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reminderCallback.add();
                }
            });
        }
    }

    public class OnDeleteReminderButtonClickedCallback {
        public void delete(Reminder reminder) {
            deletedReminder = reminder;
            remove(reminder);
            undoDeleteSnackbar.show();
        }
    }

    public void setReminderCallback(ReminderCallback reminderCallback) {
        this.reminderCallback = reminderCallback;
    }

    public void setOnReminderListItemLongClickCallback(OnReminderListItemLongClickCallback callback) {
        adapter.setOnReminderListItemLongClickCallback(callback);
    }

    public void set(Collection<Reminder> reminders) {
        adapter.clear();
        adapter.addAll(reminders);
    }

    public void add(Reminder reminder) {
        adapter.add(reminder);
    }

    public void remove(Reminder reminder) {
        adapter.remove(reminder);
    }

    public void addAll(Collection<Reminder> collection) {
        adapter.addAll(collection);
    }

    public void removeAll(Collection<Reminder> collection) {
        for (Reminder reminder:
                collection) {
            adapter.remove(reminder);
        }
    }
}
