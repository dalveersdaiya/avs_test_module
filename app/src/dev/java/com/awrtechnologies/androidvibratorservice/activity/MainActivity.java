package com.awrtechnologies.androidvibratorservice.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.awrtechnologies.androidvibratorservice.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        applyClickListeners();
        loadIMEI();
    }

    public void findViews() {
        button = (Button) findViewById(R.id.button_get_info);
    }

    public void applyClickListeners() {
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_get_info:
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Called when the 'loadIMEI' function is triggered.
     */
    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.
            requestReadPhoneStatePermission();
        } else {
            // READ_PHONE_STATE permission is already been granted.
            doPermissionGrantedStuffs();
        }
    }

    /**
     * Requests the READ_PHONE_STATE permission.
     * If the permission has been denied previously, a dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                doPermissionGrantedStuffs();
            } else {
            }
        }
    }

    public void doPermissionGrantedStuffs() {
        //Have an  object of TelephonyManager
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Get IMEI Number of Phone  //////////////// for this example i only need the IMEI
        String IMEINumber = tm.getDeviceId();
        String subscriberID = tm.getDeviceId();
        String SIMSerialNumber = tm.getSimSerialNumber();
        String networkCountryISO = tm.getNetworkCountryIso();
        String SIMCountryISO = tm.getSimCountryIso();
        String softwareVersion = tm.getDeviceSoftwareVersion();
        String voiceMailNumber = tm.getVoiceMailNumber();
        int phoneType = tm.getPhoneType();
        switch (phoneType) {
            case (TelephonyManager.PHONE_TYPE_CDMA):
                // your code
                break;
            case (TelephonyManager.PHONE_TYPE_GSM):
                // your code
                break;
            case (TelephonyManager.PHONE_TYPE_NONE):
                // your code
                break;
        }

//        boolean isRoaming=tm.isNetworkRoaming();
//        if(isRoaming)
//            phoneDetails+="\nIs In Roaming : "+"YES";
//        else
//            phoneDetails+="\nIs In Roaming : "+"NO";


        //Get the SIM state
        int SIMState = tm.getSimState();
        switch (SIMState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                // your code
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                // your code
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                // your code
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                // your code
                break;
            case TelephonyManager.SIM_STATE_READY:
                // your code
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                // your code
                break;

        }
        Log.d("Daiya", "Get Device IMEINumber" + IMEINumber);
        Log.d("Daiya", "Get Device subscriberID" + subscriberID);
        Log.d("Daiya", "Get Device SIMSerialNumber" + SIMSerialNumber);
        Log.d("Daiya", "Get Device networkCountryISO" + networkCountryISO);
        Log.d("Daiya", "Get Device SIMCountryISO" + SIMCountryISO);
        Log.d("Daiya", "Get Device softwareVersion" + softwareVersion);
        Log.d("Daiya", "Get Device voiceMailNumber" + voiceMailNumber);
        Log.d("Daiya", "Get Device phoneType" + phoneType);



    }
}
