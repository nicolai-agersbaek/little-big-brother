package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;
import dk.au.cs.nicolai.pvc.littlebigbrother.MapsActivity;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class LocationPicker extends Widget implements InteractiveWidget, OnMapReadyCallback, OnMapClickListener {

    private MapFragment mapFragment;

    private View container;
    private Button selectLocationButton;

    private String LOCATION_BUTTON_DEFAULT_VALUE;

    public LocationPicker(Activity context, View container, Button selectLocationButton) {
        super(context, container);

        this.selectLocationButton = selectLocationButton;
        LOCATION_BUTTON_DEFAULT_VALUE = (String) selectLocationButton.getText();
    }

    public LocationPicker(Activity context, int containerResId, int selectLocationButtonResId) {
        this(context, context.findViewById(containerResId), (Button) context.findViewById(selectLocationButtonResId));
    }

    private void pickLocation() {
        Intent pickLocationIntent = new Intent(getContext(), MapsActivity.class);
        pickLocationIntent.setAction(LittleBigBrother.Action.PICK_LOCATION);
        getContext().startActivity(pickLocationIntent);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public void reset() {
        selectLocationButton.setText(LOCATION_BUTTON_DEFAULT_VALUE);
    }
}
