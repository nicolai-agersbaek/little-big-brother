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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.materialdrawer.Drawer;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
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
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.OnReminderDataChangedCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.OnReminderListItemLongClickCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.OnReminderRadiusChangedCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.OnReminderRadiusChangedListener;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.ReminderCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.ReminderListWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.SelectLocationWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder.SelectTypeWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ActivityDrawer;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ReminderListArrayAdapter;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.SimpleDateTime;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ViewUtil;

public class RemindersActivity extends AppCompatActivity implements OnReminderRadiusChangedListener {

    private WidgetCollection mReminderViewWidgets;
    private WidgetCollection mWidgets;
    private HashSet<View> mViews;

    private enum VIEW {
        LIST, REMINDER
    }

    private VIEW currentView;
    private boolean existingReminder;

    // Reminder information
    private Reminder mReminder = new Reminder();

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

    // Data monitor
    private OnReminderDataChangedCallback onReminderDataChangedCallback;

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


        onReminderDataChangedCallback = new OnReminderDataChangedCallback() {
            @Override
            public void onLocationChanged(LatLng location) {
                ((Reminder.Location) mReminder).setPosition(new ParseGeoPoint(location.latitude, location.longitude));
            }

            @Override
            public void onRadiusChanged(int radius) {
                ((ReminderWithRadius) mReminder).setRadius(radius);
            }

            @Override
            public void onTargetUserChanged(ParseUser targetUser) {
                ((Reminder.TargetUser) mReminder).setTargetUser(targetUser);
            }

            @Override
            public void onDateChanged(SimpleDateTime simpleDateTime) {
                if (mReminder.type() == ReminderType.DATE_TIME) {
                    ((Reminder.DateTime) mReminder).setDate(simpleDateTime);
                } else {
                    mReminder.setExpires(simpleDateTime);
                }
            }
        };


        mProgressWidget = newWidget(new ProgressWidget(this,
                R.id.reminderProgress,
                R.id.reminderProgressTextView));

        mSelectRadiusWidget = newReminderViewWidget(new SeekBarWidget(this,
                R.id.reminderSelectRadiusForm,
                R.id.reminderRadiusSeekBar,
                R.id.reminderRadiusValueTextView));
        mSelectRadiusWidget.setValues(getResources().getIntArray(R.array.reminderRadiusSeekBarValues));
        mSelectRadiusWidget.setDefaultValue(getResources().getInteger(R.integer.reminderRadiusSeekBarDefaultValue));
        mSelectRadiusWidget.setOnReminderRadiusChangedCallback(new OnReminderRadiusChangedCallback() {
            @Override
            public void onRadiusChanged(int radius) {
                mLocationPicker.onRadiusChanged(radius);
            }
        });
        mSelectRadiusWidget.setOnReminderDataChangedCallback(onReminderDataChangedCallback);

        mLocationPicker = new LocationPicker(this,
                (MapFragment) getFragmentManager().findFragmentById(R.id.reminderMapFragment),
                R.id.reminderMapContainer,
                R.id.reminderMapConfirmLocationButton);
        mLocationPicker.onRadiusChanged(mSelectRadiusWidget.getValue());

        mSelectLocationWidget = newReminderViewWidget(new SelectLocationWidget(this,
                R.id.reminderLocationForm,
                R.id.reminderSelectLocationButton,
                mLocationPicker));
        mSelectLocationWidget.setOnReminderDataChangedCallback(onReminderDataChangedCallback);

        mSelectDateTimeWidget = newReminderViewWidget(new SelectDateTimeWidget(this,
                R.id.reminderDateTimeForm,
                R.id.reminderSelectDateButton,
                R.id.reminderSelectTimeButton));
        mSelectDateTimeWidget.setOnReminderDataChangedCallback(onReminderDataChangedCallback);

        mSelectExpirationDateTimeWidget = newReminderViewWidget(new SelectDateTimeWidget(this,
                R.id.reminderExpirationForm,
                R.id.reminderExpirationSelectDateButton,
                R.id.reminderExpirationSelectTimeButton));
        mSelectExpirationDateTimeWidget.setResetButton(R.id.reminderExpirationResetDateTimeButton);
        mSelectExpirationDateTimeWidget.setOnReminderDataChangedCallback(onReminderDataChangedCallback);

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

