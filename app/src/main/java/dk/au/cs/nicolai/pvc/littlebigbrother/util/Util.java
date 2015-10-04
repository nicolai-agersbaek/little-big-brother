package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import android.location.Location;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseGeoPoint;

import java.util.Calendar;

import dk.au.cs.nicolai.pvc.littlebigbrother.database.DateTime;

/**
 * Created by Nicolai on 04-10-2015.
 */
public final class Util {
    public static final String LATITUDE_LONGITUDE_SEPARATOR = ":";

    public static void setDateText(TextView view, Calendar calendar) {
        view.setText(DateTime.getDateString(calendar));
    }

    public static void setDateText(Button view, Calendar calendar) {
        view.setText(DateTime.getDateString(calendar));
    }

    public static void setTimeText(TextView view, Calendar calendar) {
        view.setText(DateTime.getTimeString(calendar));
    }

    public static void setTimeText(Button view, Calendar calendar) {
        view.setText(DateTime.getTimeString(calendar));
    }

    public static String getLocationString(Location location) {
        return location.getLatitude() + LATITUDE_LONGITUDE_SEPARATOR + location.getLongitude();
    }

    public static String getLocationString(ParseGeoPoint location) {
        return location.getLatitude() + LATITUDE_LONGITUDE_SEPARATOR + location.getLongitude();
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
}
