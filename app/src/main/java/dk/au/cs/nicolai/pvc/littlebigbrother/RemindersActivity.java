package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dk.au.cs.nicolai.pvc.littlebigbrother.database.Reminder;
import dk.au.cs.nicolai.pvc.littlebigbrother.database.ReminderType;
import dk.au.cs.nicolai.pvc.littlebigbrother.database.ReminderWithRadius;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ActivityDrawer;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ReminderListArrayAdapter;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Util;

public class RemindersActivity extends AppCompatActivity {

    private final Context CONTEXT = this;

    // Reminder information
    private Reminder mOriginialReminder;
    private Reminder mCurrentReminder;

    // Drawer
    private Drawer mDrawer;

    // Progress
    private View mProgressView;
    private TextView mProgressTextView;
    private ProgressBar mProgressBar;

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

    // DateTime
    private View mDateTimeForm;
    private IconicsImageView mDateTimeIconView;
    private Button mSelectDateButton;
    private Button mSelectTimeButton;

    // SelectRadius
    private View mSelectRadiusForm;
    private TextView mRadiusValueTextView;
    private SeekBar mSelectRadiusSeekBar;

    private int[] RADIUS_SEEKBAR_VALUES;
    private int RADIUS_SEEKBAR_DEFAULT_VALUE;
    private int RADIUS_SEEKBAR_MAX_PROGRESS;

    // Expiration
    private View mExpirationForm;
    private Button mSelectExpirationDateButton;
    private Button mResetExpirationDateButton;

    // Finalize
    private View mFinalizeReminderButtonsContainer;
    private Button mSaveChangesButton;
    private Button mCreateReminderButton;
    private Button mCancelButton;

    private String SAVE_CHANGES_PROGRESS_TEXT;
    private String CREATE_REMINDER_PROGRESS_TEXT;


    // Reminders list
    private ListView mRemindersListView;
    private ReminderListArrayAdapter mReminderListAdapter;
    private ArrayList<Reminder> mRemindersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        mDrawer = ActivityDrawer.build(this);

        if (mDrawer != null) {
            mDrawer.setSelection(ApplicationController.DrawerPosition.REMINDERS);
        }

        // Progress view
        initProgressView();

        // Reminder view: Edit/Create
        mReminderView = findViewById(R.id.reminderView);

        mReminderTitleView = (EditText) findViewById(R.id.reminderTitleEdit);
        mReminderDescriptionView = (EditText) findViewById(R.id.reminderDescriptionEdit);


        // Reminder type selection
        mSelectTypeView = findViewById(R.id.reminderSelectTypeView);
        initSelectTypeButtons();

        initLocationForm();
        initTargetUserForm();
        initDateTimeForm();

        // Reminder details
        mReminderDetailsContainer = findViewById(R.id.reminderDetailsContainer);

        initExpirationForm();

        initFinalizeReminderForm();

        // Reminders list
        mRemindersListView = (ListView) findViewById(R.id.remindersListView);

