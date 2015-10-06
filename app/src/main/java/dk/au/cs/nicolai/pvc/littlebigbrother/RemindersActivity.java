package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.maps.MapFragment;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dk.au.cs.nicolai.pvc.littlebigbrother.database.Reminder;
import dk.au.cs.nicolai.pvc.littlebigbrother.database.ReminderType;
import dk.au.cs.nicolai.pvc.littlebigbrother.database.ReminderWithRadius;
import dk.au.cs.nicolai.pvc.littlebigbrother.database.UserListResultCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.WidgetCollection;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.LocationPicker;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.ProgressWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.SeekBarWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.SelectDateTimeWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ActivityDrawer;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ReminderListArrayAdapter;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Util;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ViewUtil;

public class RemindersActivity extends AppCompatActivity {

    private final Context CONTEXT = this;

    private final FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();

    private WidgetCollection mReminderViewWidgets;

    // Reminder information
    private Reminder mCurrentReminder;

    // Drawer
    private Drawer mDrawer;

    // Progress
    private ProgressWidget mProgressWidget;

    // Reminder view
    private View mReminderView;

    private EditText mReminderTitleView;
    private EditText mReminderDescriptionView;

    // Select type
    private View mSelectTypeView;
    private Button mSelectTypeLocationButton;
    private Button mSelectTypeTargetUserButton;
    private Button mSelectTypeDateTimeButton;


    private View mReminderDetailsContainer;

    // Location
    private View mLocationForm;
    private IconicsImageView mLocationIconView;
    private Button mSelectLocationButton;

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
    private View mFinalizeReminderButtonsContainer;
    private Button mSaveChangesButton;
    private Button mCreateReminderButton;
    private Button mCancelButton;

    private String SAVE_CHANGES_PROGRESS_TEXT;
    private String CREATE_REMINDER_PROGRESS_TEXT;

    // No reminders
    private View mNoRemindersView;
    private Button mNoRemindersAddReminderButton;

    // Reminders list
    private ListView mRemindersListView;
    private ReminderListArrayAdapter mReminderListAdapter;
    private ArrayList<Reminder> mRemindersList = new ArrayList<>();

    // Map
    MapFragment mMapFragment;
    private LocationPicker mLocationPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        mDrawer = ActivityDrawer.build(this);

        if (mDrawer != null) {
            mDrawer.setSelection(ApplicationController.DrawerPosition.REMINDERS);
        }

