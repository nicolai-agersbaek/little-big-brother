package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;

/**
 * <p>Main class for methods and constants specific to the Little Big Brother application.</p>
 *
 * TODO: Create Reminder class for storing reminder information and handle sync with database.
 */
public class LittleBigBrother {
    public static final String ID_PREFIX = "dk.au.cs.nicolai.pvc.littlebigbrother";
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
    public static final boolean USE_CACHED_USER = false;

    private static final String PARSE_APPLICATION_ID = "QbrGLBCIPI7lRRX4p3Hn66xP1hjkO49gRfJhfVN5";
    private static final String PARSE_CLIENT_KEY = "L1AKtlEN8UIqoypKJmpiETyV4iijHFySExLajbql";


    public interface Constants {
        String LOG = "pvc.littlebigbrother";

        interface DB {
            String USER_POSITION_ATTRIBUTE = "position";
            String USER_PAIRED_DEVICES_ATTRIBUTE = "pairedDevices";
        }
    }

    public interface DB {
        String USER_NAME_ATTRIBUTE = "username";
        String USER_POSITION_ATTRIBUTE = "position";
        String USER_PAIRED_DEVICES_ATTRIBUTE = "pairedDevices";
    }

    public interface Events {
        String GOOGLE_API_CLIENT_CONNECTED = buildEventName("GOOGLE_API_CLIENT_CONNECTED");
        String USER_LOGIN_SUCCESS = buildEventName("USER_LOGIN_SUCCESS");
    }

    public interface Colors {
        int HOLO_BLUE_LIGHT = Color.parseColor("#33b5e5");
    }

    // Register custom ParseObject subclasses
    static {
        //ParseObject.registerSubclass(User.class);
    }

    private LittleBigBrother() {}

    private static String buildEventName(String id) {
        return ID_PREFIX + EVENT_NAME_SUFFIX_SEPARATOR + id;
    }

    /**
     * <p>Initializes the Parse database using the given context.</p>
     *
     * @param context context to use for initialization
     */
    public static void initParseDB(Context context) {
        Log.e(Constants.LOG, "LittleBigBrother: Initializing database.");

        Parse.enableLocalDatastore(context);

        // Enable Crash Reporting
        ParseCrashReporting.enable(context);

        Parse.initialize(context, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
    }

    public abstract class LogInCallback {
        public abstract void success();
        public abstract void usernameOrPasswordIsInvalid();
        public abstract void error(ParseException e);
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
