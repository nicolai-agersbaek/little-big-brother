package dk.au.cs.nicolai.pvc.littlebigbrother.database;

import com.google.android.gms.maps.model.LatLng;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import dk.au.cs.nicolai.pvc.littlebigbrother.LittleBigBrother;

/**
 * Created by nicolai on 9/16/15.
 */
public class User {
    private String username;
    private String password;
    private String email;
    private String id;

    private LatLng position;

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void logIn(final LittleBigBrother.LogInCallback login) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    login.success();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    login.error(e);
                }

                if (e == null && user != null) {
                    login.success();
                } else if (user == null) {
                    login.usernameOrPasswordIsInvalid();
                } else {
                    login.error(e);
                }
            }
        });
    }
}
