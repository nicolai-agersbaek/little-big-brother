package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;

/**
 * Created by nicolai on 10/9/15.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        Log.debug(this, "onHandleIntent called.");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            //String errorMessage = GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
            String errorMessage = "Geofence error";
            Log.error(this, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            sendNotification(triggeringGeofences);
            Log.info(this, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.error(this, getString(R.string.error_geofence_transition_invalid_type,
                    geofenceTransition));
        }
    }

    private String getGeofenceTransitionDetails(IntentService intentService, int integer, List<Geofence> geofences) {
        String details;

        return "";
    }

    private void sendNotification(List<Geofence> geofences) {
        Log.info(this, "sendNotification called.");

        int id = 0;

        for (Geofence geofence :
                geofences) {
            id = id + 1;
            String reminderTitle = geofence.getRequestId();
            String reminderDescription = "within fence";

            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_cast_dark)
                    .setContentTitle(reminderTitle)
                    .setContentText(reminderDescription);

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(id, builder.build());
            Log.info(this, "Geofence triggered.");
        }
    }
}