        //initRemindersList();
    }

    // TODO: Support for clone() in order to enable detection of changes.
    private void setOriginalReminder(Reminder reminder) {
        //mOriginialReminder =
    }

    private void setCurrentReminder(Reminder reminder) {
        mCurrentReminder = reminder;
    }

    // PROGRESS VIEW

    private void initProgressView() {
        mProgressView = findViewById(R.id.reminderProgress);

        mProgressTextView = (TextView) findViewById(R.id.reminderProgressTextView);
        mProgressBar = (ProgressBar) findViewById(R.id.reminderProgressBar);
    }

    private void animatedShowProgress() {
        ApplicationController.animatedShowView(this, true, mProgressView);
    }

    private void animatedHideProgress() {
        ApplicationController.animatedShowView(this, false, mProgressView);
    }

    private void setProgressViewText(String text) {
        mProgressTextView.setText(text);
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

        initExpirationForm();

        ApplicationController.hideView(mSelectTypeView);
        ApplicationController.showView(mReminderDetailsContainer);

        switch (type) {
            case LOCATION:
                enableRadiusSelect();
                locationTypeSelected();
                break;
            case TARGET_USER:
                enableRadiusSelect();
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
    }

    private void targetUserTypeSelected() {
        initTargetUserForm();

        showTargetUserForm();
    }

    private void dateTimeTypeSelected() {
        initDateTimeForm();

        showDateTimeForm();
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

    private void initDateTimeForm() {
        mDateTimeForm = findViewById(R.id.reminderDateTimeForm);

        mDateTimeIconView = (IconicsImageView) findViewById(R.id.reminderDateTimeIcon);
        mSelectDateButton = (Button) findViewById(R.id.reminderSelectDateButton);
        mSelectTimeButton = (Button) findViewById(R.id.reminderSelectTimeButton);

        setDateTimeButtonsOnClickListeners();
    }

    // RADIUS SELECT

    private void enableRadiusSelect() {
        initRadiusSelectForm();
        showRadiusSelectForm();
    }

    private void showRadiusSelectForm() {
        ApplicationController.showView(mSelectRadiusForm);
    }

    private void initRadiusSelectForm() {
        if (mSelectRadiusForm == null) {
            mSelectRadiusForm = findViewById(R.id.reminderSelectRadiusForm);

            mRadiusValueTextView = (TextView) findViewById(R.id.reminderRadiusValueTextView);
            mSelectRadiusSeekBar = (SeekBar) findViewById(R.id.reminderRadiusSeekBar);

            initRadiusSeekBarValues();

            mRadiusValueTextView.setText(LittleBigBrother.distanceAsString(RADIUS_SEEKBAR_DEFAULT_VALUE));

            mSelectRadiusSeekBar.setMax(RADIUS_SEEKBAR_MAX_PROGRESS);
            setRadiusSeekBarProgressFromRadiusValue(RADIUS_SEEKBAR_DEFAULT_VALUE);

            setRadiusSeekBarOnChangeListener();
        }
    }

    private void initRadiusSeekBarValues() {
        RADIUS_SEEKBAR_VALUES = getResources().getIntArray(R.array.reminderRadiusSeekBarValues);
        RADIUS_SEEKBAR_DEFAULT_VALUE = getResources().getInteger(R.integer.reminderRadiusSeekBarDefaultValue);
        RADIUS_SEEKBAR_MAX_PROGRESS = RADIUS_SEEKBAR_VALUES.length - 1;
    }

    private void setRadiusSeekBarOnChangeListener() {
        mSelectRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        mRadiusValueTextView.setText(radius);
    }

    private int getCurrentRadius() {
        int currentProgress = mSelectRadiusSeekBar.getProgress();
        return RADIUS_SEEKBAR_VALUES[currentProgress];
    }

    private int getRadiusSeekBarProgressFromRadiusValue(int radius) {
        int progress = 0;

        for (int i = 0; i <= RADIUS_SEEKBAR_MAX_PROGRESS; i++) {
            if (RADIUS_SEEKBAR_VALUES[i] == radius) {
                progress = i;
            }
        }

        return progress;
    }

    private void setRadiusSeekBarProgressFromRadiusValue(int radius) {
        int progress = getRadiusSeekBarProgressFromRadiusValue(radius);

        mSelectRadiusSeekBar.setProgress(progress);
    }

    // EXPIRATION FORM

    private void initExpirationForm() {
        mExpirationForm = findViewById(R.id.reminderExpirationForm);

        mSelectExpirationDateButton = (Button) findViewById(R.id.reminderExpirationSelectDateButton);
        mResetExpirationDateButton = (Button) findViewById(R.id.reminderExpirationResetDateButton);

        setExpirationDateButtonOnClickListeners();
    }

    private void setExpirationDateButtonOnClickListeners() {
        mSelectExpirationDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateSelect(v);
            }
        });

        mResetExpirationDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetExpirationDate();
            }
        });
    }

    private void showResetExpirationDateButton() {
        ApplicationController.showView(mResetExpirationDateButton);
    }

    private void hideResetExpirationDateButton() {
        ApplicationController.hideView(mResetExpirationDateButton);
    }

    private void resetExpirationDate() {
        mSelectExpirationDateButton.setText(getString(R.string.prompt_selectDate));
        hideResetExpirationDateButton();
    }

    // DATE FORM

    private void setDateTimeButtonsOnClickListeners() {
        mSelectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateSelect(v);
            }
        });

        mSelectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSelect(v);
            }
        });
    }

    private void showDateSelect(View parentView) {
        Log.debug(this, "Show date select.");
    }

    private void showTimeSelect(View parentView) {
        Log.debug(this, "Show time select.");
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
        Log.debug(this, "Save changes button clicked.");

        setProgressViewText(SAVE_CHANGES_PROGRESS_TEXT);

    }

    private void saveChanges() {
        Log.debug(this, "Saving changes to Reminder: " + mCurrentReminder.getTitle());
    }

    private void createReminderButtonClicked() {
        Log.debug(this, "Create reminder button clicked.");

        setProgressViewText(CREATE_REMINDER_PROGRESS_TEXT);
    }

    private void createReminder() {
        Log.debug(this, "Creating reminder.");
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
                showDateTimeForm();
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
            Calendar cal = reminder.getExpires();

            Util.setDateText(mSelectExpirationDateButton, cal);
        }

        showResetExpirationDateButton();
    }

    private void setRadiusFormFieldsFromReminder(ReminderWithRadius reminder) {
        setRadiusSeekBarProgressFromRadiusValue(reminder.getRadius());
    }

    private void setLocationFormFieldsFromReminder(Reminder.Location reminder) {
        setRadiusFormFieldsFromReminder(reminder);

        Util.setLocationText(mSelectLocationButton, reminder.getPosition());
    }

    private void setTargetUserFormFieldsFromReminder(Reminder.TargetUser reminder) {
        setRadiusFormFieldsFromReminder(reminder);


    }

    private void setDateTimeFormFieldsFromReminder(Reminder.DateTime reminder) {
        Calendar cal = reminder.getDate();

        Util.setDateText(mSelectDateButton, cal);
        Util.setTimeText(mSelectTimeButton, cal);
    }

    /**
     * <p>Initialize ReminderView for creating a new reminder.</p>
     */
    private void initForNewReminder() {
        clearReminderFormFields();

        hideLocationForm();
        hideTargetUserForm();
        hideDateTimeForm();

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

    }

    private void deleteReminder(Reminder reminder) {
        Log.debug(this, "Deleting reminder: " + reminder.getTitle());
    }

    private void hideReminderView() {
        ApplicationController.hideView(mReminderView);
    }

    private void showReminderView() {
        ApplicationController.showView(mReminderView);
    }

    private void hideRemindersList() {
        ApplicationController.hideView(mRemindersListView);
    }

    private void showRemindersList() {
        ApplicationController.showView(mRemindersListView);
    }

    private void hideSelectTypeView() {
        ApplicationController.hideView(mRemindersListView);
    }

    private void showSelectTypeView() {
        ApplicationController.showView(mRemindersListView);
    }

    private void showLocationForm() {
        ApplicationController.showView(mLocationForm);
    }

    private void hideLocationForm() {
        ApplicationController.hideView(mLocationForm);
    }

    private void showTargetUserForm() {
        ApplicationController.showView(mTargetUserForm);
    }

    private void hideTargetUserForm() {
        ApplicationController.hideView(mTargetUserForm);
    }

    private void showDateTimeForm() {
        ApplicationController.showView(mDateTimeForm);
    }

    private void hideDateTimeForm() {
        ApplicationController.hideView(mDateTimeForm);
    }

    private void showCreateReminderButton() {
        ApplicationController.showView(mCreateReminderButton);
    }

    private void hideCreateReminderButton() {
        ApplicationController.hideView(mCreateReminderButton);
    }

    private void showSaveChangesButton() {
        ApplicationController.showView(mSaveChangesButton);
    }

    private void hideSaveChangesButton() {
        ApplicationController.hideView(mSaveChangesButton);
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
