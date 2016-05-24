package com.aau.wimb.whereismybike.BikeOwner;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aau.wimb.whereismybike.Bike;
import com.aau.wimb.whereismybike.LaunchActivity;
import com.aau.wimb.whereismybike.MapsActivity;
import com.aau.wimb.whereismybike.OnFragmentInteractionListener;
import com.aau.wimb.whereismybike.R;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    public static final String TRACK_BIKE = "trackBike";
    public static final String EDIT_BIKE = "editBike";
    public static final String USER_ID = "user_id";
    private String urlJsonObjLoginBikes = "http://192.168.0.104:3000/wimb/loginBikes";

    private int SocketServerPortApp = 8080;
    private String AppIpAddress, msgToBike, receivedMessage;
    private String response = "";
    private ServerSocket serverSocket;

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private View mView;
    private SharedPreferences preferences, preferences2;
    private UserAccount user = new UserAccount();
    private ArrayList<Bike> bikes = new ArrayList<Bike>();
    private RequestQueue queue;
    private Bundle mBundle = new Bundle();

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBundle = getIntent().getExtras();

        // Instantiate the RequestQueue. It is for the database!
        queue = Volley.newRequestQueue(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
//            drawer.setDrawerListener(toggle);
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        if (navigationView != null) {
            mView = navigationView.getHeaderView(0);
        }
        TextView mName = (TextView) mView.findViewById(R.id.nameView);
//        mEmail = (TextView)mView.findViewById(R.id.emailView);
        ImageView mProfileImage = (ImageView) mView.findViewById(R.id.profileImageView);
        ProfilePictureView profilePictureView = (ProfilePictureView) mView.findViewById(R.id.fbProfileImage);

        // For testing purposes. Must be commented.
//        Bike obj = new Bike("wb0256", "Velorbis", "Green", false, false, "none", 55.6527395, 12.5432467);
//        bikes.add(0, obj);

        if (mBundle != null) {
            Profile mProfile;
            String bikeString, loginString, registerString;

            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String userLogin = preferences.getString("userLogin", "");
            switch (userLogin) {
                case "register":
                    profilePictureView.setVisibility(View.INVISIBLE);

                    registerString = mBundle.getString(UserRegisterActivity.REGISTER_USER);
                    storeUser(registerString);

                    break;
                case "normal":
                    profilePictureView.setVisibility(View.INVISIBLE);

                    loginString = mBundle.getString(UserLoginActivity.LOGIN_USER);

                    storeUser(loginString);
                    retrieveBikes();

                    break;
                case "facebook":
                    mProfileImage.setVisibility(View.INVISIBLE);

                    mProfile = mBundle.getParcelable(UserLoginActivity.FBLOGIN_USER);
                    loginString = mBundle.getString(UserLoginActivity.LOGIN_USER);

                    storeUser(loginString);

                    if (mProfile != null) {
                        user.setProfileId(mProfile.getId());
                        user.setProfileLink(mProfile.getLinkUri().toString());
                        user.setProfilePic(mProfile.getProfilePictureUri(50, 50).toString());

                        profilePictureView.setProfileId(mProfile.getId());
                    }

                    retrieveBikes();
                    break;
            }

            String name = user.getFirstName() + " " + user.getLastName();
            mName.setText(name);
        }

        AppIpAddress = getIpAddress();
        Log.e("IPaddress", AppIpAddress);
//        msgToBike = "Notification from Bike";

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

//        activationButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                MyClientTask myClientTask = new MyClientTask(BikeIpAddress, SocketClientPortBike, msgToBike);
//                myClientTask.execute();
//            }
//        });

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
    public void onResume() {
        super.onResume();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String bikeNew = preferences.getString("BikeNew", "");
        String bikeEdit = preferences.getString("BikeEdit2", "");
        Log.e("RESUME2", bikeEdit);

        if (bikeNew.equals("true")) {
            deleteBikes();
            retrieveBikes();

            // Restart the register bike process.
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("BikeNew", "false");
            editor.apply();
        }
        if (bikeEdit.equals("true")) {
            deleteBikes();
            retrieveBikes();

            // Restart the register bike process.
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("BikeEdit2", "false");
            editor.apply();
            Log.e("RESUME3", bikeEdit);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
        int id = item.getItemId();

        if (id == R.id.nav_logout) {

            // Forget the user.
            preferences = PreferenceManager.getDefaultSharedPreferences(UserMainActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
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
                new UserBikesFragment();
                fragment = UserBikesFragment.newInstance(user, bikes);
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

    private void storeUser(String string) {
        String[] userName = string.split(",");

        user.setUniqueId(userName[0]);
        user.setFirstName(userName[1]);
        user.setLastName(userName[2]);

        Log.e("ResponseSTOREUSER", userName[0]);
    }

    private void retrieveBikes() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlJsonObjLoginBikes,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("Response_BIKES", response);

                        if (response.equals("error")) {
                            Log.e("Error", response);

                        } else {
                            sortBikes(response);
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
                params.put("_id", user.getUniqueId());

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void sortBikes(String bikeString) {
        // If bikes exists show them in the list.
        if (!bikeString.equals("false")) {
            Map<String, String> mapBikes = new HashMap<>();
            String[] entry = new String[2];
            int i = 0;
            int j = 0;

            // Pattern is curly brackets ({ and }) characters. It finds each JSON object.
            Pattern pattern = Pattern.compile("\\{(.*?)\\}");
            Matcher matcher = pattern.matcher(bikeString);
            while (matcher.find()) {
                // Splits according to comma (,) character. It finds each name/value pair.
                String[] items = matcher.group(1).split(",");

                //iterate over the pairs
                for (String pair : items) {
                    // Pattern is double quotes (" and "). It finds each name and value separately.
                    Pattern pattern2 = Pattern.compile("\"(.*?)\"");
                    Matcher matcher2 = pattern2.matcher(pair);
                    while (matcher2.find()) {
                        entry[i] = matcher2.group(1);
                        i++;
                    }

                    //add them to the hashmap
                    mapBikes.put(entry[0], entry[1]);
                    i = 0;
                }

                Bike obj = new Bike(mapBikes.get("_id"), mapBikes.get("VIN"), mapBikes.get("brand"), mapBikes.get("color"), mapBikes.get("key"),
                        Boolean.valueOf(mapBikes.get("lock")), Boolean.valueOf(mapBikes.get("status")),
                        mapBikes.get("access"), Double.parseDouble(mapBikes.get("latitude")), Double.parseDouble(mapBikes.get("longitude")));

                bikes.add(j, obj);
                j++;
            }
        }
        // Set the home as default
        new UserBikesFragment();
        fragment = UserBikesFragment.newInstance(user, bikes);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }

    public void registerNewBike() {
        mBundle.putString(USER_ID, user.getUniqueId());

        Intent myIntent = new Intent(UserMainActivity.this, UserNewBikeActivity.class);
        myIntent.putExtras(mBundle);
        UserMainActivity.this.startActivity(myIntent);
    }

    private void deleteBikes() {

        bikes.clear();

        // Set the home as default
        new UserBikesFragment();
        fragment = UserBikesFragment.newInstance(user, bikes);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

    private class SocketServerThread extends Thread {
        @Override
        public void run() {
            Socket socket = null;
            DataInputStream dataInputStream = null;

            try {
                serverSocket = new ServerSocket(SocketServerPortApp);

                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());

                    String messageFromClient = "";

                    messageFromClient = dataInputStream.readUTF();

                    receivedMessage = messageFromClient;
                    final String[] movingBike = receivedMessage.split(",");
                    final String bikeId = movingBike[1];
                    final Bike[] tempBike = {new Bike()};

                    UserMainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // custom dialog
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_alert);

                            tempBike[0] = retrieveOneBike(bikeId);

                            dialog.setTitle("Bike VIN No: " + tempBike[0].getVin());

                            // set the custom dialog components - text, image and button
                            TextView text = (TextView) dialog.findViewById(R.id.dialog_text);

                            Button dialogTrackButton = (Button) dialog.findViewById(R.id.dialog_track_button);
                            // if button is clicked, close the custom dialog
                            dialogTrackButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent myIntent = new Intent(v.getContext(),MapsActivity.class);
                                    myIntent.putExtra(TRACK_BIKE, tempBike[0].getVin() + "-" + tempBike[0].getLatitude() + "-" + tempBike[0].getLongitude());
                                    v.getContext().startActivity(myIntent);

                                    dialog.dismiss();
                                }
                            });

                            Button dialogReportButton = (Button) dialog.findViewById(R.id.dialog_report_button);
                            // if button is clicked, close the custom dialog
                            dialogReportButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UserMainActivity.this);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("ReportStolen", "true");
                                    editor.apply();

                                    Intent myIntent = new Intent(v.getContext(),UserBikeActivity.class);
                                    myIntent.putExtra(EDIT_BIKE, tempBike[0].getVin() + "-" + tempBike[0].getBrand()
                                            + "-" + tempBike[0].getColor() + "-" + tempBike[0].getSymetricKey()
                                            + "-" + tempBike[0].isLock() + "-" + tempBike[0].isStatus()
                                            + "-" + tempBike[0].getAccess() + "-" + tempBike[0].getLatitude()
                                            + "-" + tempBike[0].getLongitude());

                                    v.getContext().startActivity(myIntent);

                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    });
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                final String errMsg = e.toString();
                UserMainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(UserMainActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                    }
                });

            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Bike retrieveOneBike(String bikeId){
        Bike tempBike = new Bike();

        for (int i = 0; i < bikes.size(); i++){
            if (bikes.get(i).getId().equals(bikeId)) {
                tempBike.setId(bikes.get(i).getId());
                tempBike.setVin(bikes.get(i).getVin());
                tempBike.setBrand(bikes.get(i).getBrand());
                tempBike.setColor(bikes.get(i).getColor());
                tempBike.setSymetricKey(bikes.get(i).getSymetricKey());
                tempBike.setLock(bikes.get(i).isLock());
                tempBike.setStatus(bikes.get(i).isStatus());
                tempBike.setAccess(bikes.get(i).getAccess());
                tempBike.setLatitude(bikes.get(i).getLatitude());
                tempBike.setLongitude(bikes.get(i).getLongitude());
            }
        }
        return tempBike;
    }
}
