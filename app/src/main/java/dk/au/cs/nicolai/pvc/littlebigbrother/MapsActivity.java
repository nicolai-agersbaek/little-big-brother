package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

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
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity
    implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;

    private CameraPosition currentCameraPosition;
    // TODO: Add list of current map markers.

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
        // TODO: Tidy up filler code.

        // Assign the passed GoogleMap instance to a private field
        mMap = map;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
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
        // Get last known user Location object from API.
        Location userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng userPosition = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

        // Create CameraPosition object from userPosition, using default zoom level
        currentCameraPosition = new CameraPosition.Builder()
                .target(userPosition)
                .zoom(LittleBigBrother.DEFAULT_ZOOM_LEVEL)
                .build();

        mMap.addMarker(new MarkerOptions().position(userPosition).snippet("Her er du."));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentCameraPosition));



        // TODO: Allow Intent-provided data to override this setting.
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
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
}