package dk.au.cs.nicolai.pvc.littlebigbrother;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import dk.au.cs.nicolai.pvc.littlebigbrother.database.UpdateUserPositionService;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.InputValidationPattern;
import dk.au.cs.nicolai.pvc.littlebigbrother.util.InputValidator;

/**
 * A login screen that offers login via email/password.
 *
 * TODO: Create Password, Email and Username form field classes to simplify LoginActivity.
 */
public class LoginActivity extends Activity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "test@test:test",
            "bar@example.com:world"
    };

    private GoogleApiClient mGoogleApiClient;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LittleBigBrother.USE_CACHED_USER) {
            performCachedUserAction();
        }

        //performDebugModeActions();

        Log.e(LittleBigBrother.Constants.LOG, "LoginActivity: GoogleApiClient creation.");

        // Create GoogleApiClient object for use with the UpdateUserPositionService.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.email_register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void performDebugModeActions() {
        if (LittleBigBrother.DEBUG_MODE) {
            String testUserEmail = "nicolai.agersbaek@gmail.com";

            mEmailView.setText(testUserEmail);
            mPasswordView.requestFocus();
        }
    }

    private void performCachedUserAction() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            loginSuccess();
        }
    }

    public boolean checkFields() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (isEmpty(mPasswordView)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid()) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (isEmpty(mEmailView)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid()) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            return true;
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (checkFields()) {
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            signInInBackground(email, password);
        }
    }

    public void attemptRegistration() {
        if (checkFields()) {
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            registerInBackground(email, password);
        }
    }

    private void signInInBackground(String email, String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    loginSuccess();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Log.e(LittleBigBrother.Constants.LOG, "Signup failed. Error code: " + e.getCode());

                    // Hide progress spinner
                    showProgress(false);

                    switch (e.getCode()) {
                        // Unregistrered user attempted to sign in
                        case ParseException.OBJECT_NOT_FOUND:
                            mEmailView.setError(getString(R.string.error_object_not_found));
                            mEmailView.requestFocus();
                    }
                }
            }
        });
    }

    private void registerInBackground(String email, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    loginSuccess();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong

                    // Hide progress spinner
                    showProgress(false);

                    switch (e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            mEmailView.setError(getString(R.string.error_email_taken));
                            mEmailView.requestFocus();
                        default:
                            Log.e(LittleBigBrother.Constants.LOG, "Login failed. Error code: " + e.getCode());
                    }
                }
            }
        });
    }

    private boolean isEmailValid() {
        return isValid(mEmailView, InputValidationPattern.EMAIL);
    }

    private boolean isPasswordValid() {
        return isValid(mPasswordView, InputValidationPattern.PASSWORD);
    }

    private boolean isValid(TextView view, InputValidationPattern pattern) {
        String input = view.getText().toString();

        return InputValidator.validate(input, pattern.pattern);
    }

    private boolean isEmpty(TextView view) {
        String input = view.getText().toString();

        return TextUtils.isEmpty(input);
    }


    private void startRequestingUserLocationUpdates() {
        // Create Intent for starting user-position update service
        Intent intent = new Intent(this, UpdateUserPositionService.class);
        startService(intent);
    }

    private void sendUserLoginSuccessMessage() {
        // TODO: Refactor - should simply call static method from ApplicationController class, to notify controller of login success.
        // This is possible since life-cycle of LoginActivity is subset of controller's.
        Intent intent = new Intent(LittleBigBrother.Events.USER_LOGIN_SUCCESS);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void loginSuccess() {
        ApplicationController AC = (ApplicationController) getApplicationContext();
        AC.userLoginSuccessNotification();

        // Hide progress spinner
        //showProgress(false);

        //Log.e(LittleBigBrother.Constants.LOG, "LoginActivity: Requesting user location updates.");
        //startRequestingUserLocationUpdates();
        redirect();
    }

    public void redirect() {
        startMapsActivity();
    }

    public void startMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);

        startActivity(intent);
    }
}

