package com.aau.wimb.whereismybike.BikeOwner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aau.wimb.whereismybike.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class UserBikeActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    private String urlJsonObjReportStolen = "http://192.168.0.104:3000/wimb/reportStolen";
    private String urlJsonObjReportFound = "http://192.168.0.104:3000/wimb/reportFound";

    // UI references.
    private TextView mBikeVin;
    private TextView mBikeBrand;
    private TextView mBikeColor;
    private TextView mBikeLock;
    private TextView mBikeStatus;
    private TextView mBikeAccess;
    private TextView mBikeLatitude;
    private TextView mBikeLongitude;

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

    private String editBike;
    private Bundle mBundle = new Bundle();
    private String[] sepString = new String[7];

    private String locked, status;
    private String symKeyString = "null";
    SecretKeySpec symKey;

    // NFC variables.
    private NfcAdapter mNfcAdapter;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bike);

        queue = Volley.newRequestQueue(this);

        mBundle = getIntent().getExtras();

        // Set up the UI.
        mBikeVin = (TextView) findViewById(R.id.bikeVinChange);
        mBikeBrand = (TextView) findViewById(R.id.bikeBrandChange);
        mBikeColor = (TextView) findViewById(R.id.bikeColorChange);
        mBikeLock = (TextView) findViewById(R.id.bikeLockChange);
        mBikeStatus = (TextView) findViewById(R.id.bikeStatusChange);
        mBikeAccess = (TextView) findViewById(R.id.bikeAccessChange);
        mBikeLatitude = (TextView) findViewById(R.id.bikeLatitudeChange);
        mBikeLongitude = (TextView) findViewById(R.id.bikeLongitudeChange);

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

        // A bike was selected to show.
        if (mBundle != null) {
            editBike = mBundle.getString(MyAdapter.EDIT_BIKE);

            sepString = editBike.split("\\-");
            mBikeVin.setText(sepString[0]);
            mBikeBrand.setText(sepString[1]);
            mBikeColor.setText(sepString[2]);
            symKeyString = sepString[3];
            Toast.makeText(UserBikeActivity.this, sepString[3], Toast.LENGTH_SHORT).show();
            mBikeLock.setText(sepString[4]);
            mBikeStatus.setText(sepString[5]);
            mBikeAccess.setText(sepString[6]);
            mBikeLatitude.setText(sepString[7]);
            mBikeLongitude.setText(sepString[8]);

            if ((mBikeLock.getText().equals("Locked")) || (mBikeLock.getText().equals("true"))) {
                locked = "true";
                mLockBikeLayout.setVisibility(View.INVISIBLE);
                mBikeLock.setTextColor(getResources().getColor(R.color.colorPrimary));
                mBikeLock.setText("Locked");
            } else {
                locked = "false";
                mUnlockBikeLayout.setVisibility(View.INVISIBLE);
                mBikeLock.setTextColor(getResources().getColor(R.color.colorAccent));
                mBikeLock.setText("Unlocked");
            }

            if ((mBikeStatus.getText().equals("Stolen")) || (mBikeStatus.getText().equals("true"))) {
                status = "true";
                mStolenBikeLayout.setVisibility(View.INVISIBLE);
                mBikeStatus.setTextColor(getResources().getColor(R.color.colorAccent));
                mBikeStatus.setText("Stolen");
            } else {
                status = "false";
                mFoundBikeLayout.setVisibility(View.INVISIBLE);
                mBikeStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                mBikeStatus.setText("Safe");
            }

            if ((mBikeAccess.getText().equals("None")) || (mBikeAccess.getText().equals("none"))) {
                mDenyBikeLayout.setVisibility(View.INVISIBLE);
                mBikeAccess.setTextColor(getResources().getColor(R.color.colorPrimary));
                mBikeAccess.setText("None");
            } else {
                mAllowBikeLayout.setVisibility(View.INVISIBLE);
                mBikeAccess.setTextColor(getResources().getColor(R.color.colorAccent));
                mBikeAccess.setText("None");
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String reportStolen = preferences.getString("ReportStolen", "");

        if (reportStolen.equals("true")) {

            reportBike(urlJsonObjReportStolen);

            // Restart the register bike process.
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ReportStolen", "false");
            editor.apply();
        }

        mStolenBikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportBike(urlJsonObjReportStolen);
            }
        });

        mFoundBikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportBike(urlJsonObjReportFound);
            }
        });

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this); // Check for available NFC Adapter
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mNfcAdapter.setNdefPushMessageCallback(this, this); // Register callback
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        byte[] encodedBytes = null;
        String cipherText = null;

        if (!symKeyString.equals("null")) {
            byte[] symKeyByteArray = Base64.decode(symKeyString, Base64.NO_WRAP);
            symKey = new SecretKeySpec(symKeyByteArray, 0, symKeyByteArray.length, "AES");
            Log.e("SYMETRIC_KEY", Base64.encodeToString(symKey.getEncoded(), Base64.NO_WRAP));
        }

        if ((locked.equals("true")) || (locked.equals("false"))) {
            try {
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.ENCRYPT_MODE, symKey);
                encodedBytes = c.doFinal(locked.getBytes());
                Log.e("Encoded Bytes", encodedBytes + " " + locked.getBytes());
                cipherText = Base64.encodeToString(encodedBytes, Base64.NO_WRAP);
            } catch (Exception e) {
                Log.e("ERROR_AES", "AES encryption error");
            }
        }

        mBikeLock.post(new Runnable() {
            public void run() {
                if (locked.equals("true")) {
                    mBikeLock.setText("Unlocked");
                    mBikeLock.setTextColor(getResources().getColor(R.color.colorAccent));
                    mUnlockBikeLayout.setVisibility(View.INVISIBLE);
                    mLockBikeLayout.setVisibility(View.VISIBLE);
                } else {
                    mBikeLock.setText("Locked");
                    mBikeLock.setTextColor(getResources().getColor(R.color.colorPrimary));
                    mLockBikeLayout.setVisibility(View.INVISIBLE);
                    mUnlockBikeLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        NdefMessage msg = new NdefMessage(new NdefRecord[]{createMimeRecord("application/@string/nfc_address", cipherText.getBytes())});
        Log.e("NdefMessage", msg + " ");
        return msg;
    }

    // Creates a custom MIME type encapsulated in an NDEF record // @param mimeType
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Finalize registration.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UserBikeActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("BikeEdit2", "true");
        editor.apply();
        finish();
    }

    private void reportBike(String url){
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("success")) {
                            if (status.equals("false")) {
                                mBikeStatus.setText("Stolen");
                                mBikeStatus.setTextColor(getResources().getColor(R.color.colorAccent));
                                mFoundBikeLayout.setVisibility(View.VISIBLE);
                                mStolenBikeLayout.setVisibility(View.INVISIBLE);
                            } else {
                                mBikeStatus.setText("Safe");
                                mBikeStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                                mFoundBikeLayout.setVisibility(View.INVISIBLE);
                                mStolenBikeLayout.setVisibility(View.VISIBLE);
                            }
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
                params.put("VIN", sepString[0]);
                return params;
            }
        };
        queue.add(postRequest);
    }
}
