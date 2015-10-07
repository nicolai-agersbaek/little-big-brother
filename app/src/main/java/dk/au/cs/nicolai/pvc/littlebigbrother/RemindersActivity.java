package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.MapFragment;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import dk.au.cs.nicolai.pvc.littlebigbrother.database.Reminder;
import dk.au.cs.nicolai.pvc.littlebigbrother.database.ReminderType;
import dk.au.cs.nicolai.pvc.littlebigbrother.database.ReminderWithRadius;
import dk.au.cs.nicolai.pvc.littlebigbrother.database.UserListResultCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.exception.UserNotLoggedInException;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.Widget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.WidgetCollection;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.LocationPicker;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.ProgressWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.SeekBarWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.SelectDateTimeWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.FinalizeReminderWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.OnReminderListItemLongClickCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.ReminderCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.ReminderListWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.SelectLocationWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.SelectTypeWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ActivityDrawer;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ReminderListArrayAdapter;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.SimpleDateTime;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Util;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ViewUtil;

public class RemindersActivity extends AppCompatActivity {

    private WidgetCollection mReminderViewWidgets;
    private WidgetCollection mWidgets;
    private HashSet<View> mViews;

    // Reminder information
    private Reminder mReminder;

    // Drawer
    private Drawer mDrawer;

    // Progress
    private ProgressWidget mProgressWidget;

    // Reminder view
    private View mReminderView;
    private View mReminderTitleAndDescriptionContainer;

    private EditText mReminderTitleView;
    private EditText mReminderDescriptionView;

    // Select type
    private SelectTypeWidget mSelectTypeWidget;

    private View mReminderDetailsContainer;

    // TargetUser
    private View mTargetUserForm;
    private IconicsImageView mTargetUserIconView;
    private Spinner mSelectTargetUserSpinner;
    private ArrayList<CharSequence> mUserNamesList;
    private ArrayAdapter<CharSequence> mSelectUserArrayAdapter;

    // DateTime
    private SelectDateTimeWidget mSelectDateTimeWidget;

    // SelectRadius
    private SeekBarWidget mSelectRadiusWidget;

    // Expiration
    private SelectDateTimeWidget mSelectExpirationDateTimeWidget;

    // Finalize
    private FinalizeReminderWidget mFinalizeReminderWidget;

    private String SAVE_CHANGES_PROGRESS_TEXT;
    private String CREATE_REMINDER_PROGRESS_TEXT;

    // Reminders list
    private ReminderListWidget mReminderListWidget;
    private ReminderListArrayAdapter mReminderListAdapter;

    // Map
    private LocationPicker mLocationPicker;
    private SelectLocationWidget mSelectLocationWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        SAVE_CHANGES_PROGRESS_TEXT = getString(R.string.progress_savingChanges);
        CREATE_REMINDER_PROGRESS_TEXT = getString(R.string.progress_creatingReminder);

        mReminderViewWidgets = new WidgetCollection();
        mWidgets = new WidgetCollection();
        mViews = new HashSet<>();

        mDrawer = ActivityDrawer.build(this);

        if (mDrawer != null) {
            mDrawer.setSelection(ApplicationController.DrawerPosition.REMINDERS);
        }

        mProgressWidget = newWidget(new ProgressWidget(this,
                R.id.reminderProgress,
                R.id.reminderProgressTextView));

        mLocationPicker = new LocationPicker(this,
                (MapFragment) getFragmentManager().findFragmentById(R.id.reminderMapFragment),
                R.id.reminderMapContainer,
                R.id.reminderMapConfirmLocationButton);

        mSelectLocationWidget = newReminderViewWidget(new SelectLocationWidget(this,
                R.id.reminderLocationForm,
                R.id.reminderSelectLocationButton,
                mLocationPicker));

        mSelectDateTimeWidget = newReminderViewWidget(new SelectDateTimeWidget(this,
                R.id.reminderDateTimeForm,
                R.id.reminderSelectDateButton,
                R.id.reminderSelectTimeButton));

        mSelectExpirationDateTimeWidget = newReminderViewWidget(new SelectDateTimeWidget(this,
                R.id.reminderExpirationForm,
                R.id.reminderExpirationSelectDateButton,
                R.id.reminderExpirationSelectTimeButton));
        mSelectExpirationDateTimeWidget.setResetButton(R.id.reminderExpirationResetDateTimeButton);

