package com.aau.wimb.whereismybike.BikeOwner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aau.wimb.whereismybike.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class UserNewBikeActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, AdapterView.OnItemSelectedListener {

    private String urlJsonObjRegisterBike = "http://192.168.0.104:3000/wimb/bike";
    public static final String NEW_BIKE = "new_bike";
    private TextView mBikeNfc;
    private EditText mEditBikeVin;
    private Spinner mBikeBrand, mBikeColor;
    private Button mSaveBikeButton;

    // NFC variables.
    private NfcAdapter mNfcAdapter;
    private static Key publicKey, privateKey, bDPublicKey;
    private static int nfcCounter = 0;
    private byte[] pubByte;
    private static String plainBikeId;

    private View mNormalView;
    private View mProgressView;

    private String userId, brandSelected, colorSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_new_bike);

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            userId = mBundle.getString(UserMainActivity.USER_ID);
        }

        mBikeNfc = (TextView) findViewById(R.id.bikeNfc);

        mEditBikeVin = (EditText) findViewById(R.id.edit_vin);
        mBikeBrand = (Spinner) findViewById(R.id.edit_brand);
        mBikeColor = (Spinner) findViewById(R.id.edit_color);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterBrand = ArrayAdapter.createFromResource(this,
                R.array.brands_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterColor = ArrayAdapter.createFromResource(this,
                R.array.colors_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterBrand.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mBikeBrand.setAdapter(adapterBrand);
        mBikeColor.setAdapter(adapterColor);

        mBikeBrand.setOnItemSelectedListener(this);
        mBikeColor.setOnItemSelectedListener(this);

        mNormalView = findViewById(R.id.normal_bike_scroll);
        mProgressView = findViewById(R.id.nfc_progress);

        mSaveBikeButton = (Button) findViewById(R.id.save_button);

        mBikeNfc.setText("Please make NFC contact with your Bike. \n Step 1/3.");

        mSaveBikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegisterBike();
            }
        });

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        publicKey = null;
        privateKey = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        } catch (Exception e) {
        }

        showProgress(true);
    }

    public void submitBikeFormula() {
        RequestQueue queue = Volley.newRequestQueue(getBaseContext());          // Instantiate the RequestQueue.
        JSONObject attributes = new JSONObject();
        try {
            attributes.put("brand", brandSelected);
            attributes.put("color", colorSelected);
            attributes.put("vin", mEditBikeVin.getText().toString());
            attributes.put("bikeId", plainBikeId);
            attributes.put("ownerId", userId);

            Log.d("Daniel", attributes.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String attrString = attributes.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlJsonObjRegisterBike,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("wimb", response);
                        if (response.equals("success")) {
//                        Toast.makeText(getBaseContext(), "Please make NFC contact again", Toast.LENGTH_LONG).show();
                            showProgress(true);
                            mBikeNfc.setText("Please make NFC contact again. \n  Step 2/3.");
                        } else {
                            Toast.makeText(getBaseContext(), "Device already registered", Toast.LENGTH_LONG).show();
                            nfcCounter = 0;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return attrString == null ? null : attrString.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", attrString, "utf-8");
                    return null;
                }
            }
        };
        queue.add(stringRequest);   // Add the request to the RequestQueue.

    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        nfcCounter++;
        Log.d("wimb", "" + nfcCounter);
        String sendingText = "";
        NdefMessage msg = new NdefMessage(new NdefRecord[]{createMimeRecord("application/@string/nfc_address", sendingText.getBytes())});
        if (nfcCounter == 2) {
            String appKeyString = Base64.encodeToString(publicKey.getEncoded(), Base64.NO_WRAP);
            byte[] encodedBytes = null;
            try {
                Cipher c = Cipher.getInstance("RSA");
                c.init(Cipher.ENCRYPT_MODE, bDPublicKey);
                String bdPubKey = Base64.encodeToString(bDPublicKey.getEncoded(), Base64.NO_WRAP);
                Log.d("wimb", "was here 7 bdPubKey: " + bdPubKey);
                encodedBytes = c.doFinal(userId.getBytes());//sendingText.getBytes());
                Log.d("wimb", "was here 8");
            } catch (Exception e) {
            }
            String cipherText = Base64.encodeToString(encodedBytes, Base64.NO_WRAP);
            Log.d("wimb", "was here 9" + "\n" + cipherText);
            sendingText = "AppPubKey: " + "\n" + appKeyString + "\n" + "CipherOwnerId: " + "\n" + cipherText;
            Log.d("wimb", "was here 9" + "\n" + sendingText);
            mBikeNfc.post(new Runnable() {
                public void run() {
                    mBikeNfc.setText("Please make the final NFC contact. \n Step 3/3.");
                }
            });

            msg = new NdefMessage(new NdefRecord[]{createMimeRecord("application/@string/nfc_address", sendingText.getBytes())});
            Log.e("NEWBIKE","Ndef: " + msg + sendingText + " GetBytes: " + sendingText.getBytes());
        }
        return msg;
    }

    // Creates a custom MIME type encapsulated in an NDEF record // @param mimeType
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    //THIS IT NEEDED FOR NFC CONTACT.
    //Parses the NDEF Message from the intent.
    void processIntent(Intent intent) {
        if (nfcCounter == 1) {
            showProgress(false);

            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage) rawMsgs[0];                 // only one message sent during the beam
            String text = new String(msg.getRecords()[0].getPayload()); // record 0 contains the MIME type, record 1 is the AAR, if present
            Log.d("Daniel", "Print Text" + text);
            byte[] decodedBytes = null;
            String[] stringSplit = text.split("\n");
            String pubKey = stringSplit[1];
            plainBikeId = stringSplit[3];
            pubByte = Base64.decode(pubKey, Base64.NO_WRAP);
            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");

                bDPublicKey = kf.generatePublic(new X509EncodedKeySpec(pubByte));
            } catch (Exception e) {
            }

        } else if (nfcCounter == 3) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage) rawMsgs[0];                 // only one message sent during the beam
            String text = new String(msg.getRecords()[0].getPayload()); // record 0 contains the MIME type, record 1 is the AAR, if present
            Log.d("Daniel", "Print Text " + text);
            byte[] encryptedTextByte = Base64.decode(text, Base64.NO_WRAP);
            byte[] dencodedBytesAppPubKey = null;
            byte[] dencodedBytesBDPriKey = null;
            try {
                // 1. Dencrypt with application private key
                Cipher c1 = Cipher.getInstance("RSA");
                c1.init(Cipher.DECRYPT_MODE, privateKey);
                dencodedBytesAppPubKey = c1.doFinal(encryptedTextByte);
                Log.d("wimb", "first decryption success");

                // 2. Dencrypt with bike devices public key
                Cipher c2 = Cipher.getInstance("RSA");
                c2.init(Cipher.DECRYPT_MODE, bDPublicKey);
                dencodedBytesBDPriKey = c2.doFinal(dencodedBytesAppPubKey);
                Log.d("wimb", "first decryption success - Registration is a success!");

//                String registerFinal = new SecretKeySpec(dencodedBytesBDPriKey,0,dencodedBytesBDPriKey.length,"AES").toString();

                String registerFinal = new String(dencodedBytesBDPriKey);
                Log.e("SYMETRIC NFC", registerFinal);

                if (registerFinal.equals("true")) {
                    Log.e("SYMETRIC NFC2", registerFinal);

                    // Finalize registration.
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UserNewBikeActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("BikeNew", registerFinal);
                    editor.apply();
                }
            } catch (Exception e) {
            }
            Intent myIntent = new Intent(UserNewBikeActivity.this, UserMainActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(myIntent);
            finish();
        }
    }

    /**
     * Shows the progress UI and hides the normal form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mNormalView.setVisibility(show ? View.GONE : View.VISIBLE);
            mNormalView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mNormalView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mNormalView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void attemptRegisterBike() {
        View focusView = null;
        boolean cancel = false;

        // Reset errors.
        mEditBikeVin.setError(null);
        // Store values at the time of the register bike attempt.
        String vin = mEditBikeVin.getText().toString();

        // Check for a valid vin number.
        if (TextUtils.isEmpty(vin)) {
            mEditBikeVin.setError(getString(R.string.error_field_required));
            focusView = mEditBikeVin;
            cancel = true;
        }
        else if (!isVinValid(vin)) {
            mEditBikeVin.setError(getString(R.string.error_invalid_vin));
            focusView = mEditBikeVin;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            submitBikeFormula();
        }
    }

    private boolean isVinValid(String vin) {
        return vin.length() > 5;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.edit_brand:
                brandSelected = parent.getItemAtPosition(position).toString();
                break;

            case R.id.edit_color:
                colorSelected = parent.getItemAtPosition(position).toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

//    private boolean isPasswordValid(String password) {
//        //TODO: Replace this with your own logic
//        return password.length() > 4;
//    }
}
