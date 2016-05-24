package com.aau.wimb.whereismybike;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TRACK_BIKE = "trackBike";
    private String urlJsonObjTrack = "http://192.168.0.104:3000/wimb/track";

    private GoogleMap mMap;
    private String trackBike;
    private String[] sepString = new String[2];

    private String vin;
    private double lat;
    private double longt;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Instantiate the RequestQueue. It is for the database!
        queue = Volley.newRequestQueue(this);

        Bundle mTrackBundle = getIntent().getExtras();

        if (mTrackBundle != null) {
            trackBike = mTrackBundle.getString(TRACK_BIKE);
            sepString = trackBike.split("\\-");

            vin = sepString[0];
            lat = Double.parseDouble(sepString[1]);
            longt = Double.parseDouble(sepString[2]);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Current Coordinates.
        LatLng mBike = new LatLng(lat, longt);

        // Create and add marker, the position of the bike
        MarkerOptions markerOptions = new MarkerOptions().position(mBike).title("Your Bike: " + vin);
        mMap.addMarker(markerOptions);

        // Move the camera of Google Maps above the bike's position.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15));

        callAsyncTask(mBike);
    }

    public void callAsyncTask(final LatLng mBike) {
        final LatLng[] mBike1 = {mBike};
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            StringRequest postRequest = new StringRequest(Request.Method.POST, urlJsonObjTrack,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            Log.e("New Coordinates", response);

                                            String[] coordinates = response.split(",");
                                            double newLat = Double.parseDouble(coordinates[0]);
                                            double newLongt = Double.parseDouble(coordinates[1]);

                                            // Current Coordinates.
                                            LatLng mBike2 = new LatLng(newLat, newLongt);

                                            if (mBike2 != mBike1[0]) {
                                                // Create and add marker, the position of the bike
                                                MarkerOptions markerOptions = new MarkerOptions().position(mBike2).
                                                        title("Your Bike: " + vin);
                                                mMap.addMarker(markerOptions);

                                                // Move the camera of Google Maps above the bike's position.
                                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.
                                                        getPosition(), 15));

                                                mBike1[0] = mBike2;
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
                                    params.put("VIN", vin);

                                    return params;
                                }
                            };
                            queue.add(postRequest);
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsyncTask, 0, 10000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
