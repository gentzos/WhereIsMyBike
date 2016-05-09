package com.aau.wimb.whereismybike.BikeOwner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aau.wimb.whereismybike.R;

public class UserBikeActivity extends AppCompatActivity {

    public static final String NEW_BIKE = "newBike";
    private String newBike;

    private UserLoginTask mAuthTask = null;

    // UI references.
    private TextView mBikeVin;
    private TextView mBikeBrand;
    private TextView mBikeColor;
    private TextView mBikeLock;
    private TextView mBikeStatus;
    private TextView mBikeAccess;
    private TextView mBikeLatitude;
    private TextView mBikeLongitude;
    private TextView mBikeNfc;

    private FrameLayout mSaveBikeLayout;
    private FrameLayout mDeleteBikeLayout;
    private FrameLayout mLockBikeLayout;
    private FrameLayout mUnlockBikeLayout;
    private FrameLayout mAllowBikeLayout;
    private FrameLayout mDenyBikeLayout;
    private FrameLayout mStolenBikeLayout;
    private FrameLayout mFoundBikeLayout;

    private Button mSaveBikeButton;
    private Button mDeleteBikeButton;
    private Button mChangeBikeNameButton;
    private Button mLockBikeButton;
    private Button mUnlockBikeButton;
    private Button mAllowBikeButton;
    private Button mDenyBikeButton;
    private Button mStolenBikeButton;
    private Button mFoundBikeButton;

    private View mNormalView;
    private View mProgressView;

    private String[] sepString = new String[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bike);

        Bundle mBundle = getIntent().getExtras();

        // Set up the UI.
        mBikeVin = (TextView) findViewById(R.id.bikeVinChange);
        mBikeBrand = (TextView) findViewById(R.id.bikeBrandChange);
        mBikeColor = (TextView) findViewById(R.id.bikeColorChange);
        mBikeLock = (TextView) findViewById(R.id.bikeLockChange);
        mBikeStatus = (TextView) findViewById(R.id.bikeStatusChange);
        mBikeAccess = (TextView) findViewById(R.id.bikeAccessChange);
        mBikeLatitude = (TextView) findViewById(R.id.bikeLatitudeChange);
        mBikeLongitude = (TextView) findViewById(R.id.bikeLongitudeChange);
        mBikeNfc = (TextView) findViewById(R.id.bikeNfc);

        mSaveBikeLayout = (FrameLayout) findViewById(R.id.save_button_frame);
        mDeleteBikeLayout = (FrameLayout) findViewById(R.id.delete_button_frame);
        mLockBikeLayout = (FrameLayout) findViewById(R.id.lock_button_frame);
        mUnlockBikeLayout = (FrameLayout) findViewById(R.id.unlock_button_frame);
        mAllowBikeLayout = (FrameLayout) findViewById(R.id.allow_button_frame);
        mDenyBikeLayout = (FrameLayout) findViewById(R.id.deny_button_frame);
        mStolenBikeLayout = (FrameLayout) findViewById(R.id.stolen_button_frame);
        mFoundBikeLayout = (FrameLayout) findViewById(R.id.found_button_frame);

        mSaveBikeButton = (Button) findViewById(R.id.save_button);
        mDeleteBikeButton = (Button) findViewById(R.id.delete_button);
        mLockBikeButton = (Button) findViewById(R.id.lock_button);
        mUnlockBikeButton = (Button) findViewById(R.id.unlock_button);
        mAllowBikeButton = (Button) findViewById(R.id.allow_button);
        mDenyBikeButton = (Button) findViewById(R.id.deny_button);
        mStolenBikeButton = (Button) findViewById(R.id.stolen_button);
        mFoundBikeButton = (Button) findViewById(R.id.found_button);

        mNormalView = findViewById(R.id.normal_bike_scroll);
        mProgressView = findViewById(R.id.nfc_progress);

