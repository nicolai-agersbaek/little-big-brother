package dk.au.cs.nicolai.pvc.littlebigbrother.exception;

/**
 * Created by Nicolai on 06-10-2015.
 */
public class ReminderAlreadyBoundException extends LittleBigBrotherException {
    public ReminderAlreadyBoundException() {
        super(ExceptionType.REMINDER_ALREADY_BOUND);
    }
}
