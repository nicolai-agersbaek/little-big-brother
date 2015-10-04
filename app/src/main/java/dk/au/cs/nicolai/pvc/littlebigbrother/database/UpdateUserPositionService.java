package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import dk.au.cs.nicolai.pvc.littlebigbrother.ApplicationController;
import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;

// TODO: IntentService: Stops the service after all start requests have been handled - different implementation?
public class UpdateUserPositionService extends IntentService implements LocationListener {

    private LocationRequest mLocationRequest;
    private boolean mLocationUpdateRequestStarted;

    // Handler for received Intents for the GOOGLE_API_CLIENT_CONNECTED event
    private BroadcastReceiver mOnConnectedReceiver;

    public UpdateUserPositionService() {
        super(LittleBigBrother.UPDATE_USER_POSITION_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO: Rewrite this ServiceIntent
        boolean alwaysTrue = true;

        // Check to see if we are already requesting location updates
        //if (!ApplicationController.isRequestingLocationUpdates()) {
        if (!alwaysTrue) {
            // TODO: Can we start building the LocationRequest before OnConnected event?

            createLocationRequest();

            startLocationUpdates();

            mOnConnectedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Extract data included in the Intent
                    startLocationUpdates();
                }
            };

            // Register mOnConnectedReceiver to receive messages.
            LocalBroadcastManager.getInstance(this).registerReceiver(mOnConnectedReceiver,
                    new IntentFilter(LittleBigBrother.Events.GOOGLE_API_CLIENT_CONNECTED));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Does not allow binding
        return null;
    }

    protected void startLocationUpdates() {
        if (!mLocationUpdateRequestStarted) {
            Log.d(LittleBigBrother.Constants.LOG, "Starting location updates.");

            //ApplicationController.setRequestingLocationUpdates(true);

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    ApplicationController.getGoogleApiClient(), mLocationRequest, this);

            mLocationUpdateRequestStarted = true;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LittleBigBrother.UPDATE_USER_POSITION_INTERVAL * 1000);
        mLocationRequest.setFastestInterval(LittleBigBrother.UPDATE_POSITION_FASTEST_INTERVAL * 1000);
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
