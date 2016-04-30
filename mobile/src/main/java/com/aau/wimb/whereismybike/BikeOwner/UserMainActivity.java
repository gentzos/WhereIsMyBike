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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class UserMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private int id;

    private TextView mName;
    private TextView mEmail;
    private View mView;
    private ImageView mProfileImage;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
//    private CallbackManager callbackManager;

    private Profile mProfile = null;
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

        mView =  navigationView.getHeaderView(0);
        mName = (TextView)mView.findViewById(R.id.nameView);
//        mEmail = (TextView)mView.findViewById(R.id.emailView);
//        mProfileImage = (ImageView)mView.findViewById(R.id.profileImageView);

        Bike obj = new Bike("OD2F894NCUJEHDJM", "csd", "black", true, true, "none", 55.650299, 12.540938);
        bikes.add(0, obj);
        Bike obj1 = new Bike("OD2F894N", "cd0", "red", false, false, "someone", 65.650299, 22.540938);
        bikes.add(1, obj1);
        Bike obj2 = new Bike("OD2F894N", "cd2", "pink", true, true, "someone", 75.650299, 32.540938);
        bikes.add(2, obj2);
        Bike obj3 = new Bike("OD2F894N", "cd3", "blue", false, false, "none", 85.650299, 42.540938);
        bikes.add(3, obj3);

        // Set the home as default
        fragment = new UserBikesFragment().newInstance(user, bikes);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();

        if (mBundle != null) {
            mProfile = (Profile) mBundle.getParcelable(UserLoginActivity.PARCEL_KEY);

            user.setUniqueId(mProfile.getId());
            user.setProfileLink(mProfile.getLinkUri().toString());
            user.setProfilePic(mProfile.getProfilePictureUri(50, 50).toString());
            user.setFirstName(mProfile.getFirstName());
            user.setLastName(mProfile.getLastName());

            mName.setText(user.getFirstName() + " " + user.getLastName());

            ProfilePictureView profilePictureView;
            profilePictureView = (ProfilePictureView)mView.findViewById(R.id.fbProfileImage);
            profilePictureView.setProfileId(mProfile.getId());


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
}
