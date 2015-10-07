package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import dk.au.cs.nicolai.pvc.littlebigbrother.ui.Widget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.InteractiveWidget;
import dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.LocationPicker;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Util;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class SelectLocationWidget extends Widget implements InteractiveWidget {

    private String LOCATION_BUTTON_DEFAULT_VALUE;

    private Button selectLocationButton;
    private LocationPicker locationPicker;

    private OnLocationSelectedCallback locationSelectedCallback;

    public SelectLocationWidget(Activity context, int containerResId, int selectLocationButtonResId, LocationPicker locationPicker) {
        super(context, containerResId);

        this.selectLocationButton = (Button) context.findViewById(selectLocationButtonResId);
        this.locationPicker = locationPicker;

        LOCATION_BUTTON_DEFAULT_VALUE = (String) selectLocationButton.getText();

        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationPicker();
            }
        });

        this.locationPicker.setOnLocationSelectedCallback(new LocationPicker.OnLocationSelectedCallback() {
            @Override
            public void done(LatLng location) {
                // TODO: Type casting should be done when selecting type
                setLocationText();

                if (locationSelectedCallback != null) {
                    locationSelectedCallback.done(location);
                }
            }
        });
    }

    public void setOnLocationSelectedCallback(OnLocationSelectedCallback callback) {
        this.locationSelectedCallback = callback;
    }

    private void showLocationPicker() {
        locationPicker.show();
    }

    private void setLocationText() {
        Util.Loc.setLocationText(selectLocationButton, locationPicker.getLocation());
    }

    public void reset() {
        selectLocationButton.setText(LOCATION_BUTTON_DEFAULT_VALUE);
        locationPicker.hide();
    }

    public static abstract class OnLocationSelectedCallback {
        public abstract void done(LatLng location);
    }
}
