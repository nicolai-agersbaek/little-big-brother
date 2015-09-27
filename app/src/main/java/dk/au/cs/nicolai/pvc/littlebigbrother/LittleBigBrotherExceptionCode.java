package dk.au.cs.nicolai.pvc.littlebigbrother;

/**
 * Created by Nicolai on 27-09-2015.
 */
public enum LittleBigBrotherExceptionCode {
    USER_NOT_LOGGED_IN(1);

    private int code;

    LittleBigBrotherExceptionCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
