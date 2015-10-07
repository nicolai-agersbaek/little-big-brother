package dk.au.cs.nicolai.pvc.littlebigbrother.ui;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;

/**
 * Created by Nicolai on 07-10-2015.
 */
public class MarkerWithRadius implements OnMarkerMovedListener, OnRadiusChangedListener {
    private MarkerOptions markerOptions;
    private CircleOptions circleOptions;

    private RenderMarkerWithRadiusCallback renderCallback;

    public MarkerWithRadius(LatLng location, int radius) {
        markerOptions = new MarkerOptions()
                .draggable(true)
                .position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(LittleBigBrother.DEFAULT_USER_OTHER_MARKER_HUE));

        circleOptions = new CircleOptions()
                .center(location)
                .radius(radius)
                .strokeWidth(10)
                .strokeColor(Color.argb(255, 50, 50, 255))
                .fillColor(Color.argb(80, 0, 0, 200));
    }

    @Override
    public void onMarkerMoved(LatLng location) {
        markerOptions.position(location);
        circleOptions.center(location);

        renderCallback.render(markerOptions, circleOptions);
    }

    @Override
    public void onRadiusChanged(int radius) {
        circleOptions.radius(radius);

        renderCallback.render(markerOptions, circleOptions);
    }

    public void setRenderCallback(RenderMarkerWithRadiusCallback renderCallback) {
        this.renderCallback = renderCallback;
    }
}
