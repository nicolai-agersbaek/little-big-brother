package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrotherExceptionCode;

/**
 * Created by Nicolai on 27-09-2015.
 */
public class LittleBigBrotherException extends Exception {
    private LittleBigBrotherExceptionCode code;
    private String message;

    public LittleBigBrotherException(LittleBigBrotherExceptionCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public LittleBigBrotherExceptionCode getCode() {
        return code;
    }
}