        mSelectRadiusWidget = newReminderViewWidget(new SeekBarWidget(this,
                R.id.reminderSelectRadiusForm,
                R.id.reminderRadiusSeekBar,
                R.id.reminderRadiusValueTextView));
        mSelectRadiusWidget.setValues(getResources().getIntArray(R.array.reminderRadiusSeekBarValues));
        mSelectRadiusWidget.setDefaultValue(getResources().getInteger(R.integer.reminderRadiusSeekBarDefaultValue));

        mSelectTypeWidget = newReminderViewWidget(new SelectTypeWidget(this,
                R.id.reminderSelectTypeContainer,
                R.id.reminderSelectLocationTypeButton,
                R.id.reminderSelectTargetUserTypeButton,
                R.id.reminderSelectDateTimeTypeButton));
        mSelectTypeWidget.setOnTypeSelectedCallback(new SelectTypeWidget.OnTypeSelectedCallback() {
            @Override
            public void done(ReminderType type) {
                reminderTypeSelected(type);
            }
        });

        mFinalizeReminderWidget = newReminderViewWidget(new FinalizeReminderWidget(this,
                R.id.reminderFinalizeReminderButtonsContainer,
                R.id.reminderSaveChangesButton,
                R.id.reminderCreateReminderButton,
                R.id.reminderCancelButton));
        mFinalizeReminderWidget.setOnButtonClickedCallback(new FinalizeReminderWidget.OnButtonClickedCallback() {
            @Override
            public void save() {
                saveChangesButtonClicked();
            }

            @Override
            public void create() {
                createReminderButtonClicked();
            }

            @Override
            public void cancel() {
                cancelButtonClicked();
            }
        });

        mReminderListWidget = newWidget(new ReminderListWidget(this,
                R.id.reminderRemindersListContainer,
                R.id.remindersListView));
        mReminderListWidget.setOnReminderListItemLongClickCallback(new OnReminderListItemLongClickCallback() {
            @Override
            public void call(Reminder reminder) {
                editReminder(reminder);
            }
        });
        mReminderListWidget.setReminderCallback(new ReminderCallback() {
            @Override
            public void add() {
                newReminder();
            }
        });
        mReminderListWidget.setNoRemindersWidget(R.id.reminderNoRemindersContainer, R.id.reminderAddReminderBorderlessButton);


        // Reminder view: Edit/Create
        mReminderView = getViewById(R.id.reminderView);
        mReminderTitleAndDescriptionContainer = getViewById(R.id.reminderTitleAndDescriptionContainer);

        mReminderTitleView = (EditText) findViewById(R.id.reminderTitleEdit);
        mReminderDescriptionView = (EditText) findViewById(R.id.reminderDescriptionEdit);

        initTargetUserForm();

        // Reminder details
        mReminderDetailsContainer = getViewById(R.id.reminderDetailsContainer);

        setSelectUserArrayAdapter();

        //initRemindersList();

        test();

        init();

