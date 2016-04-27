package com.aau.wimb.whereismybike.BikeOwner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aau.wimb.whereismybike.R;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class UserLoginActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    public static final String PARCEL_KEY = "parcel_key";

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mSignInButton;
    private Button mRegisterButton;
    private Button mForgotPasswordButton;
    private LoginButton mFacebookButton;

    private TextView fbInfo;

    // To check if user had log in.
    private String userLogin = "false";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Bundle mBundle;

    // Facebook
    private CallbackManager mCallbackManager = null;
    private AccessTokenTracker mAccessTokenTracker = null;
    private ProfileTracker mProfileTracker = null;
    private AccessToken mAccessToken = null;
    private Profile mProfile = null;

    private AccessToken accessToken;
    private Profile profile;

//    private UserAccount facebookUserAccount;
//    private String profileId = "none";
    private String profileLink = "none";
    private String profileFirstName = "none";
    private String profileLastName = "none";
    private String profileEmail = "none";
    private String profilePicUrl = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        setupActionBar();

        // Facebook Initialize.
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

//        facebookUserAccount = new UserAccount();

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mForgotPasswordButton = (Button) findViewById(R.id.forgot_pwd_button);

        // Facebook login.
        fbInfo = (TextView)findViewById(R.id.info);
        mFacebookButton = (LoginButton) findViewById(R.id.facebook_login);

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

        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(UserLoginActivity.this,UserRegisterActivity.class);
//                startActivityForResult(myIntent,3);
                UserLoginActivity.this.startActivity(myIntent);
            }
        });

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.e("WiMB", "Access Token " + currentAccessToken);
            }
        };

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.e("WiMB", "Profile " + currentProfile);
            }
        };

        mAccessTokenTracker.startTracking();
        mProfileTracker.startTracking();

        if (AccessToken.getCurrentAccessToken() != null) {
            mAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    mAccessTokenTracker.stopTracking();
                    if(currentAccessToken == null) {
                        //(the user has revoked your permissions -
                        //by going to his settings and deleted your app)
                        //do the simple login to FaceBook
                        fbInfo.setText("You have been signed out, please login again.");
                    }
                    else {
                        //you've got the new access token now.
                        //AccessToken.getToken() could be same for both
                        //parameters but you should only use "currentAccessToken"

                        if(Profile.getCurrentProfile() != null) {
                            mProfile = Profile.getCurrentProfile();

                            mBundle = new Bundle();
                            mBundle.putParcelable(PARCEL_KEY, Profile.getCurrentProfile());

                            Intent myIntent = new Intent(UserLoginActivity.this,UserMainActivity.class);
                            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            myIntent.putExtras(mBundle);
                            UserLoginActivity.this.startActivity(myIntent);
                            finish();
                        }
                    }
                }
            };
            AccessToken.refreshCurrentAccessTokenAsync();
        }

        mFacebookButton.setReadPermissions("email");
        mFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                mAccessToken = loginResult.getAccessToken();
                mProfile = Profile.getCurrentProfile();

                // User signed in.
                preferences = PreferenceManager.getDefaultSharedPreferences(UserLoginActivity.this);
                editor = preferences.edit();
                editor.putString("userLogin","true");
                editor.apply();

                mBundle = new Bundle();
                mBundle.putParcelable(PARCEL_KEY, mProfile);

                Intent myIntent = new Intent(UserLoginActivity.this,UserMainActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                myIntent.putExtras(mBundle);
                UserLoginActivity.this.startActivity(myIntent);
                finish();
            }

            @Override
            public void onCancel() {
                Log.v("facebook - onCancel", "cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                Log.v("facebook - onError", e.getMessage());
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == 3) {
//            setResult(2);
//            finish();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAccessTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if(!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent myIntent = new Intent(UserLoginActivity.this,UserMainActivity.class);
//                setResult(2);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                UserLoginActivity.this.startActivity(myIntent);
                finish();
            } else {
                mEmailView.setError(getString(R.string.error_incorrect_email));
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}