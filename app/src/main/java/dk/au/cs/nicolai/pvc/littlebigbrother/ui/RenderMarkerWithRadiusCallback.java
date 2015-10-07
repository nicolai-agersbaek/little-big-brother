package dk.au.cs.nicolai.pvc.littlebigbrother.ui;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Nicolai on 07-10-2015.
 */
public abstract class RenderMarkerWithRadiusCallback {
    public abstract void render(MarkerOptions markerOptions, CircleOptions circleOptions);
}
