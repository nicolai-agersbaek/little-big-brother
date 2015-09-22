package dk.au.cs.nicolai.pvc.littlebigbrother;

import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;

import android.app.Application;
import android.location.Location;
import android.os.Bundle;

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

import dk.au.cs.nicolai.pvc.littlebigbrother.util.GoogleApiClientFactory;

/**
 * Created by nicolai on 9/22/15.
 */
public class ApplicationController extends Application
        implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static boolean mRequestingLocationUpdates = false;
    private static GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private boolean mLocationUpdateRequestStarted;

    private boolean googleApiClientConnected = false;
    private boolean userLoginSucceeded = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Application-wide initialization here

        makeLocationRequest();

        // Initialize Parse database
        LittleBigBrother.initParseDB(this);

        // Create GoogleApiClient object for use with the UpdateUserPositionService.
        mGoogleApiClient = GoogleApiClientFactory.LocationServices.build(this);
        mGoogleApiClient.connect();
    }

    public static boolean isRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    public static void setRequestingLocationUpdates(boolean requestingLocationUpdates) {
        mRequestingLocationUpdates = requestingLocationUpdates;
    }

    public static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.debug(this, "GoogleApiClient connected successfully.");
        googleApiClientConnected = true;
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.debug(this, "GoogleApiClient: onConnectionSuspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.debug(this, "GoogleApiClient: onConnectionFailed.");
    }

    public void userLoginSuccessNotification() {
        userLoginSucceeded = true;
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (userLoginSucceeded & googleApiClientConnected & !mLocationUpdateRequestStarted) {
            Log.debug(this, "Starting location updates.");

            mRequestingLocationUpdates = true;

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

            mLocationUpdateRequestStarted = true;
        }
    }

    protected void makeLocationRequest() {
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
        Log.debug(this, "Pushing user location to database.");

        // Push information to database
        ParseObject user = ParseUser.getCurrentUser();

        ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        user.put(LittleBigBrother.Constants.DB.USER_POSITION_ATTRIBUTE, point);
        user.saveInBackground();
    }
}
