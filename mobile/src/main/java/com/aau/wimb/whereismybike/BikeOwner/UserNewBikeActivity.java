package com.aau.wimb.whereismybike.BikeOwner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aau.wimb.whereismybike.R;

import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class UserNewBikeActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    private TextView mBikeNfc;
    private EditText mEditBikeVin;
    private EditText mEditBikeBrand;
    private EditText mEditBikeColor;
    private Button mSaveBikeButton;

    // NFC/security variables.
    private NfcAdapter mNfcAdapter;
    private Key publicKey, privateKey;
    private static int nfcCounter = 0;

    private View mNormalView;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_new_bike);

        mBikeNfc = (TextView) findViewById(R.id.bikeNfc);

        mEditBikeVin = (EditText) findViewById(R.id.edit_vin);
        mEditBikeBrand = (EditText) findViewById(R.id.edit_brand);
        mEditBikeColor = (EditText) findViewById(R.id.edit_color);

        mNormalView = findViewById(R.id.normal_bike_scroll);
        mProgressView = findViewById(R.id.nfc_progress);

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

//        showProgress(true);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("RSA Works! \n\n" + "Beam Time: " + System.currentTimeMillis());
        String sendingText = "";
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, privateKey);
            encodedBytes = c.doFinal(text.getBytes());
            sendingText = "Key: " + "\n"+ Base64.encodeToString(publicKey.getEncoded(),Base64.NO_WRAP) +"\n"+ "Encrypted Text: " +"\n"+ Base64.encodeToString(encodedBytes, Base64.NO_WRAP);
        } catch (Exception e) {
        }
        NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord("application/@string/nfc_address", sendingText.getBytes())});
        return msg;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            showProgress(false);
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {

        byte[] decodedBytes = null;
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        Log.e("NFCCCC", "YYYYY");

//         record 0 contains the MIME type, record 1 is the AAR, if present
        String recString = new String(msg.getRecords()[0].getPayload());
        String[] stringSplit = recString.split("\n");
        String pubKey = stringSplit[1];
        String cipherText = stringSplit[3];

        byte[] pubByte = Base64.decode(pubKey,Base64.NO_WRAP);
        byte[] cipTextByte = Base64.decode(cipherText,Base64.NO_WRAP);
        try{
            KeyFactory kf = KeyFactory.getInstance("RSA");
            Key pubKeyObe = kf.generatePublic(new X509EncodedKeySpec(pubByte));
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, pubKeyObe);
            decodedBytes = c.doFinal(cipTextByte);
        }catch(Exception e){

        }

        Log.e("WORKS", "Key: " +pubKey+"\n\n"+"Cipher Text: "+ cipherText+"\n\n"+new String(decodedBytes));
    }

    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     *
     * @param mimeType
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
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
}
