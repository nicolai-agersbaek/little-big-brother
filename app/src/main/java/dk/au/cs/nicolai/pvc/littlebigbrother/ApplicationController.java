package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dk.au.cs.nicolai.pvc.littlebigbrother.database.UserListResultCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ApplicationDrawerItem;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ApplicationDrawerItemOnClickedCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.GoogleApiClientFactory;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;

/**
 * Created by nicolai on 9/22/15.
 */
public final class ApplicationController extends Application
        implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private boolean mRequestingLocationUpdates;
    private boolean mLocationUpdateRequestStarted;

    private boolean isGoogleApiClientConnected = false;
    private boolean hasUserLoginSucceeded = false;

    public interface DrawerItem {
        // Map
        ApplicationDrawerItem MAP = ApplicationDrawerItem.MAP(new ApplicationDrawerItemOnClickedCallback() {
            @Override
            public void onClicked(Activity activity) {
                Intent intent = new Intent(activity, MapsActivity.class);
                activity.startActivity(intent);
            }
        });

        // WiFi
        ApplicationDrawerItem WIFI = ApplicationDrawerItem.WIFI(new ApplicationDrawerItemOnClickedCallback() {
            @Override
            public void onClicked(Activity activity) {
                Intent intent = new Intent(activity, WifiActivity.class);
                activity.startActivity(intent);
            }
        });

        // Reminders
        ApplicationDrawerItem REMINDERS = ApplicationDrawerItem.REMINDERS(new ApplicationDrawerItemOnClickedCallback() {
            @Override
            public void onClicked(Activity activity) {
                //throw new UnsupportedOperationException("RemindersActivity not yet implemented.");
                //Intent intent = new Intent(activity, MapsActivity.class);
                //activity.startActivity(intent);
            }
        });

        // Sign out
        ApplicationDrawerItem LOGOUT = ApplicationDrawerItem.LOGOUT(new ApplicationDrawerItemOnClickedCallback() {
            @Override
            public void onClicked(final Activity activity) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // Logged out successfully
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                        } else {
                            Log.exception(activity, "Logout failed", e);
                        }
                    }
                });
            }
        });
    }

    public interface DrawerPosition {
        int MAP = 0;
        int WIFI = 1;
        int REMINDERS = 2;
        int LOGOUT = 3;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Application-wide initialization here

        // Location request
        mRequestingLocationUpdates = LittleBigBrother.Settings.REQUEST_LOCATION_UPDATES;
        makeLocationRequestWithApplicationDefaultSettings();

        // Initialize Parse database
        LittleBigBrother.initParseDB(this);

        // Create GoogleApiClient object for use with the UpdateUserPositionService.
        mGoogleApiClient = GoogleApiClientFactory.LocationServices.build(this);
        mGoogleApiClient.connect();
    }

    public boolean isRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    protected boolean hasUserLoginSucceeded() {
        return hasUserLoginSucceeded;
    }

    protected boolean isGoogleApiClientConnected() {
        return isGoogleApiClientConnected;
    }

    public void userLoginSuccessNotification() {
        hasUserLoginSucceeded = true;
        startLocationUpdates();
    }

    public static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    private void startLocationUpdates() {
        if (hasUserLoginSucceeded()
                & isGoogleApiClientConnected()
                & mRequestingLocationUpdates
                & !mLocationUpdateRequestStarted) {
            Log.info(this, "Starting location updates.");

            // TODO: Use the PendingResult<Status> object returned to check isSuccess() or set a ResultCallback listener
            //PendingResult<Status> pendingResult =  LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    getGoogleApiClient(), mLocationRequest, this);

            mLocationUpdateRequestStarted = true;
        }
    }

    private void makeLocationRequestWithApplicationDefaultSettings() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LittleBigBrother.UPDATE_USER_POSITION_INTERVAL);
        mLocationRequest.setFastestInterval(LittleBigBrother.UPDATE_POSITION_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        saveUserLocationInBackground(location);
    }

    public void saveUserLocationInBackground(android.location.Location location) {
        Log.info(this, "Pushing user location to database.");

        // Push information to database
        ParseObject user = ParseUser.getCurrentUser();

        if (user != null) {
            ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            user.put(LittleBigBrother.Constants.DB.USER_POSITION_ATTRIBUTE, point);
            user.saveInBackground();
        }
    }

    // TODO: Finish centralization of options menu behavior
    public static class OptionsMenuHandler {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.debug(this, "GoogleApiClient connected successfully.");
        isGoogleApiClientConnected = true;

        Intent intent = new Intent(LittleBigBrother.Events.GOOGLE_API_CLIENT_CONNECTED);

        Log.debug(this, "Local broadcast sent.");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.error(this, "GoogleApiClient: onConnectionSuspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.error(this, "GoogleApiClient: onConnectionFailed.");
    }

    public static void getUserListFromDatabase(final UserListResultCallback callback) {
        // Get list of users from database
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    callback.success(userList);
                } else {
                    Log.exception(this, e);
                    callback.error(e);
                }
            }
        });
    }

    public static ArrayList<CharSequence> getUsernamesFromParseUserList(List<ParseUser> users) {
        ArrayList<CharSequence> usernames = new ArrayList<>();

        for (ParseUser user :
                users) {
            usernames.add(user.getUsername());
        }

        return usernames;
    }
}