        FloatingActionButton confirmMapLocationButton = (FloatingActionButton) findViewById(R.id.reminderMapConfirmLocationButton);
        confirmMapLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.debug(this, "FloatingActionButton clicked.");
            }
        });

        mLocationPicker = new LocationPicker(this,
                MapFragment.newInstance(),
                mFragmentTransaction,
                R.id.reminderMapContainer,
                R.id.reminderMapFragmentContainer,
                R.id.reminderSelectLocationButton,
                R.id.reminderMapConfirmLocationButton);

        mProgressWidget = new ProgressWidget(this,
                R.id.reminderProgress,
                R.id.reminderProgressTextView);

        mSelectDateTimeWidget = new SelectDateTimeWidget(this,
                R.id.reminderDateTimeForm,
                R.id.reminderSelectDateButton,
                R.id.reminderSelectTimeButton);

        mSelectExpirationDateTimeWidget = new SelectDateTimeWidget(this,
                R.id.reminderExpirationForm,
                R.id.reminderExpirationSelectDateButton,
                R.id.reminderExpirationSelectTimeButton);
        mSelectExpirationDateTimeWidget.setResetButton(R.id.reminderExpirationResetDateTimeButton);

        mSelectRadiusWidget = new SeekBarWidget(this,
                R.id.reminderSelectRadiusForm,
                R.id.reminderRadiusSeekBar,
                R.id.reminderRadiusValueTextView);
        mSelectRadiusWidget.setValues(getResources().getIntArray(R.array.reminderRadiusSeekBarValues));
        mSelectRadiusWidget.setDefaultValue(getResources().getInteger(R.integer.reminderRadiusSeekBarDefaultValue));

        mReminderViewWidgets = new WidgetCollection();
        mReminderViewWidgets.add(
                mSelectDateTimeWidget,
                mSelectExpirationDateTimeWidget);



        // Reminder view: Edit/Create
        mReminderView = findViewById(R.id.reminderView);

        mReminderTitleView = (EditText) findViewById(R.id.reminderTitleEdit);
        mReminderDescriptionView = (EditText) findViewById(R.id.reminderDescriptionEdit);


        // Reminder type selection
        mSelectTypeView = findViewById(R.id.reminderSelectTypeView);
        initSelectTypeButtons();

        initLocationForm();
        initTargetUserForm();

        // Reminder details
        mReminderDetailsContainer = findViewById(R.id.reminderDetailsContainer);

        initFinalizeReminderForm();

        setSelectUserArrayAdapter();


        mNoRemindersView = findViewById(R.id.reminderNoReminders);
        mNoRemindersAddReminderButton = (Button) findViewById(R.id.reminderAddReminderBorderlessButton);
        mNoRemindersAddReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminder();
            }
        });

        // Reminders list
        mRemindersListView = (ListView) findViewById(R.id.remindersListView);

        //initRemindersList();
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
        mCurrentReminder = reminder;
    }

    // TYPE SELECTION BUTTONS

    private void initSelectTypeButtons() {
        mSelectTypeLocationButton = (Button) findViewById(R.id.reminder_typeButton_location);
        mSelectTypeTargetUserButton = (Button) findViewById(R.id.reminder_typeButton_targetUser);
        mSelectTypeDateTimeButton = (Button) findViewById(R.id.reminder_typeButton_dateTime);

        setSelectTypeButtonsOnClickListeners();
    }

    private void setSelectTypeButtonsOnClickListeners() {
        mSelectTypeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderTypeSelected(ReminderType.LOCATION);
            }
        });

        mSelectTypeTargetUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderTypeSelected(ReminderType.TARGET_USER);
            }
        });

        mSelectTypeDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderTypeSelected(ReminderType.DATE_TIME);
            }
        });
    }

    private void reminderTypeSelected(ReminderType type) {
        Log.debug(this, "Selected type: " + type);

        ViewUtil.hideView(mSelectTypeView);
        ViewUtil.showView(mReminderDetailsContainer);

        mSelectExpirationDateTimeWidget.show();

        switch (type) {
            case LOCATION:
                mSelectRadiusWidget.show();
                locationTypeSelected();
                break;
            case TARGET_USER:
                mSelectRadiusWidget.show();
                targetUserTypeSelected();
                break;
            case DATE_TIME:
                dateTimeTypeSelected();
                break;
        }
    }

    private void locationTypeSelected() {
        initLocationForm();

        showLocationForm();
        hideTargetUserForm();
        mSelectDateTimeWidget.hide();
    }

    private void targetUserTypeSelected() {
        initTargetUserForm();

        hideLocationForm();
        showTargetUserForm();
        mSelectDateTimeWidget.hide();
    }

    private void dateTimeTypeSelected() {
        hideLocationForm();
        hideTargetUserForm();
        mSelectDateTimeWidget.show();
    }

    // LOCATION FORM

    private void initLocationForm() {
        mLocationForm = findViewById(R.id.reminderLocationForm);

        mLocationIconView = (IconicsImageView) findViewById(R.id.reminderLocationIcon);
        mSelectLocationButton = (Button) findViewById(R.id.reminderSelectLocationButton);

        setLocationButtonOnClickListener();
    }

    private void setLocationButtonOnClickListener() {
        mSelectTypeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLocationButtonClicked(v);
            }
        });
    }

    private void selectLocationButtonClicked(View parentView) {
        Log.debug(this, "Select location clicked.");
    }

    // TARGET USER FORM

    private void initTargetUserForm() {
        mTargetUserForm = findViewById(R.id.reminderTargetUserForm);

        mTargetUserIconView = (IconicsImageView) findViewById(R.id.reminderTargetUserIcon);
        mSelectTargetUserSpinner = (Spinner) findViewById(R.id.reminderSelectTargetUserSpinner);
    }

    // FINALIZE REMINDER FORM

    private void initFinalizeReminderForm() {
        mFinalizeReminderButtonsContainer = findViewById(R.id.reminderFinalizeReminderButtonsContainer);

        mSaveChangesButton = (Button) findViewById(R.id.reminderSaveChangesButton);
        mCreateReminderButton = (Button) findViewById(R.id.reminderCreateReminderButton);
        mCancelButton = (Button) findViewById(R.id.reminderCancelButton);

        SAVE_CHANGES_PROGRESS_TEXT = getString(R.string.progress_savingChanges);
        CREATE_REMINDER_PROGRESS_TEXT = getString(R.string.progress_creatingReminder);

        setFinalizeReminderButtonsOnClickListeners();
    }

    private void setFinalizeReminderButtonsOnClickListeners() {
        mSaveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChangesButtonClicked();
            }
        });

        mCreateReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReminderButtonClicked();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelButtonClicked();
            }
        });
    }

    private void saveChangesButtonClicked() {
        mProgressWidget.setLabel(SAVE_CHANGES_PROGRESS_TEXT);
        saveChanges();
    }

    private void saveChanges() {
        Log.debug(this, "Saving changes to Reminder: " + mCurrentReminder.getTitle());

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

    private void showReminderViewsBasedOnReminderType(ReminderType type) {
        switch (type) {
            case LOCATION:
                showLocationForm();
                break;
            case TARGET_USER:
                showTargetUserForm();
                break;
            case DATE_TIME:
                mSelectDateTimeWidget.show();
                break;
        }
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

        if (mReminderView.getVisibility() == View.VISIBLE) {
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

        mLocationPicker.setLocation(reminder.getPosition());
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

        hideRemindersList();
        hideNoRemindersView();

        hideLocationForm();
        hideTargetUserForm();
        mSelectDateTimeWidget.hide();

        showReminderView();

        showSelectTypeView();

        showCreateReminderButton();
        hideSaveChangesButton();

        mReminderView.clearFocus();

        //mReminderTitleView.requestFocus();
    }

    private void initForExistingReminder(Reminder reminder) {
        // Set form fields from reminder object
        setReminderFormFieldsFromReminder(reminder);

        hideRemindersList();

        showReminderView();
        hideSelectTypeView();

        showReminderViewsBasedOnReminderType(reminder.type());

        showSaveChangesButton();
        hideCreateReminderButton();
    }

    private void clearReminderFormFields() {
        Util.showNotYetImplementedAlert(this, "clearReminderFormFields()");
    }

    private void deleteReminder(Reminder reminder) {
        Log.debug(this, "Deleting reminder: " + reminder.getTitle());

        Util.showNotYetImplementedAlert(this, "deleteReminder()");
    }

    private void hideReminderView() {
        ViewUtil.hideView(mReminderView);
    }

    private void showReminderView() {
        ViewUtil.showView(mReminderView);
    }

    private void hideRemindersList() {
        ViewUtil.hideView(mRemindersListView);
    }

    private void showRemindersList() {
        ViewUtil.showView(mRemindersListView);
    }

    private void hideSelectTypeView() {
        ViewUtil.hideView(mRemindersListView);
    }

    private void showSelectTypeView() {
        ViewUtil.showView(mRemindersListView);
    }

    private void showLocationForm() {
        ViewUtil.showView(mLocationForm);
    }

    private void hideLocationForm() {
        ViewUtil.hideView(mLocationForm);
    }

    private void showTargetUserForm() {
        ViewUtil.showView(mTargetUserForm);
    }

    private void hideTargetUserForm() {
        ViewUtil.hideView(mTargetUserForm);
    }

    private void showCreateReminderButton() {
        ViewUtil.showView(mCreateReminderButton);
    }

    private void hideCreateReminderButton() {
        ViewUtil.hideView(mCreateReminderButton);
    }

    private void showSaveChangesButton() {
        ViewUtil.showView(mSaveChangesButton);
    }

    private void hideSaveChangesButton() {
        ViewUtil.hideView(mSaveChangesButton);
    }

    private void showNoRemindersView() {
        ViewUtil.showView(mNoRemindersView);
    }

    private void hideNoRemindersView() {
        ViewUtil.hideView(mNoRemindersView);
    }

    private void init() {
        hideReminderView();
        showRemindersList();

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
        mRemindersList = (ArrayList<Reminder>) reminders;

        if (mReminderListAdapter == null) {
            // First time we populate the list
            mReminderListAdapter = new ReminderListArrayAdapter(this, (ArrayList<Reminder>) reminders);
            mRemindersListView.setAdapter(mReminderListAdapter);
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
                        hideReminderView();
                        showRemindersList();
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


}