package com.aau.wimb.whereismybike.BikeOwner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.aau.wimb.whereismybike.Bike;
import com.aau.wimb.whereismybike.LaunchActivity;
import com.aau.wimb.whereismybike.OnFragmentInteractionListener;
import com.aau.wimb.whereismybike.R;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginFragment;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private int id;
    private String bikeString, userLogin;

    private TextView mName;
    private TextView mEmail;
    private View mView;
    private ImageView mProfileImage;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
//    private CallbackManager callbackManager;

    private Profile mProfile = null;
    private String loginString, registerString;
    private UserAccount user = new UserAccount();
    private ArrayList bikes = new ArrayList<Bike>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle mBundle = getIntent().getExtras();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        mView = navigationView.getHeaderView(0);
        mName = (TextView) mView.findViewById(R.id.nameView);
//        mEmail = (TextView)mView.findViewById(R.id.emailView);
//        mProfileImage = (ImageView)mView.findViewById(R.id.profileImageView);

        // Set the home as default
        fragment = new UserBikesFragment().newInstance(user, bikes);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();

        // For testing purposes. Must be commented.
//        Bike obj = new Bike("wb0256", "Velorbis", "Green", false, false, "none", 55.6527395, 12.5432467);
//        bikes.add(0, obj);

        if (mBundle != null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            userLogin = preferences.getString("userLogin", "");

            if(userLogin.equals("register"))
            {
                registerString = mBundle.getString(UserRegisterActivity.REGISTER_USER);

                String[] userName = registerString.split(",");

                user.setFirstName(userName[0]);
                user.setLastName(userName[1]);

            } else if(userLogin.equals("normal")) {
                loginString = mBundle.getString(UserLoginActivity.LOGIN_USER);
                bikeString = mBundle.getString(UserLoginActivity.BIKE_KEY);

                String[] userName = loginString.split(",");

                user.setFirstName(userName[0]);
                user.setLastName(userName[1]);

                retrieveBikes();

            } else if(userLogin.equals("facebook")) {
                mProfile = (Profile) mBundle.getParcelable(UserLoginActivity.FBLOGIN_USER);
                bikeString = mBundle.getString(UserLoginActivity.BIKE_KEY);

                user.setUniqueId(mProfile.getId());
                user.setProfileLink(mProfile.getLinkUri().toString());
                user.setProfilePic(mProfile.getProfilePictureUri(50, 50).toString());
                user.setFirstName(mProfile.getFirstName());
                user.setLastName(mProfile.getLastName());

                ProfilePictureView profilePictureView;
                profilePictureView = (ProfilePictureView) mView.findViewById(R.id.fbProfileImage);
                profilePictureView.setProfileId(mProfile.getId());

                retrieveBikes();
            }

            mName.setText(user.getFirstName() + " " + user.getLastName());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.bike_owner_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        // Always call the superclass so it can save the view hierarchy state
//        super.onSaveInstanceState(savedInstanceState);
//
//        // Save the user's current game state
//        savedInstanceState.putInt("FRAGMENT", id);
//    }
//
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        // Always call the superclass so it can restore the view hierarchy
//        super.onRestoreInstanceState(savedInstanceState);
//
//        // Restore state members from saved instance
//        id = savedInstanceState.getInt("FRAGMENT");
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        id = item.getItemId();

        if (id == R.id.nav_logout) {

            // Forget the user.
            preferences = PreferenceManager.getDefaultSharedPreferences(UserMainActivity.this);
            editor = preferences.edit();
            editor.putString("userLogin", "false");
            editor.apply();

            // Logout the user from Facebook.
            LoginManager.getInstance().logOut();

            // Redirect to LaunchActivity.
            Intent myIntent = new Intent(UserMainActivity.this, LaunchActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            UserMainActivity.this.startActivity(myIntent);
            finish();
        } else {
            if (id == R.id.nav_account) {
                fragment = new UserAccountFragment();
                setTitle(R.string.title_fragment_bike_user_account);
            } else if (id == R.id.nav_settings) {
                fragment = new UserSettingsFragment();
                setTitle(R.string.title_fragment_bike_user_settings);
            } else {
                fragment = new UserBikesFragment().newInstance(user, bikes);
                setTitle(R.string.title_activity_bike_user_main);
            }

            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content, fragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void retrieveBikes(){
        // If bikes exists show them in the list.
        if (bikeString != "false") {
            Map<String, String> mapBikes = new HashMap<>();
            Map<String, String> map = new HashMap<>();
            String[] entry = new String[2];
            int i = 0;
            int j = 0;

            Pattern pattern = Pattern.compile("\\{(.*?)\\}");
            Matcher matcher = pattern.matcher(bikeString);
            while (matcher.find()) {
                String[] items = matcher.group(1).split(",");

                //iterate over the pairs
                for (String pair : items)
                {
                    Pattern pattern2 = Pattern.compile("\"(.*?)\"");
                    Matcher matcher2 = pattern2.matcher(pair);
                    while (matcher2.find()) {
                        entry[i] = matcher2.group(1);
                        i++;
                    }

                    //add them to the hashmap
                    map.put(entry[0], entry[1]);
                    i = 0;
                }

                Bike obj = new Bike(map.get("VIN"), map.get("brand"), map.get("color"),
                        Boolean.valueOf(map.get("lock")), Boolean.valueOf(map.get("status")),
                        map.get("access"), Double.parseDouble(map.get("latitude")), Double.parseDouble(map.get("longitude")));
                bikes.add(j, obj);
                j++;
            }
        }
    }
}
