package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Nicolai on 27-09-2015.
 */
public abstract class UserListResultCallback {
    public abstract void success(final List<ParseUser> userList);
    public abstract void error(ParseException e);
}
