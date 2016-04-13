package com.aau.wimb.whereismybike;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aau.wimb.whereismybike.BikeOwner.UserLoginActivity;
import com.aau.wimb.whereismybike.BikeOwner.UserMainActivity;
import com.aau.wimb.whereismybike.Police.PoliceLoginActivity;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("user", "");
        if(!name.equalsIgnoreCase(""))
        {
            Intent myIntent = new Intent(LaunchActivity.this, UserMainActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LaunchActivity.this.startActivity(myIntent);
            finish();
        } else {
            setContentView(R.layout.activity_launch);
            setupActionBar();

            Button mBikeOwnerButton = (Button) findViewById(R.id.bike_owner_btn);
            mBikeOwnerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(LaunchActivity.this, UserLoginActivity.class);
////                myIntent.putExtra("accountInfo", "bikeOwner");
//                    LaunchActivity.this.startActivity(myIntent);
                    startActivityForResult(myIntent,2);
                }
            });

            Button mPoliceButton = (Button) findViewById(R.id.police_btn);
            mPoliceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(LaunchActivity.this, PoliceLoginActivity.class);
////                myIntent.putExtra("accountInfo", "police");
//                    LaunchActivity.this.startActivity(myIntent);
                    startActivityForResult(myIntent,2);
                }
            });
        }
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
        if (resultCode == 2) {
            finish();
        }
    }
}
