package com.aau.wimb.whereismybike;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TRACK_BIKE = "trackBike";

    private GoogleMap mMap;
    private String trackBike;
    private String[] sepString = new String[2];

    private String vin;
    private double lat;
    private double longt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle mTrackBundle = getIntent().getExtras();

        if(mTrackBundle != null){
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

        // Current Coordinates.
        LatLng mBike = new LatLng(lat, longt);

        // Create and add marker, the position of the bike
        MarkerOptions markerOptions = new MarkerOptions().position(mBike).title("Your Bike: " + vin);
        mMap.addMarker(markerOptions);

        // Move the camera of Google Maps above the bike's position.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15));


    }
}
