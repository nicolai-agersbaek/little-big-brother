package dk.au.cs.nicolai.pvc.littlebigbrother.exception;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;

/**
 * Created by Nicolai on 27-09-2015.
 */
interface ExceptionType {
    String USER_NOT_LOGGED_IN = LittleBigBrother.buildEventName("USER_NOT_LOGGED_IN");
    String REMINDER_ALREADY_BOUND = LittleBigBrother.buildEventName("REMINDER_ALREADY_BOUND");
}
