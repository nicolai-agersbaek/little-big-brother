package dk.au.cs.nicolai.pvc.littlebigbrother.ui.widget.reminder;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;

import dk.au.cs.nicolai.pvc.littlebigbrother.util.SimpleDateTime;

/**
 * Created by Nicolai on 07-10-2015.
 */
public abstract class OnReminderDataChangedCallback {
    public abstract void onLocationChanged(LatLng location);
    public abstract void onRadiusChanged(int radius);
    public abstract void onTargetUserChanged(ParseUser targetUser);
    public abstract void onDateChanged(SimpleDateTime simpleDateTime);
}
