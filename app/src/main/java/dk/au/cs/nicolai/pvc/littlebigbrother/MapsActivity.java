package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

// TODO: Consider using Map padding when using the Maps activity to edit/create Reminder locations.
// TODO: Handle when user minimizes application (think it's the onStop() event callback)
public class MapsActivity extends FragmentActivity
    implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;

    private CameraPosition currentCameraPosition;
    private Map<String, Marker> userMapMarkers = new HashMap<>();
    private List<Marker> reminderMapMarkers = new ArrayList<>();
    private Timer updatePositionsTimer;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // TODO: Get map camera position from Intent, if provided (from Reminders activities)

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApiIfAvailable(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startUpdateUserPositionsTimer();
    }

    /**
     * <p>This is where we can add markers or lines, add listeners or move the camera.<p/>
     *
     * <p>This should only be called once and when we are sure that {@link #mMap} is not null.</p>
     */
    @Override
    public void onMapReady(GoogleMap map) {
        // TODO: Get previous camera position from state.
        // TODO: Get map markers from state.

        // Assign the passed GoogleMap instance to a private field
        mMap = map;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUpdateUserPositionsTimer();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * <p>Called after successfully connecting to Google Play services.</p>
     *
     * @param connectionHint
     */
    public void onConnected(Bundle connectionHint) {
        Log.e(LittleBigBrother.Constants.LOG, "MapsActivity: GoogleApiClient: onConnected.");

        // Get last known user Location object from API.
        Location userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng userPosition = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

        // Create CameraPosition object from userPosition, using default zoom level
        currentCameraPosition = new CameraPosition.Builder()
                .target(userPosition)
                .zoom(LittleBigBrother.DEFAULT_ZOOM_LEVEL)
                .build();

        String username = ParseUser.getCurrentUser().getUsername();
        addOrUpdateUserLocationMarker(username, userPosition);

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentCameraPosition));


        // Start updating positions of other users
        startUpdateUserPositionsTimer();

        // TODO: Allow Intent-provided data to override this setting.
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.

        stopUpdateUserPositionsTimer();

        // TODO: Set up logic for handling this event.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // TODO: Finish logic for displaying errors in this event.

        stopUpdateUserPositionsTimer();

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            //showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    /**
     * <p>Adds a marker to the Google map as well as the internal HashMap that keeps track of existing markers.</p>
     *
     * @param username
     * @param userPosition
     */
    private void addOrUpdateUserLocationMarker(String username, LatLng userPosition) {
        Marker userLocationMarker = mMap.addMarker(new MarkerOptions().position(userPosition).title(username));
        if (userMapMarkers.containsKey(username)) {
            userMapMarkers.get(username).remove();
        }

        userMapMarkers.put(username, userLocationMarker);
    }

    private void addOrUpdateUserLocationMarker(String username, ParseGeoPoint point) {
        LatLng userPosition = new LatLng(point.getLatitude(), point.getLongitude());
        addOrUpdateUserLocationMarker(username, userPosition);
    }

    private void getUserPositionsFromDatabase() {
        // Get (username, position)-pairs from the server and add to userPositionsMap
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereExists("position");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    // Return the map to the onPostExecute method
                    updateUserPositionsFromDatabaseData(userList);
                } else {
                    Log.d("userPositionFetch", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void updateUserPositionsFromDatabaseData(List<ParseObject> userList) {
        for (ParseObject userObj : userList) {
            String username = userObj.getString("username");
            ParseGeoPoint point = (ParseGeoPoint) userObj.get("position");
            addOrUpdateUserLocationMarker(username, point);
        }
    }

    private void startUpdateUserPositionsTimer() {
        updatePositionsTimer = new Timer();
        TimerTask updateUserPositionsTask = new TimerTask() {
            @Override
            public void run() {
                getUserPositionsFromDatabase();
            }
        };
        updatePositionsTimer.scheduleAtFixedRate(updateUserPositionsTask, 0, LittleBigBrother.FETCH_USER_POSITION_INTERVAL);
    }

    private void stopUpdateUserPositionsTimer() {
        updatePositionsTimer.cancel();
    }
}