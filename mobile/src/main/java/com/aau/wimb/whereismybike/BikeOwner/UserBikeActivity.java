package com.aau.wimb.whereismybike.BikeOwner;

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

    // UI references.
    private TextView mBikeVin;
    private TextView mBikeBrand;
    private TextView mBikeColor;
    private TextView mBikeLock;
    private TextView mBikeStatus;
    private TextView mBikeAccess;
    private TextView mBikeLatitude;
    private TextView mBikeLongitude;

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

        if(mBundle != null){
            newBike = mBundle.getString(NEW_BIKE);
            if(newBike.equals("true")){
                mDeleteBikeLayout.setVisibility(View.INVISIBLE);
                mLockBikeLayout.setVisibility(View.INVISIBLE);
                mUnlockBikeLayout.setVisibility(View.INVISIBLE);
                mAllowBikeLayout.setVisibility(View.INVISIBLE);
                mDenyBikeLayout.setVisibility(View.INVISIBLE);
                mStolenBikeLayout.setVisibility(View.INVISIBLE);
                mFoundBikeLayout.setVisibility(View.INVISIBLE);
            } else {
                getSupportActionBar().setTitle(R.string.title_activity_edit_bike);
                mSaveBikeLayout.setVisibility(View.INVISIBLE);
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
}
