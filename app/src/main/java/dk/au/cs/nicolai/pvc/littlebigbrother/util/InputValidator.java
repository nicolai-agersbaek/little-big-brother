package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nicolai on 9/18/15.
 */
public class InputValidator {
    private InputValidator() {}

    public static boolean validate(final String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }
}
