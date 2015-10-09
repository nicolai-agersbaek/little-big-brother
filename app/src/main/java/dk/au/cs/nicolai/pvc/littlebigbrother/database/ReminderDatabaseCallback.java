package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import com.parse.ParseException;

/**
 * Created by nicolai on 10/9/15.
 */
public interface ReminderDatabaseCallback {
    void success();
    void error(ParseException e);
}
