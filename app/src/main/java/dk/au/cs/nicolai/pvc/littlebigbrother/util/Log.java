package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import com.parse.ParseException;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;

/**
 * Created by Nicolai on 22-09-2015.
 */
public final class Log {
    private static final String TAG = "pvc.littlebigbrother";
    private static final String CLASS_MESSAGE_SEPARATOR = ":";

    public interface LOG_LEVEL {
        int NONE = 0;
        int INFO = 1;
        int WARN = 2;
        int EXCEPTION = 4;
        int DEBUG = 8;
        int ERROR = 16;
        int VERBOSE = 32;
    }

    private Log() {}

    private static String buildLogMsg(Object source, String message) {
        String className = LittleBigBrother.getName(source);

        String msg = className + CLASS_MESSAGE_SEPARATOR + " " + message;

        return msg;
    }



    private static void log(Object source, String message, int LEVEL) {
        switch (LEVEL) {
            case LOG_LEVEL.VERBOSE:
                android.util.Log.v(TAG, buildLogMsg(source, message));
                break;
            case LOG_LEVEL.ERROR:
                android.util.Log.e(TAG, buildLogMsg(source, message));
                break;
            case LOG_LEVEL.DEBUG:
                android.util.Log.d(TAG, buildLogMsg(source, message));
                break;
            case LOG_LEVEL.EXCEPTION:
                android.util.Log.e(TAG, buildLogMsg(source, message));
                break;
            case LOG_LEVEL.WARN:
                android.util.Log.w(TAG, buildLogMsg(source, message));
                break;
            case LOG_LEVEL.INFO:
                android.util.Log.i(TAG, buildLogMsg(source, message));
                break;
        }
    }

    private static void log(Object source, String message, Throwable throwable, int LEVEL) {
        switch (LEVEL) {
            case LOG_LEVEL.VERBOSE:
                android.util.Log.v(TAG, buildLogMsg(source, message), throwable);
                break;
            case LOG_LEVEL.ERROR:
                android.util.Log.e(TAG, buildLogMsg(source, message), throwable);
                break;
            case LOG_LEVEL.DEBUG:
                android.util.Log.d(TAG, buildLogMsg(source, message), throwable);
                break;
            case LOG_LEVEL.EXCEPTION:
                android.util.Log.e(TAG, buildLogMsg(source, message), throwable);
                break;
            case LOG_LEVEL.WARN:
                android.util.Log.w(TAG, buildLogMsg(source, message), throwable);
                break;
            case LOG_LEVEL.INFO:
                android.util.Log.i(TAG, buildLogMsg(source, message), throwable);
                break;
        }
    }

    public static void debug(Object source, String message) {
        log(source, message, LOG_LEVEL.DEBUG);
    }

    public static void debug(Object source, String message, Throwable throwable) {
        log(source, message, throwable, LOG_LEVEL.DEBUG);
    }

    public static void error(Object source, String message) {
        log(source, message, LOG_LEVEL.ERROR);
    }

    public static void error(Object source, String message, Throwable throwable) {
        log(source, message, throwable, LOG_LEVEL.ERROR);
    }

    public static void info(Object source, String message) {
        log(source, message, LOG_LEVEL.INFO);
    }

    public static void info(Object source, String message, Throwable throwable) {
        log(source, message, throwable, LOG_LEVEL.INFO);
    }

    public static void verbose(Object source, String message) {
        log(source, message, LOG_LEVEL.VERBOSE);
    }

    public static void verbose(Object source, String message, Throwable throwable) {
        log(source, message, throwable, LOG_LEVEL.VERBOSE);
    }

    public static void warn(Object source, String message) {
        log(source, message, LOG_LEVEL.WARN);
    }

    public static void warn(Object source, String message, Throwable throwable) {
        log(source, message, throwable, LOG_LEVEL.WARN);
    }

    private static void exception(Object source, String message, String eName, String eCause, String eMsg) {
        String cause = (eCause == null ? "" : " (" + eCause + ")");
        String msg = message + "." + eName + cause + ": " + eMsg;

        log(source, message, LOG_LEVEL.ERROR);
    }

    public static void exception(Object source, String message, Exception e) {
        String eName = e.getClass().getName();
        String eCause = e.getCause().getMessage();
        String eMsg = e.getMessage();

        exception(source, message, eName, eCause, eMsg);
    }

    public static void exception(Object source, String message, ParseException e) {
        String eName = e.getClass().getName() + "[" + e.getCode() + "]";
        String eCause = e.getCause().getMessage();
        String eMsg = e.getMessage();

        exception(source, message, eName, eCause, eMsg);
    }

    public static void exception(Object source, ParseException e) {
        exception(source, "", e);
    }
}