        if(mBundle != null){
            newBike = mBundle.getString(NEW_BIKE);
            if(newBike.equals("true")){
                showProgress(true);
                mAuthTask = new UserLoginTask();
                mAuthTask.execute((Void) null);

                mDeleteBikeLayout.setVisibility(View.INVISIBLE);
                mLockBikeLayout.setVisibility(View.INVISIBLE);
                mUnlockBikeLayout.setVisibility(View.INVISIBLE);
                mAllowBikeLayout.setVisibility(View.INVISIBLE);
                mDenyBikeLayout.setVisibility(View.INVISIBLE);
                mStolenBikeLayout.setVisibility(View.INVISIBLE);
                mFoundBikeLayout.setVisibility(View.INVISIBLE);



            } else {
                showProgress(false);

                getSupportActionBar().setTitle(R.string.title_activity_edit_bike);
                mSaveBikeLayout.setVisibility(View.INVISIBLE);
                mBikeNfc.setVisibility(View.INVISIBLE);
                sepString = newBike.split("\\-");

                mBikeVin.setText(sepString[0]);
                mBikeBrand.setText(sepString[1]);
                mBikeColor.setText(sepString[2]);
                mBikeLock.setText(sepString[3]);
                mBikeStatus.setText(sepString[4]);
                mBikeAccess.setText(sepString[5]);
                mBikeLatitude.setText(sepString[6]);
                mBikeLongitude.setText(sepString[7]);

                if(mBikeLock.getText().equals("Locked")){
                    mLockBikeLayout.setVisibility(View.INVISIBLE);
                    mBikeLock.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    mUnlockBikeLayout.setVisibility(View.INVISIBLE);
                    mBikeLock.setTextColor(getResources().getColor(R.color.colorAccent));
                }

                if(mBikeStatus.getText().equals("Stolen")){
                    mStolenBikeLayout.setVisibility(View.INVISIBLE);
                    mBikeStatus.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    mFoundBikeLayout.setVisibility(View.INVISIBLE);
                    mBikeStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if(mBikeAccess.getText().equals("None")){
                    mDenyBikeLayout.setVisibility(View.INVISIBLE);
                    mBikeAccess.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    mAllowBikeLayout.setVisibility(View.INVISIBLE);
                    mBikeAccess.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        }

    }

    /**
     * Shows the progress UI and hides the normal form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mNormalView.setVisibility(show ? View.GONE : View.VISIBLE);
            mNormalView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mNormalView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mNormalView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        UserLoginTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            /**
             * Gets current device state and checks for working internet
             * connection by trying Google.
             **/
//            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo netInfo = cm.getActiveNetworkInfo();
//            if (netInfo != null && netInfo.isConnected()) {
//                try {
//                    URL url = new URL("http://www.google.com");
//                    HttpURLConnection urlc = (HttpURLConnection) url
//                            .openConnection();
//                    urlc.setConnectTimeout(3000);
//                    urlc.connect();
//                    if (urlc.getResponseCode() == 200) {
//                        return true;
//                    }
//                } catch (MalformedURLException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
//            showProgress(false);

            if (success) {
//                Intent myIntent = new Intent(UserLoginActivity.this, UserMainActivity.class);
////                setResult(2);
//                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                UserLoginActivity.this.startActivity(myIntent);
//                finish();
//                mEmailView = (EditText) findViewById(R.id.email);

//                mEmail = mEmailView.getText().toString();
//                mPassword = mPasswordView.getText().toString();
//
//                StringRequest postRequest = new StringRequest(Request.Method.POST, urlJsonObjLogin,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                // response
//                                Log.e("Response", response);
//
//                                if (response.equals("Success")) {
////                                    mBundle = new Bundle();
////                                    mBundle.putParcelable(PARCEL_KEY, profile);
//
//                                    Intent myIntent = new Intent(UserLoginActivity.this, UserMainActivity.class);
//                                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                                    myIntent.putExtras(mBundle);
//                                    UserLoginActivity.this.startActivity(myIntent);
//                                    finish();
//                                } else {
//                                    mEmailView.setError(getString(R.string.error_incorrect_email));
//                                    mPasswordView.setError(getString(R.string.error_incorrect_password));
//                                    focusView = mEmailView;
//                                    focusView = mPasswordView;
//                                }
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                // error
//                                Log.e("Error.Response", String.valueOf(error));
//                            }
//                        }
//                ) {
//                    @Override
//                    protected Map<String, String> getParams() {
//                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("email", mEmail);
//                        params.put("password", mPassword);
//
//                        return params;
//                    }
//                };
//                queue.add(postRequest);
            } else {
//                Toast.makeText(getApplicationContext(),
//                        "Error in Network Connection", Toast.LENGTH_SHORT)
//                        .show();
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
}
