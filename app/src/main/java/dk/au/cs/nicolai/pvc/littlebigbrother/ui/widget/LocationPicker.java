package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

import dk.au.cs.nicolai.pvc.littlebigbrother.ApplicationController;
import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.FragmentWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Util;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class LocationPicker extends FragmentWidget<MapFragment> implements InteractiveWidget, OnMapReadyCallback, OnMapClickListener {

    private boolean hasMarker;

    private GoogleMap map;
    private Marker marker;

    private View mapContainer;
    private FloatingActionButton confirmLocationButton;
    private Button selectLocationButton;

    private String LOCATION_BUTTON_DEFAULT_VALUE;

    private LatLng location;
    private CameraPosition cameraPosition;

    private OnLocationSelectedCallback locationSelectedCallback;

    public LocationPicker(Activity context, MapFragment fragment, FragmentTransaction transaction, int containerResId, int mapContainerResId, int selectLocationButtonResId, int confirmLocationButtonResId) {
        super(context, fragment, transaction, containerResId);

        this.mapContainer = context.findViewById(mapContainerResId);
        this.selectLocationButton = (Button) context.findViewById(selectLocationButtonResId);
        this.confirmLocationButton = (FloatingActionButton) context.findViewById(confirmLocationButtonResId);

        LOCATION_BUTTON_DEFAULT_VALUE = (String) selectLocationButton.getText();

        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        confirmLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLocationButtonClicked();
            }
        });
    }

    private boolean hasLocationSelectedCallback() {
        return (locationSelectedCallback != null);
    }

    private void setLocationText() {
        Util.Loc.setLocationText(selectLocationButton, location);
    }

    public void setOnLocationSelectedCallback(OnLocationSelectedCallback callback) {
        this.locationSelectedCallback = callback;
    }

    private void confirmLocationButtonClicked() {
        setLocationText();

        if (hasLocationSelectedCallback()) {
            locationSelectedCallback.done(location);
        }

        hide();
    }

    @Override
    public void show() {
        centerMap();

        super.show();
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
        LatLng userPosition = ApplicationController.getUserLatLng();

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
        if (hasMarker) {
            removeMarker();
        }

        marker = map.addMarker(new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(LittleBigBrother.DEFAULT_USER_OTHER_MARKER_HUE)));

        hasMarker = true;
    }

    @Override
    public void onMapClick(LatLng location) {
        this.location = location;

        setMarker(location);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        centerMap();
    }

    public void setLocation(LatLng location) {
        this.location = location;

        setLocationText();
    }

    public void setLocation(double latitude, double longitude) {
        setLocation(new LatLng(latitude, longitude));
    }

    public void setLocation(ParseGeoPoint location) {
        setLocation(location.getLatitude(), location.getLongitude());
    }

    public void reset() {
        location = null;
        selectLocationButton.setText(LOCATION_BUTTON_DEFAULT_VALUE);
    }

    public static abstract class OnLocationSelectedCallback {
        public abstract void done(LatLng location);
    }
}
