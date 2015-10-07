package dk.au.cs.nicolai.pvc.littlebigbrother.exception;

/**
 * Created by Nicolai on 27-09-2015.
 */
public abstract class LittleBigBrotherException extends Exception {

    private String code;
    private String message;

    LittleBigBrotherException(String code) {
        this.code = code;
    }

    LittleBigBrotherException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public final String getCode() {
        return code;
    }

    public final String getMessage() {
        return message;
    }
}
