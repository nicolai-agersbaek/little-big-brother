package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;

/**
 * Created by Nicolai on 27-09-2015.
 */
public abstract class LittleBigBrotherException extends Exception {

    private static final String USER_NOT_LOGGED_IN = LittleBigBrother.buildEventName("USER_NOT_LOGGED_IN");

    private static final String REMINDER_ALREADY_BOUND = LittleBigBrother.buildEventName("REMINDER_ALREADY_BOUND");

    private String errorCode;
    private String message;

    private LittleBigBrotherException(String errorCode) {
        this.errorCode = errorCode;
    }

    private LittleBigBrotherException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static final class UserNotLoggedIn extends LittleBigBrotherException {
        public UserNotLoggedIn(String message) {
            super(USER_NOT_LOGGED_IN, message);
        }
    }

    public static final class ReminderAlreadyBound extends LittleBigBrotherException {
        public ReminderAlreadyBound() {
            super(REMINDER_ALREADY_BOUND);
        }
    }

    public String getErrorCode() {
        return errorCode;
    }
}
