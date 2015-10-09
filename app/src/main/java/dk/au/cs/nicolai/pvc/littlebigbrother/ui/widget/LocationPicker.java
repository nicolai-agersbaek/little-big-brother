package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget;

import android.app.Activity;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

import dk.au.cs.nicolai.pvc.littlebigbrother.ApplicationController;
import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.FragmentWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.MarkerWithRadius;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.OnRadiusChangedListener;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.RenderMarkerWithRadiusCallback;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class LocationPicker extends FragmentWidget<MapFragment> implements InteractiveWidget, OnMapReadyCallback, OnMapClickListener, OnMarkerDragListener, OnRadiusChangedListener {

    private boolean hasMarker;

    private GoogleMap map;
    private Marker marker;
    private Circle circle;
    private MarkerWithRadius markerWithRadius;

    private FloatingActionButton confirmLocationButton;

    private int radius;
    private LatLng location;
    private CameraPosition cameraPosition;

    private OnLocationSelectedCallback locationSelectedCallback;

    public LocationPicker(Activity context, MapFragment mapFragment, int containerResId, int confirmLocationButtonResId) {
        super(context, mapFragment, containerResId);

        this.confirmLocationButton = (FloatingActionButton) context.findViewById(confirmLocationButtonResId);

        confirmLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLocationButtonClicked();
            }
        });
    }

    public void setRadius(int radius) {
        this.radius = radius;

        markerWithRadius.onRadiusChanged(radius);
    }

    public void getMapAsync() {
        Log.debug(this, "getMapAsync called.");
        getFragment().getMapAsync(this);
    }

    public void setOnLocationSelectedCallback(OnLocationSelectedCallback callback) {
        this.locationSelectedCallback = callback;
    }

    private void confirmLocationButtonClicked() {
        locationSelectedCallback.done(location);

        hide();
    }

    @Override
    public void show() {
        if (map != null) {
            super.show();
        } else {
            Log.error(this, "show() called, but Map is not ready.");
        }
    }

    private void centerMap() {
        // Check location against camera position
        if (location != null && cameraPosition != map.getCameraPosition()) {
            setMapCameraPosition(location);
        } else {
            centerMapOnUser();
        }
    }

    private void centerMapOnUser() {
        Location userLocation = LocationServices.FusedLocationApi.getLastLocation(ApplicationController.getGoogleApiClient());

        LatLng userPosition = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

        setMapCameraPosition(userPosition);
    }

    private void setMapCameraPosition(LatLng position) {
        cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(LittleBigBrother.DEFAULT_ZOOM_LEVEL)
                .build();

        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void removeMarker() {
        marker.remove();
        hasMarker = false;
    }

    private void setMarker(LatLng location) {
        MarkerOptions markerOptions = new MarkerOptions()
                .draggable(true)
                .position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(LittleBigBrother.DEFAULT_USER_OTHER_MARKER_HUE));

        setMarker(markerOptions);
    }

    private void setMarker(MarkerOptions markerOptions) {
        if (hasMarker) {
            removeMarker();
        }

        marker = map.addMarker(markerOptions);

        hasMarker = true;
    }

    private void setCircle(CircleOptions circleOptions) {
        if (circle != null) {
            circle.remove();
        }

        circle = map.addCircle(circleOptions);
    }

    @Override
    public void onMapClick(LatLng location) {
        Log.debug(getContext(), "onMapClick: " + location);

        this.location = location;

        if (markerWithRadius == null) {
            markerWithRadius = new MarkerWithRadius(location, radius);
            markerWithRadius.setRenderCallback(new RenderMarkerWithRadiusCallback() {
                @Override
                public void render(MarkerOptions markerOptions, CircleOptions circleOptions) {
                    setMarker(markerOptions);
                    setCircle(circleOptions);
                }
            });
        }

        markerWithRadius.onMarkerMoved(location);

        setMarker(location);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.debug(this, "onMapReady.");

        map = googleMap;

        map.setOnMapClickListener(this);
        map.setOnMarkerDragListener(this);

        centerMap();
    }

    @Override
    public void onRadiusChanged(int radius) {
        this.radius = radius;

        if (markerWithRadius != null) {
            markerWithRadius.onRadiusChanged(radius);
        }
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setLocation(double latitude, double longitude) {
        setLocation(new LatLng(latitude, longitude));
    }

    public void setLocation(ParseGeoPoint location) {
        setLocation(location.getLatitude(), location.getLongitude());
    }

    public void reset() {
        location = null;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        // Hide Circle while dragging
        circle.remove();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        // Do nothing
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // Save location
        markerWithRadius.onMarkerMoved(marker.getPosition());
    }

    public static abstract class OnLocationSelectedCallback {
        public abstract void done(LatLng location);
    }
}
