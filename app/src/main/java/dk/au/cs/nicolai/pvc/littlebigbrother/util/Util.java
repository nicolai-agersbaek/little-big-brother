package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

/**
 * Created by Nicolai on 04-10-2015.
 */
public final class Util {

    public static class Loc {
        public static final String LATITUDE_LONGITUDE_SEPARATOR = ":";

        public static String getLocationString(Location location) {
            return location.getLatitude() + LATITUDE_LONGITUDE_SEPARATOR + location.getLongitude();
        }

        public static String getLocationString(ParseGeoPoint location) {
            return location.getLatitude() + LATITUDE_LONGITUDE_SEPARATOR + location.getLongitude();
        }

        public static String getLocationString(LatLng location) {
            return location.latitude + LATITUDE_LONGITUDE_SEPARATOR + location.longitude;
        }


        public static void setLocationText(TextView view, Location location) {
            view.setText(getLocationString(location));
        }

        public static void setLocationText(Button view, Location location) {
            view.setText(getLocationString(location));
        }

        public static void setLocationText(TextView view, ParseGeoPoint location) {
            view.setText(getLocationString(location));
        }

        public static void setLocationText(Button view, ParseGeoPoint location) {
            view.setText(getLocationString(location));
        }

        public static void setLocationText(TextView view, LatLng location) {
            view.setText(getLocationString(location));
        }

        public static void setLocationText(Button view, LatLng location) {
            view.setText(getLocationString(location));
        }
    }


    // OTHER
    public static void showNotYetImplementedAlert(Context context, String srcName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // Set title
        alertDialogBuilder.setTitle("Error");

        // Set dialog message
        alertDialogBuilder
                .setMessage(srcName + " not yet implemented!")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close dialog, do nothing
                        dialog.cancel();
                    }
                });

        // Create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Show it
        alertDialog.show();
    }
}
