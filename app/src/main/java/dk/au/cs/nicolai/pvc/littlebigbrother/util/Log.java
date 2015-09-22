package dk.au.cs.nicolai.pvc.littlebigbrother.util;

/**
 * Created by Nicolai on 22-09-2015.
 */
public final class Log {
    private static final String TAG = "pvc.littlebigbrother";
    private static final String CLASS_MESSAGE_SEPARATOR = ":";

    private Log() {}

    private static String buildLogMsg(Object source, String message) {
        String sourceName = source.getClass().getName();
        String msg = sourceName + CLASS_MESSAGE_SEPARATOR + " " + message;

        return msg;
    }

    public static void debug(Object source, String message) {
        android.util.Log.d(TAG, buildLogMsg(source, message));
    }

    public static void debug(Object source, String message, Throwable throwable) {
        android.util.Log.d(TAG, buildLogMsg(source, message), throwable);
    }

    public static void error(Object source, String message) {
        android.util.Log.e(TAG, buildLogMsg(source, message));
    }

    public static void error(Object source, String message, Throwable throwable) {
        android.util.Log.e(TAG, buildLogMsg(source, message), throwable);
    }

    public static void info(Object source, String message) {
        android.util.Log.i(TAG, buildLogMsg(source, message));
    }

    public static void info(Object source, String message, Throwable throwable) {
        android.util.Log.i(TAG, buildLogMsg(source, message), throwable);
    }

    public static void verbose(Object source, String message) {
        android.util.Log.v(TAG, buildLogMsg(source, message));
    }

    public static void verbose(Object source, String message, Throwable throwable) {
        android.util.Log.v(TAG, buildLogMsg(source, message), throwable);
    }

    public static void warn(Object source, String message) {
        android.util.Log.w(TAG, buildLogMsg(source, message));
    }

    public static void warn(Object source, String message, Throwable throwable) {
        android.util.Log.w(TAG, buildLogMsg(source, message), throwable);
    }
}
