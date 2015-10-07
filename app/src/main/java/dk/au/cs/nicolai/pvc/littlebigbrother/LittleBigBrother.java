package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseObject;

import dk.au.cs.nicolai.pvc.littlebigbrother.database.Reminder;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.ClassNameType;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.Log;

/**
 * <p>Main class for methods and constants specific to the Little Big Brother application.</p>
 *
 * TODO: Create Reminder class for storing reminder information and handle sync with database.
 */
public class LittleBigBrother {
    public static final String PACKAGE_NAME = LittleBigBrother.class.getPackage().getName();
    public static final String ID_PREFIX = "dk.au.cs.nicolai.pvc.littlebigbrother";
    private static final String SUFFIX_SEPARATOR = ".";
    private static final String EVENT_NAME_SUFFIX_SEPARATOR = ":";

    public static final float DEFAULT_ZOOM_LEVEL = 14;
    public static final float DEFAULT_USER_SELF_MARKER_HUE = BitmapDescriptorFactory.HUE_BLUE;
    public static final float DEFAULT_USER_OTHER_MARKER_HUE = BitmapDescriptorFactory.HUE_YELLOW;
    public static final float DEFAULT_REMINDER_MARKER_HUE = BitmapDescriptorFactory.HUE_RED;

    public static final long UPDATE_POSITION_FASTEST_INTERVAL = 5 * 1000;
    public static final long UPDATE_USER_POSITION_INTERVAL = 20 * 1000;
    public static final long FETCH_USER_POSITION_INTERVAL = 20 * 1000;

    public static final String UPDATE_USER_POSITION_SERVICE_NAME = ID_PREFIX + ":" + "UPDATE_USER_POSITION_SERVICE";

    public static final boolean DEBUG_MODE = true;
    public static final boolean USE_CACHED_USER = true;

    private static final String PARSE_APPLICATION_ID = "QbrGLBCIPI7lRRX4p3Hn66xP1hjkO49gRfJhfVN5";
    private static final String PARSE_CLIENT_KEY = "L1AKtlEN8UIqoypKJmpiETyV4iijHFySExLajbql";

    public static final boolean DRAWER_ONLY_SHOW_FOR_USERS = false;

    public interface Settings {
        boolean REQUEST_LOCATION_UPDATES = true;

        ClassNameType PREFERRED_CLASS_NAME_TYPE = ClassNameType.SIMPLE;
    }

    public interface Constants {
        String LOG = "pvc.littlebigbrother";

        interface DB {
            String USER_POSITION_ATTRIBUTE = "position";
            String USER_PAIRED_DEVICES_ATTRIBUTE = "pairedDevices";
        }

        interface Date {
            String SHORT_YEAR_POSTFIX   = "y";
            String SHORT_MONTH_POSTFIX  = "m";
            String SHORT_DAY_POSTFIX    = "d";
            String SHORT_HOUR_POSTFIX   = "h";
            String SHORT_MINUTE_POSTFIX = "m";
        }
    }

    public interface Extra {
        String REQUEST_TYPE = ID_PREFIX + ":REQUEST_TYPE";

        String LOCATION = ID_PREFIX + ":EXTRA_LOCATION";
    }

    public interface Action {
        String DEFAULT = ID_PREFIX + ":" + "ACTION_DEFAULT";
        String PICK_LOCATION = ID_PREFIX + ":" + "ACTION_PICK_LOCATION";
    }

    public interface DB {
        String USER_NAME_ATTRIBUTE = "username";
        String USER_POSITION_ATTRIBUTE = "position";
        String USER_PAIRED_DEVICES_ATTRIBUTE = "pairedDevices";

        String REMINDERS = PACKAGE_NAME + ":" + "REMINDERS";
    }

    public interface Events {
        String GOOGLE_API_CLIENT_CONNECTED = buildEventName("GOOGLE_API_CLIENT_CONNECTED");
        String USER_LOGIN_SUCCESS = buildEventName("USER_LOGIN_SUCCESS");

        interface Wifi {
            String UNPAIRING_SUCCESSFUL = buildEventName("UNPAIRING_SUCCESSFUL");
            String PAIRING_SUCCESSFUL = buildEventName("PAIRING_SUCCESSFUL");
        }
    }

    public interface Icons {
        GoogleMaterial.Icon REMINDER_ICON_LOCATION    = GoogleMaterial.Icon.gmd_satellite;
        GoogleMaterial.Icon REMINDER_ICON_TARGET_USER = GoogleMaterial.Icon.gmd_person;
        GoogleMaterial.Icon REMINDER_ICON_DATE_TIME   = GoogleMaterial.Icon.gmd_alarm;
    }

    public interface Colors {
        int HOLO_BLUE_LIGHT = Color.parseColor("#33b5e5");
    }

    // Register custom ParseObject subclasses
    static {
        ParseObject.registerSubclass(Reminder.class);
        ParseObject.registerSubclass(Reminder.Location.class);
        ParseObject.registerSubclass(Reminder.TargetUser.class);
        ParseObject.registerSubclass(Reminder.DateTime.class);
    }

    private LittleBigBrother() {}

    public static String getName(Object src) {
        Class sourceClass = src.getClass();

        switch (Settings.PREFERRED_CLASS_NAME_TYPE) {
            case NAME:
                return sourceClass.getName();
            case SIMPLE:
                return sourceClass.getSimpleName();
            case CANONICAL:
                return sourceClass.getCanonicalName();
        }

        return null;
    }

    public static String enumValue(Enum src, String value) {
        String name = getName(src);
        return name + SUFFIX_SEPARATOR + value;
    }

    public static String buildEventName(String id) {
        return ID_PREFIX + EVENT_NAME_SUFFIX_SEPARATOR + id;
    }

    /**
     * <p>Initializes the Parse database using the given context.</p>
     *
     * @param context context to use for initialization
     */
    public static void initParseDB(Context context) {
        Log.debug(LittleBigBrother.class, "Initializing database.");

        Parse.enableLocalDatastore(context);

        // Enable Crash Reporting
        ParseCrashReporting.enable(context);

        Parse.initialize(context, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
    }

    public static String distanceAsString(Integer distance) {
        if (distance < 1000) {
            return distance + " m";
        } else {
            return (distance / 1000) + " km";
        }
    }


    /**
     * <p>Constants used for identifying message types used for interfacing between activities in
     * the Little Big Brother application.</p>
     *
     * <p>Most commonly used for communicating via {@link android.content.Intent} objects when switching
     * activity.</p>
     */
    public static class MessageType {
        public static final String MAP_CAMERA_POSITION = ID_PREFIX + ":" + "MAP_CAMERA_POSITION";

        private MessageType() {}
    }

    public enum MsgType {
        //MAP_CAMERA_POSITION("MAP_CAMERA_POSITION");
    }

    public static class MessageTypeFactory {

    }

    public static class MapCameraPosition {
        private float latitude;
        private float longitude;
        private float zoom;
        private float bearing;
        private float tilt;

        public MapCameraPosition() {}



        public static void build() {

        }
    }
}