        IntentFilter filter = new IntentFilter(LittleBigBrother.Events.GOOGLE_API_CLIENT_CONNECTED);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               Log.debug(this, "Broadcast received");
               mLocationPicker.getMapAsync();
            }
        }, filter);
    }

    private void test() {
        SimpleDateTime dateTime = new SimpleDateTime(Calendar.getInstance());

        try {
            for (int i = 1; i < 5; i++) {
                Reminder.DateTime reminder = new Reminder.DateTime();
                reminder.setTitle("Reminder " + Integer.toString(i));
                reminder.setDescription("Here is a very, very short description of this reminder.");
                reminder.setDate(dateTime);

                mReminderListWidget.add(reminder);
            }

            Reminder.TargetUser reminder2 = new Reminder.TargetUser();
            reminder2.setTitle("Reminder");
            reminder2.setDescription("Here is a very, very short description of this reminder.");
            reminder2.setTargetUser(ParseUser.getCurrentUser());
            mReminderListWidget.add(reminder2);

            Reminder.Location reminder3 = new Reminder.Location();
            reminder3.setTitle("Reminder");
            reminder3.setDescription("Here is a very, very short description of this reminder.");
            reminder3.setPosition(new ParseGeoPoint(50, 50));
            mReminderListWidget.add(reminder3);

        } catch (UserNotLoggedInException e) {
            Log.debug(this, "test() : UserNotLoggedInException");
        }
    }

    private void populateRemindersList(Reminder... reminders) {
        populateRemindersList(Arrays.asList(reminders));
    }

    private <T extends Widget> T newReminderViewWidget(T widget) {
        mReminderViewWidgets.add(widget);

        return newWidget(widget);
    }

    private <T extends Widget> T newWidget(T widget) {
        mWidgets.add(widget);

        return widget;
    }

    private View getViewById(int resourceId) {
        View view = findViewById(resourceId);
        mViews.add(view);

        return view;
    }

    private void setSelectUserArrayAdapter() {
        ApplicationController.getUserListFromDatabase(new UserListResultCallback() {
            @Override
            public void success(List<ParseUser> userList) {
                setSelectUserArrayAdapter(userList);
            }

            @Override
            public void error(ParseException e) {
                Log.exception(this, e);
            }
        });
    }

    private void setSelectUserArrayAdapter(List<ParseUser> users) {
        mUserNamesList = ApplicationController.getUsernamesFromParseUserList(users);

        mSelectUserArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mUserNamesList);
        mSelectUserArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSelectTargetUserSpinner.setAdapter(mSelectUserArrayAdapter);
    }

    // TODO: Support for clone() in order to enable detection of changes.
    private void setOriginalReminder(Reminder reminder) {
        //mOriginialReminder =
    }

    private void setCurrentReminder(Reminder reminder) {
        mReminder = reminder;
    }

    private void reminderTypeSelected(ReminderType type) {
        Log.debug(this, "Selected type: " + type);

        setForSelectType(type);

        try {
            switch (type) {
                case LOCATION:
                    mReminder = new Reminder.Location();
                    break;
                case TARGET_USER:
                    mReminder = new Reminder.TargetUser();
                    break;
                case DATE_TIME:
                    mReminder = new Reminder.DateTime();
                    break;
            }
        } catch (UserNotLoggedInException e) {

        }
    }

    // TARGET USER FORM

    private void initTargetUserForm() {
        mTargetUserForm = getViewById(R.id.reminderTargetUserForm);


        mTargetUserIconView = (IconicsImageView) findViewById(R.id.reminderTargetUserIcon);
        mSelectTargetUserSpinner = (Spinner) findViewById(R.id.reminderSelectTargetUserSpinner);
    }

    // FINALIZE REMINDER FORM
    private void saveChangesButtonClicked() {
        mProgressWidget.setLabel(SAVE_CHANGES_PROGRESS_TEXT);
        saveChanges();
    }

    private void saveChanges() {
        Log.debug(this, "Saving changes to Reminder: " + mReminder.getTitle());

        Util.showNotYetImplementedAlert(this, "saveChanges()");
    }

    private void createReminderButtonClicked() {
        mProgressWidget.setLabel(CREATE_REMINDER_PROGRESS_TEXT);
        createReminder();
    }

    private void createReminder() {
        Log.error(this, "createReminder not yet implemented.");

        Util.showNotYetImplementedAlert(this, "createReminder()");
    }

    private void editReminder(Reminder reminder) {
        initForExistingReminder(reminder);
    }

    /**
     * <p>Cancel the changes made to the currently active Reminder.</p>
     *
     * <p>If any changes were made, show the alert dialog.</p>
     * TODO: Detect when changes are made?
     */
    private void cancelButtonClicked() {
        Log.debug(this, "CancelButton clicked.");

        showDiscardChangesAlertDialog();
    }

    /**
     * <p>Request to add a reminder from the Options menu</p>
     *
     * <p>Show alert dialog if currently editing/creating a reminder</p>
     */
    private void addReminder() {
        Log.debug(this, "addReminder invoked.");

        if (mReminder != null) {
            showDiscardChangesAlertDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    initForNewReminder();
                }
            });
        } else {
            initForNewReminder();
        }
    }

    private void setReminderFormFieldsFromReminder(Reminder reminder) {
        setCurrentReminder(reminder);

        mReminderTitleView.setText(reminder.getTitle());
        mReminderDescriptionView.setText(reminder.getDescription());

        setExpirationDateFormFieldsFromReminder(reminder);

        switch (reminder.type()) {
            case LOCATION:
                setLocationFormFieldsFromReminder((Reminder.Location) reminder);
                break;
            case TARGET_USER:
                setTargetUserFormFieldsFromReminder((Reminder.TargetUser) reminder);
                break;
            case DATE_TIME:
                setDateTimeFormFieldsFromReminder((Reminder.DateTime) reminder);
                break;
        }
    }

    private void setExpirationDateFormFieldsFromReminder(Reminder reminder) {
        if (reminder.getExpires() != null) {
            mSelectExpirationDateTimeWidget.setDateTime(reminder.getExpires());
        }
    }

    private void setRadiusFormFieldsFromReminder(ReminderWithRadius reminder) {
        mSelectRadiusWidget.setValue(reminder.getRadius());
    }

    private void setLocationFormFieldsFromReminder(Reminder.Location reminder) {
        setRadiusFormFieldsFromReminder(reminder);

        // TODO: Set location from existing reminder
        //mLocationPicker.setLocation(reminder.getPosition());
    }

    private void setTargetUserFormFieldsFromReminder(Reminder.TargetUser reminder) {
        setRadiusFormFieldsFromReminder(reminder);


    }

    private void setDateTimeFormFieldsFromReminder(Reminder.DateTime reminder) {
        mSelectDateTimeWidget.setDateTime(reminder.getDate());
    }

    /**
     * <p>Initialize ReminderView for creating a new reminder.</p>
     */
    private void initForNewReminder() {
        clearReminderFormFields();

        setToNewReminder();

        mReminderView.clearFocus();

        //mReminderTitleView.requestFocus();
    }

    private void initForExistingReminder(Reminder reminder) {
        setToEditReminder(reminder);

        // Set form fields from reminder object
        setReminderFormFieldsFromReminder(reminder);
    }

    private void clearReminderFormFields() {
        Util.showNotYetImplementedAlert(this, "clearReminderFormFields()");
    }

    private void deleteReminder(Reminder reminder) {
        Log.debug(this, "Deleting reminder: " + reminder.getTitle());

        Util.showNotYetImplementedAlert(this, "deleteReminder()");
    }

    private void hideAll() {
        for (View view :
                mViews) {
            ViewUtil.hideView(view);
        }
        mWidgets.hideAll();
    }

    private void init() {
        setToReminderList();

        mProgressWidget.setLabel(R.string.progress_gettingReminders);
        mProgressWidget.hide();
    }


    // Reminders list
    private void initRemindersList() {
        getRemindersFromDatabase();
    }

    private void pinRemindersInBackground(List<Reminder> reminders) {
        // Get user object to perform query with
        ParseUser user = ParseUser.getCurrentUser();

        if (user != null) {
            ParseObject.pinAllInBackground(reminders);
        } else {
            Log.error(this, "Attempted to pin reminders. User not logged in.");
        }
    }

    private void getRemindersFromDatabase() {
        Log.debug(this, "Getting reminders from database");

        // Get user object to perform query with
        final ParseUser user = ParseUser.getCurrentUser();

        if (user != null) {
            // Get list of reminders from database
            ParseQuery<Reminder> query = ParseQuery.getQuery("Reminders");
            query.whereEqualTo("owner", user);
            query.findInBackground(new FindCallback<Reminder>() {
                public void done(final List<Reminder> reminders, ParseException e) {
                    if (e == null) {
                        Log.debug(this, "Retrieved reminders from server. Result count: " + reminders.size());
                        if (!reminders.isEmpty()) {
                            pinRemindersInBackground(reminders);
                            populateRemindersList(reminders);
                        } else {
                            // Get Reminders from local datastore
                            getRemindersFromLocalStore(user);
                        }
                    } else {
                        Log.error(this, "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            Log.error(this, "User not logged in.");
        }
    }

    private void getRemindersFromLocalStore(ParseUser user) {
        Log.debug(this, "Getting reminders from local store");

        ParseQuery<Reminder> query = ParseQuery.getQuery("Reminders");
        query.whereEqualTo("owner", user);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Reminder>() {
            @Override
            public void done(List<Reminder> reminders, ParseException e) {
                if (e == null) {
                    Log.debug(this, "Retrieved reminders from local storage. Result count: " + reminders.size());
                    if (!reminders.isEmpty()) {
                        populateRemindersList(reminders);
                    }
                } else {
                    Log.error(this, "Error: " + e.getMessage());
                }
            }
        });
    }

    private void clearRemindersList() {
        // First item in list should be add reminder

        mReminderListAdapter.clear();
    }

    private void populateRemindersList(List<Reminder> reminders) {
        //mRemindersList = (ArrayList<Reminder>) reminders;

        if (mReminderListAdapter == null) {
            // First time we populate the list
            mReminderListAdapter = new ReminderListArrayAdapter(this, (ArrayList<Reminder>) reminders);
            //mRemindersListView.setAdapter(mReminderListAdapter);
            return;
        }

        if (!mReminderListAdapter.isEmpty()) {
            clearRemindersList();
        }

        mReminderListAdapter.addAll(reminders);
        mReminderListAdapter.notifyDataSetChanged();

        mProgressWidget.hide();
    }


    // Alert: Discard changes

    public void showDiscardChangesAlertDialog(DialogInterface.OnClickListener onDiscardClickedListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set title
        alertDialogBuilder.setTitle("Are you sure?");

        // Set dialog message
        alertDialogBuilder
                .setMessage("You will lose all changes made!")
                .setCancelable(false)
                .setPositiveButton("Discard", onDiscardClickedListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close dialog, do nothing
                        dialog.cancel();
                    }
                });

        // Create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Show it
        alertDialog.show();
    }

    /**
     * <p>Shows an AlertDialog asking user if the changes to the currently selected Reminder
     * should be discarded.</p>
     *
     * <p>Should be invoked when user is editing/creating a Reminder and does one of the following:</p>
     * <ul>
     *     <li>Pressed Add Reminder in OptionsMenu</li>
     *     <li>Uses Back button to return to previous activity</li>
     * </ul>
     *
     * <p>Returns the user to the Reminder list.</p>
     */
    public void showDiscardChangesAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set title
        alertDialogBuilder.setTitle("Are you sure?");

        // Set dialog message
        alertDialogBuilder
                .setMessage("You will lose all changes made!")
                .setCancelable(false)
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Return user to the Reminders list
                        setToReminderList();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close dialog, do nothing
                        dialog.cancel();
                    }
                });

        // Create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Show it
        alertDialog.show();
    }


    // Options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_newReminder) {
            Log.debug(this, "onOptionsItemSelected: " + getString(R.string.action_addReminder));
            addReminder();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setToReminderList() {
        hideAll();

        mReminderListWidget.show();
    }

    private void setToReminder() {
        ViewUtil.showView(mReminderView);
    }

    private void setToNewReminder() {
        hideAll();

        setToReminder();

        ViewUtil.showView(mReminderTitleAndDescriptionContainer);
        mSelectTypeWidget.show();
    }

    private void setToEditReminder(Reminder reminder) {
        hideAll();

        setToReminder();

        ViewUtil.showView(mReminderTitleAndDescriptionContainer);

        setForSelectType(reminder.type());
    }

    private void setForAfterSelectType() {
        ViewUtil.showView(mReminderDetailsContainer);
        mFinalizeReminderWidget.show();

        mSelectTypeWidget.hide();
        mSelectExpirationDateTimeWidget.show();
    }

    private void setForSelectType(ReminderType type) {
        switch (type) {
            case LOCATION:
                setForSelectLocationType();
                break;
            case TARGET_USER:
                setForSelectTargetUserType();
                break;
            case DATE_TIME:
                setForSelectDateTimeType();
                break;
        }

        setForAfterSelectType();
    }

    private void setForSelectLocationType() {
        mSelectLocationWidget.show();
    }

    private void setForSelectTargetUserType() {
        ViewUtil.showView(mTargetUserForm);
    }

    private void setForSelectDateTimeType() {
        mSelectDateTimeWidget.show();
    }

    private void newReminder() {
        Util.showNotYetImplementedAlert(this, "newReminder() not yet implemented");
    }
}