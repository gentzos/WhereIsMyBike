package com.aau.wimb.whereismybike;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aau.wimb.whereismybike.BikeOwner.UserLoginActivity;
import com.aau.wimb.whereismybike.BikeOwner.UserMainActivity;
import com.aau.wimb.whereismybike.Police.PoliceLoginActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LaunchActivity extends AppCompatActivity {

    // To track the tokens
    private AccessTokenTracker mAccessTokenTracker = null;

    // Facebook
    private CallbackManager mCallbackManager;

//    To check if user had log in.
    private String userLogin;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        setupActionBar();

//        Uncomment to get Facebook HASH key. Then comment it again.
//        printKeyHash();

        Button mBikeOwnerButton = (Button) findViewById(R.id.bike_owner_btn);
        Button mPoliceButton = (Button) findViewById(R.id.police_btn);

//        Facebook Initialize.
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

//        To check if user had log in.
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userLogin = preferences.getString("userLogin", "");
        Log.e("Login", userLogin);
        if(!userLogin.equals("false"))
        {
            Intent myIntent = new Intent(LaunchActivity.this, UserMainActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LaunchActivity.this.startActivity(myIntent);
            finish();
        }

        mBikeOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LaunchActivity.this, UserLoginActivity.class);
//                myIntent.putExtra("accountInfo", "bikeOwner");
                LaunchActivity.this.startActivity(myIntent);
//                startActivityForResult(myIntent,2);
                }
        });

        mPoliceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent myIntent = new Intent(LaunchActivity.this, PoliceLoginActivity.class);
//                    myIntent.putExtra("accountInfo", "police");
                    LaunchActivity.this.startActivity(myIntent);
//                    startActivityForResult(myIntent,2);
                }
            });
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Hide the action bar.
            getSupportActionBar().hide();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == 2) {
//            finish();
//        }
    }

    // Call this method inside onCreate once, to get your hash key.
    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.aau.wimb.whereismybike", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("WiMB", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

}
