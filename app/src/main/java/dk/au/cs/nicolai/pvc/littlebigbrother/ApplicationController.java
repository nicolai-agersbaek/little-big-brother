package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by nicolai on 9/22/15.
 */
public class ApplicationController extends Application
        implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static boolean mRequestingLocationUpdates = false;
    private static GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private boolean mLocationUpdateRequestStarted;

    // Handler for received Intents for the GOOGLE_API_CLIENT_CONNECTED event
    private BroadcastReceiver mOnGoogleApiClientConnectedReceiver;

    // Handler for received Intents for the USER_LOGIN_SUCCEEDED event
    private BroadcastReceiver mOnUserLoginSuccessReceiver;

    private boolean googleApiClientConnected = false;
    private boolean userLoginSucceeded = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Application-wide initialization here

        makeOnGoogleApiClientConnectedReceiver();

        // Register mOnGoogleApiClientConnectedReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mOnGoogleApiClientConnectedReceiver,
                new IntentFilter(LittleBigBrother.Events.GOOGLE_API_CLIENT_CONNECTED));

        makeOnUserLoginSuccessReceiver();

        // Register mOnUserLoginSuccessReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mOnUserLoginSuccessReceiver,
                new IntentFilter(LittleBigBrother.Events.USER_LOGIN_SUCCESS));

        createLocationRequest();

        // Initialize Parse database
        LittleBigBrother.initParseDB(this);

        createGoogleApiClientObject();
        mGoogleApiClient.connect();
    }

    public static boolean isRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    public static void setRequestingLocationUpdates(boolean requestingLocationUpdates) {
        mRequestingLocationUpdates = requestingLocationUpdates;
    }

    private void makeOnGoogleApiClientConnectedReceiver() {
        mOnGoogleApiClientConnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                googleApiClientConnected = true;
                Log.d(LittleBigBrother.Constants.LOG, "ApplicationController: OnConnected received.");
                startLocationUpdates();
            }
        };
    }

    private void makeOnUserLoginSuccessReceiver() {
        mOnUserLoginSuccessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                userLoginSucceeded = true;
                Log.d(LittleBigBrother.Constants.LOG, "ApplicationController: LoginSuccess received.");
                startLocationUpdates();
            }
        };
    }


    private void createGoogleApiClientObject() {
        // Create GoogleApiClient object for use with the UpdateUserPositionService.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApiIfAvailable(LocationServices.API)
                .build();
    }

    public static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(LittleBigBrother.Constants.LOG, "ApplicationController: GoogleApiClient connected successfully.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LittleBigBrother.Constants.LOG, "ApplicationController: GoogleApiClient: onConnectionSuspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LittleBigBrother.Constants.LOG, "ApplicationController: GoogleApiClient: onConnectionFailed.");
    }



    protected void startLocationUpdates() {
        if (userLoginSucceeded & googleApiClientConnected & !mLocationUpdateRequestStarted) {
            Log.d(LittleBigBrother.Constants.LOG, "Starting location updates.");

            mRequestingLocationUpdates = true;

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

            mLocationUpdateRequestStarted = true;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LittleBigBrother.UPDATE_USER_POSITION_INTERVAL);
        mLocationRequest.setFastestInterval(LittleBigBrother.UPDATE_POSITION_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        saveUserLocationInBackground(location);
    }

    public void saveUserLocationInBackground(Location location) {
        Log.d(LittleBigBrother.Constants.LOG, "Pushing user location to database.");

        // Push information to database
        ParseObject user = ParseUser.getCurrentUser();

        ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        user.put(LittleBigBrother.Constants.DB.USER_POSITION_ATTRIBUTE, point);
        user.saveInBackground();
    }
}
