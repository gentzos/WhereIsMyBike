package com.aau.wimb.whereismybike.BikeOwner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.aau.wimb.whereismybike.Bike;
import com.aau.wimb.whereismybike.R;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class UserLoginActivity extends AppCompatActivity {

    // json object response url
    private String urlJsonObjLogin = "http://192.168.0.104:3000/wimb/login";
    private String urlJsonObjFb = "http://192.168.0.104:3000/wimb/facebookLogin";

    public static final String LOGIN_USER = "login_key";
    public static final String FBLOGIN_USER = "fblogin_key";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mSignInButton;
    private Button mRegisterButton;
    private Button mForgotPasswordButton;
    private LoginButton mFacebookButton;
    private View focusView = null;

    private TextView fbInfo;

    // To check if user had log in.
    private String userLogin = "false";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Bundle mBundle = new Bundle();
    private String bikeUserString, bikeString;

    // Facebook
    private CallbackManager mCallbackManager = null;
    private AccessTokenTracker mAccessTokenTracker = null;
    private ProfileTracker mProfileTracker = null;

    private RequestQueue queue;

    String[] user = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        setupActionBar();

        // Instantiate the RequestQueue. It is for the database!
        queue = Volley.newRequestQueue(this);

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
        fbInfo = (TextView) findViewById(R.id.info);
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
                Intent myIntent = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
//                startActivityForResult(myIntent,3);
                UserLoginActivity.this.startActivity(myIntent);
            }
        });

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // For testing purposes. Must be commented.
//                preferences = PreferenceManager.getDefaultSharedPreferences(UserLoginActivity.this);
//                editor = preferences.edit();
//                editor.putString("userLogin", "normal");
//                editor.apply();
//
//                Intent myIntent = new Intent(UserLoginActivity.this, UserMainActivity.class);
//                UserLoginActivity.this.startActivity(myIntent);
//                finish();

                // Actual action
                attemptLogin();
            }
        });

        // Check if user had log in.
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userLogin = preferences.getString("userLogin", "");
        Log.e("Login", userLogin);
//        if(!userLogin.equals("false"))
//        {
//            Intent myIntent = new Intent(UserLoginActivity.this, UserMainActivity.class);
//            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            UserLoginActivity.this.startActivity(myIntent);
//            finish();
//        }

        AccessToken();

        mFacebookButton.setReadPermissions("email");
        mFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            Log.v("facebook - profile", profile2.getFirstName());
                            Redirect(profile2);
                            mProfileTracker.stopTracking();
                        }
                    };
                    mProfileTracker.startTracking();
                } else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getFirstName());
                    Redirect(profile);
                }
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
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

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
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
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        String mEmail;
        String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            /**
             * Gets current device state and checks for working internet
             * connection by trying Google.
             **/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url
                            .openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                mEmail = mEmailView.getText().toString();
                mPassword = mPasswordView.getText().toString();

                StringRequest postRequest = new StringRequest(Request.Method.POST, urlJsonObjLogin,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.e("Response_NORMAL", response);

                                if (response.equals("error")) {
                                    mEmailView.setError(getString(R.string.error_incorrect_email));
                                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                                    focusView = mEmailView;
                                    focusView = mPasswordView;

                                } else {
                                    mBundle.putString(LOGIN_USER, response);

                                    // User signed in with normal login.
                                    preferences = PreferenceManager.getDefaultSharedPreferences(UserLoginActivity.this);
                                    editor = preferences.edit();
                                    editor.putString("userLogin", "normal");
                                    editor.apply();

//                                    dbBikes(response);
                                    Intent myIntent = new Intent(UserLoginActivity.this, UserMainActivity.class);
                                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    myIntent.putExtras(mBundle);
                                    UserLoginActivity.this.startActivity(myIntent);
                                    finish();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.e("Error.Response", String.valueOf(error));
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", mEmail);
                        params.put("password", mPassword);

                        return params;
                    }
                };
                queue.add(postRequest);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error in Network Connection", Toast.LENGTH_SHORT)
                        .show();
//                mEmailView.setError(getString(R.string.error_incorrect_email));
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    // A method to check if there is a valid access token for Facebook Login.
    private void AccessToken() {
        if (AccessToken.getCurrentAccessToken() != null) {
            mAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    mAccessTokenTracker.stopTracking();
                    if (currentAccessToken == null) {
                        // The user has revoked permissions or deleted the app.
                        fbInfo.setText("You have been signed out, please login again.");
                    } else {
                        // New access token received.
                        GraphRequest request = GraphRequest.newMeRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        // this is where you should have the profile
                                        Log.v("fetched info", object.toString());
                                        Redirect(Profile.getCurrentProfile());
                                    }
                                });
                        Bundle parameters = new Bundle();
                        // Required fields.
                        parameters.putString("fields", "id,first_name,last_name,link,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }
                }
            };
            mAccessTokenTracker.startTracking();
            AccessToken.refreshCurrentAccessTokenAsync();
        }
    }

    // Communicate with database and redirect to MainActivity.
    private void Redirect(final Profile profile) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlJsonObjFb,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Response_FACEBOOK", response);

                        mBundle.putParcelable(FBLOGIN_USER, profile);
                        mBundle.putString(LOGIN_USER, response);

                        // User signed in with facebook login.
                        preferences = PreferenceManager.getDefaultSharedPreferences(UserLoginActivity.this);
                        editor = preferences.edit();
                        editor.putString("userLogin", "facebook");
                        editor.apply();

//                        dbBikes(response);
                        Intent myIntent = new Intent(UserLoginActivity.this, UserMainActivity.class);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        myIntent.putExtras(mBundle);
                        UserLoginActivity.this.startActivity(myIntent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", String.valueOf(error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("_id", profile.getId());
                params.put("fName", profile.getFirstName());
                params.put("lName", profile.getLastName());

                return params;
            }
        };
        queue.add(postRequest);
    }
}