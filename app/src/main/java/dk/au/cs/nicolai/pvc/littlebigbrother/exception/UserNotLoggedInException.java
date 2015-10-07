package dk.au.cs.nicolai.pvc.littlebigbrother.exception;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class UserNotLoggedInException extends LittleBigBrotherException {
    public UserNotLoggedInException(String message) {
        super(ExceptionType.USER_NOT_LOGGED_IN, message);
    }
}