            @Override
            public void delete(Reminder reminder) {
                deleteReminder(reminder);
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

        initRemindersList();

        //test();

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

    @Override
    public void onRadiusChanged(int radius) {
        mLocationPicker.onRadiusChanged(radius);
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

        mReminder.setTitle(mReminderTitleView.getText().toString());
        mReminder.setDescription(mReminderDescriptionView.getText().toString());
    }

    // TARGET USER FORM

    private void initTargetUserForm() {
        mTargetUserForm = getViewById(R.id.reminderTargetUserForm);

        mTargetUserIconView = (IconicsImageView) findViewById(R.id.reminderTargetUserIcon);
        mSelectTargetUserSpinner = (Spinner) findViewById(R.id.reminderSelectTargetUserSpinner);

        mSelectTargetUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.debug(this, "" + parent.getItemAtPosition(position));

                // Save to mReminder
                //((Reminder.TargetUser) mReminder).setTargetUser();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // FINALIZE REMINDER FORM
    private void saveChangesButtonClicked() {
        mProgressWidget.setLabel(SAVE_CHANGES_PROGRESS_TEXT);
        saveChanges();
    }

    private void createReminderButtonClicked() {
        mProgressWidget.setLabel(CREATE_REMINDER_PROGRESS_TEXT);
        createReminder();
    }

    /**
     * <p>Cancel the changes made to the currently active Reminder.</p>
     *
     * <p>If any changes were made, show the alert dialog.</p>
     * TODO: Detect when changes are made?
     */
    private void cancelButtonClicked() {
        Log.debug(this, "CancelButton clicked.");

        if (!mReminder.isFresh()) {
            showDiscardChangesAlertDialog();
        }
    }



    private <T extends Reminder> void setReminderFormFieldsFromReminder(T reminder) {
        setCurrentReminder(reminder);

        mReminderTitleView.setText(reminder.getTitle());
        mReminderDescriptionView.setText(reminder.getDescription());

        mSelectExpirationDateTimeWidget.setDateTime(reminder);

        switch (reminder.type()) {
            case LOCATION:
                setLocationFormFieldsFromReminder((Reminder.Location) reminder);
                break;
            case TARGET_USER:
                setTargetUserFormFieldsFromReminder((Reminder.TargetUser) reminder);
                break;
            case DATE_TIME:
                mSelectDateTimeWidget.setDateTime(reminder);
                break;
        }
    }

    private void setRadiusFormFieldsFromReminder(ReminderWithRadius reminder) {
        mSelectRadiusWidget.setValue(reminder);
    }

    private void setLocationFormFieldsFromReminder(Reminder.Location reminder) {
        setRadiusFormFieldsFromReminder(reminder);

        // TODO: Set location from existing reminder
        //mLocationPicker.setLocation(reminder.getPosition());
    }

    private void setTargetUserFormFieldsFromReminder(Reminder.TargetUser reminder) {
        setRadiusFormFieldsFromReminder(reminder);

        // TODO: Set target user from reminder
    }

    /**
     * <p>Initialize ReminderView for creating a new reminder.</p>
     */
    private void initForNewReminder() {
        clearReminderFormFields();

        setToNewReminder();

        mReminderView.clearFocus();
    }

    private void initForExistingReminder(Reminder reminder) {
        setToEditReminder(reminder);

        // Set form fields from reminder object
        setReminderFormFieldsFromReminder(reminder);
    }

    private void clearReminderFormFields() {
        mReminderTitleView.setText(null);
        mReminderDescriptionView.setText(null);

        mSelectLocationWidget.reset();
        mSelectDateTimeWidget.reset();
        mSelectExpirationDateTimeWidget.reset();
    }



    private void hideAll() {
        for (View view :
                mViews) {
            ViewUtil.hideView(view);
        }
        mWidgets.hideAll();
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
            mProgressWidget.setLabel("Retrieving reminders.");
            mProgressWidget.show();

            // Get list of reminders from database
            ParseQuery<Reminder> query = ParseQuery.getQuery("Reminders");
            query.whereEqualTo("owner", user);
            query.findInBackground(new FindCallback<Reminder>() {
                public void done(final List<Reminder> reminders, ParseException e) {
                    if (e == null) {
                        Log.debug(this, "Retrieved reminders from server. Result count: " + reminders.size());
                        if (!reminders.isEmpty()) {
                            setRemindersFromDatabase(reminders);
                            pinRemindersInBackground(reminders);

                            setToReminderList();
                            mProgressWidget.hide();
                        } else {
                            // Get Reminders from local datastore
                            getRemindersFromLocalStore(user);
                        }
                    } else {
                        Log.error(this, "Error: " + e.getMessage());

                        setToReminderList();
                        mProgressWidget.hide();
                    }
                }
            });
        } else {
            Log.error(this, "User not logged in.");
        }
    }

    private void setRemindersFromDatabase(List<Reminder> reminders) {
        mReminderListWidget.set(reminders);
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
                        setRemindersFromDatabase(reminders);
                    }

                    setToReminderList();
                    mProgressWidget.hide();
                } else {
                    Log.exception(this, e);

                    setToReminderList();
                    mProgressWidget.hide();
                }
            }
        });
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
            newReminder();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setToReminderList() {
        hideAll();
        existingReminder = false;

        mReminderListWidget.show();
    }

    private void setToReminder() {
        hideAll();

        ViewUtil.showView(mReminderView);
    }

    private void setToNewReminder() {
        setToReminder();

        ViewUtil.showView(mReminderTitleAndDescriptionContainer);
        mSelectTypeWidget.show();
    }

    private void setToEditReminder(Reminder reminder) {
        setToReminder();

        ViewUtil.showView(mReminderTitleAndDescriptionContainer);

        setForSelectType(reminder.type());
    }

    private void setForAfterSelectType() {
        ViewUtil.showView(mReminderDetailsContainer);

        mSelectTypeWidget.hide();
        mSelectExpirationDateTimeWidget.show();
        mFinalizeReminderWidget.show();
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
        mSelectRadiusWidget.show();
    }

    private void setForSelectTargetUserType() {
        ViewUtil.showView(mTargetUserForm);
        mSelectRadiusWidget.show();
    }

    private void setForSelectDateTimeType() {
        mSelectDateTimeWidget.show();
    }

    private void saveChanges() {
        if (mReminder.type() == ReminderType.DATE_TIME) {
            mReminder.setExpires(mSelectExpirationDateTimeWidget.getDateTime());
        }

        if (!mReminder.valid()) {
            Log.error(this, "Attempting save invalid Reminder.");
            return;
        }

        Log.debug(this, "Saving changes to Reminder: " + mReminder.getTitle());

        mProgressWidget.setLabel(SAVE_CHANGES_PROGRESS_TEXT);
        mProgressWidget.show();

        mReminder.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    mReminder = new Reminder();
                    setToReminderList();
                    mProgressWidget.hide();
                } else {
                    Log.exception(this, e);
                }
            }
        });
    }

    private void newReminder() {
        if (!mReminder.isFresh() && currentView == VIEW.REMINDER) {
            showDiscardChangesAlertDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    existingReminder = false;
                    initForNewReminder();
                }
            });
        } else {
            existingReminder = false;
            initForNewReminder();
        }
    }

    private void createReminder() {
        if (mReminder.type() == ReminderType.DATE_TIME) {
            mReminder.setExpires(mSelectExpirationDateTimeWidget.getDateTime());
        }

        if (!mReminder.valid()) {
            Log.error(this, "Attempting save invalid Reminder.");
            return;
        }

        Log.debug(this, "Creating new Reminder: " + mReminder.getTitle());

        mReminderListWidget.add(mReminder);

        mReminder.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {

                } else {
                    Log.exception(this, e);
                }
                mReminder = new Reminder();
                setToReminderList();
                mProgressWidget.hide();
            }
        });
    }

    private void editReminder(Reminder reminder) {
        existingReminder = true;
        initForExistingReminder(reminder);
    }

    private void init() {
        currentView = VIEW.LIST;

        mReminderListWidget.show();

        mProgressWidget.setLabel(R.string.progress_gettingReminders);
        mProgressWidget.hide();
    }

    private void deleteReminder(Reminder reminder) {
        Log.debug(this, "Deleting reminder: " + reminder.getTitle());

        reminder.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.debug(this, "Deleted reminder.");
                } else {
                    Log.exception(this, e);
                }
            }
        });
    }
}