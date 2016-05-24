package com.aau.wimb.whereismybike.BikeOwner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aau.wimb.whereismybike.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A register screen that offers register via claims.
 */
public class UserRegisterActivity extends AppCompatActivity {

    private String urlJsonObjRegister = "http://192.168.0.104:3000/wimb/register";
    public static final String REGISTER_USER = "register_key";

    /**
     * Keep track of the register task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailView;
    private EditText mConfirmEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mAddressView;
    private EditText mPhoneNumberView;
    private View mProgressView;
    private View mRegisterFormView;
    private View focusView = null;

    private RequestQueue queue;
    private Bundle mBundle;

    // To check if user had log in.
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        setupActionBar();

        // Instantiate the RequestQueue. It is for the database!
        queue = Volley.newRequestQueue(this);

        mFirstNameView = (EditText) findViewById(R.id.first_name);
        mLastNameView = (EditText) findViewById(R.id.last_name);
        mEmailView = (EditText) findViewById(R.id.email);
        mConfirmEmailView = (EditText) findViewById(R.id.email_cfm);
        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPasswordView = (EditText) findViewById(R.id.password_cfm);
        mAddressView = (EditText) findViewById(R.id.address);
        mPhoneNumberView = (EditText) findViewById(R.id.phone_number);
        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        mPhoneNumberView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
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

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mConfirmEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        mAddressView.setError(null);
        mPhoneNumberView.setError(null);

        // Store values at the time of the register attempt.
        String fName = mFirstNameView.getText().toString();
        String lName = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String cfmEmail = mConfirmEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String cfmPassword = mConfirmPasswordView.getText().toString();
        String address = mAddressView.getText().toString();
        String pNumber = mPhoneNumberView.getText().toString();

        boolean cancel = false;

        // Check for a mobile number.
        if (TextUtils.isEmpty(pNumber)) {
            pNumber = "none";
        }

        // Check for an address.
        if (TextUtils.isEmpty(address)) {
            address = "none";
        }

        // Check for password and confirm password fields to match.
        if (TextUtils.isEmpty(cfmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_field_required));
            focusView = mConfirmPasswordView;
            cancel = true;
        } else if (!TextUtils.equals(password, cfmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_password_not_match));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

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

        // Check for email and confirm email fields to match.
        if (TextUtils.isEmpty(cfmEmail)) {
            mConfirmEmailView.setError(getString(R.string.error_field_required));
            focusView = mConfirmEmailView;
            cancel = true;
        } else if (!TextUtils.equals(email, cfmEmail)) {
            mConfirmEmailView.setError(getString(R.string.error_email_not_match));
            focusView = mConfirmEmailView;
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

        // Check for a last name.
        if (TextUtils.isEmpty(lName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        // Check for a first name.
        if (TextUtils.isEmpty(fName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(fName, lName, email, password, address, pNumber);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    /**
     * Shows the progress UI and hides the register form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private String mFirstName;
        private String mLastName;
        private String mEmail;
        private String mPassword;
        private String mAddress;
        private String mPNumber;

        UserRegisterTask(String fName, String lName, String email, String password, String address, String pNumber) {
            mFirstName = fName;
            mLastName = lName;
            mEmail = email;
            mPassword = password;
            mAddress = address;
            mPNumber = pNumber;
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
                mFirstName = mFirstNameView.getText().toString();
                mLastName = mLastNameView.getText().toString();

                StringRequest postRequest = new StringRequest(Request.Method.POST, urlJsonObjRegister,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.e("Response", response);

                                if (response.equals("error")) {
                                    mEmailView.setError(getString(R.string.error_incorrect_email));
                                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                                    focusView = mEmailView;
                                    focusView = mPasswordView;

                                } else {
                                    mBundle = new Bundle();
                                    mBundle.putString(REGISTER_USER, response);

                                    // User registers.
                                    preferences = PreferenceManager.getDefaultSharedPreferences(UserRegisterActivity.this);
                                    editor = preferences.edit();
                                    editor.putString("userLogin", "register");
                                    editor.apply();

                                    Intent myIntent = new Intent(UserRegisterActivity.this, UserMainActivity.class);
                                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    myIntent.putExtras(mBundle);
                                    UserRegisterActivity.this.startActivity(myIntent);
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
                        params.put("pw", mPassword);
                        params.put("fName", mFirstName);
                        params.put("lName", mLastName);
                        return params;
                    }
                };
                queue.add(postRequest);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Error in Network Connection", Toast.LENGTH_SHORT)
                        .show();
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
}
