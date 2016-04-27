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
    private TextView mBikeName;
    private TextView mBikeId;
    private TextView mBikeStatus;
    private TextView mBikeAccess;
    private TextView mBikeLatitude;
    private TextView mBikeLongitude;

    private FrameLayout mSaveBikeLayout;
    private FrameLayout mDeleteBikeLayout;
    private FrameLayout mChangeBikeNameLayout;
    private FrameLayout mLockBikeLayout;
    private FrameLayout mUnlockBikeLayout;
    private FrameLayout mAllowBikeLayout;
    private FrameLayout mDenyBikeLayout;

    private Button mSaveBikeButton;
    private Button mDeleteBikeButton;
    private Button mChangeBikeNameButton;
    private Button mLockBikeButton;
    private Button mUnlockBikeButton;
    private Button mAllowBikeButton;
    private Button mDenyBikeButton;

    private String[] sepString = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bike);

        Bundle mBundle = getIntent().getExtras();

        // Set up the UI.
        mBikeName = (TextView) findViewById(R.id.bikeNameChange);
        mBikeId = (TextView) findViewById(R.id.bikeIdChange);
        mBikeStatus = (TextView) findViewById(R.id.bikeStatusChange);
        mBikeAccess = (TextView) findViewById(R.id.bikeAccessChange);
        mBikeLatitude = (TextView) findViewById(R.id.bikeLatitudeChange);
        mBikeLongitude = (TextView) findViewById(R.id.bikeLongitudeChange);

        mSaveBikeLayout = (FrameLayout) findViewById(R.id.save_button_frame);
        mDeleteBikeLayout = (FrameLayout) findViewById(R.id.delete_button_frame);
        mChangeBikeNameLayout = (FrameLayout) findViewById(R.id.change_name_button_frame);
        mLockBikeLayout = (FrameLayout) findViewById(R.id.lock_button_frame);
        mUnlockBikeLayout = (FrameLayout) findViewById(R.id.unlock_button_frame);
        mAllowBikeLayout = (FrameLayout) findViewById(R.id.allow_button_frame);
        mDenyBikeLayout = (FrameLayout) findViewById(R.id.deny_button_frame);

        mSaveBikeButton = (Button) findViewById(R.id.save_button);
        mDeleteBikeButton = (Button) findViewById(R.id.delete_button);
        mChangeBikeNameButton = (Button) findViewById(R.id.change_name_button);
        mLockBikeButton = (Button) findViewById(R.id.lock_button);
        mUnlockBikeButton = (Button) findViewById(R.id.unlock_button);
        mAllowBikeButton = (Button) findViewById(R.id.allow_button);
        mDenyBikeButton = (Button) findViewById(R.id.deny_button);

        if(mBundle != null){
            newBike = mBundle.getString(NEW_BIKE);
            if(newBike.equals("true")){
                mDeleteBikeLayout.setVisibility(View.INVISIBLE);
                mLockBikeLayout.setVisibility(View.INVISIBLE);
                mUnlockBikeLayout.setVisibility(View.INVISIBLE);
                mAllowBikeLayout.setVisibility(View.INVISIBLE);
                mDenyBikeLayout.setVisibility(View.INVISIBLE);
            } else {
                getSupportActionBar().setTitle(R.string.title_activity_edit_bike);
                mSaveBikeLayout.setVisibility(View.INVISIBLE);
                sepString = newBike.split("\\-");

                mBikeName.setText(sepString[0]);
                mBikeId.setText(sepString[1]);
                mBikeStatus.setText(sepString[2]);
                mBikeAccess.setText(sepString[3]);
                mBikeLatitude.setText(sepString[4]);
                mBikeLongitude.setText(sepString[5]);

                if(mBikeStatus.getText().equals("Locked")){
                    mLockBikeLayout.setVisibility(View.INVISIBLE);
                    mBikeStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if(mBikeStatus.getText().equals("Unlocked")) {
                    mUnlockBikeLayout.setVisibility(View.INVISIBLE);
                    mBikeStatus.setTextColor(getResources().getColor(R.color.colorAccent));
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
