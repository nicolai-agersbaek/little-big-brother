package dk.au.cs.nicolai.pvc.littlebigbrother.util;

import java.util.regex.Pattern;

/**
 * Created by nicolai on 9/22/15.
 */
public enum InputValidationPattern {
    EMAIL("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"),
    USERNAME("[_A-Za-z0-9\\.]{4,}"),
    PASSWORD("[_A-Za-z0-9\\.]{4,}");

    public Pattern pattern;

    InputValidationPattern(String patternString) {
        pattern = Pattern.compile(patternString);
    }
}
