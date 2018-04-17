package com.awrtechnologies.androidvibratorservice.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.awrtechnologies.androidvibratorservice.service.BackgroundLocationService;
import com.awrtechnologies.androidvibratorservice.service.GoogleDefaults;
import com.awrtechnologies.androidvibratorservice.utility.CheckNetwork;
import com.awrtechnologies.androidvibratorservice.service.MyService;
import com.awrtechnologies.androidvibratorservice.R;
import com.awrtechnologies.androidvibratorservice.utility.GeneralHelper;
import com.awrtechnologies.androidvibratorservice.utility.Utils;

public class MyLocation extends AppCompatActivity implements View.OnClickListener {

    private Button mStartUpdatesButton;
    private Button mPauseUpdatesButton;
    private Button buttonCamouflage;
    private Button buttonSetPin;
    private Button buttonStopUpdates;

    TextView tv_dual_sim;
    TextView tv_imei_main;
    TextView tv_imei_secondary;
    TextView tv_is_sim_one_ready;
    TextView tv_is_sim_two_ready;
    TextView tv_network_name;
    TextView tv_mobile_number;
    TextView tv_battery_percent;
    TextView tv_date_time;
    TextView tv_location;
    TextView tv_location_accuracy;
    TextView tv_location_bearing;
    TextView tv_location_altitude;

    String dual_sim = "";
    String imei_main = "";
    String imei_secondary = "";
    String is_sim_one_ready = "";
    String is_sim_two_ready = "";
    String network_name = "";
    String mobile_number = "";
    String battery_percent = "";
    String date_time = "";
    String location = "";
    String location_accuracy = "";
    String location_bearing = "";
    String location_altitude = "";

    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    boolean isInternetPresent = false;

    Point point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_my_location);
        findViews ();

        applyClickListeners ();
        mStartUpdatesButton.setEnabled (false);
//        startService (new Intent (this, MyService.class));
        updateUI ();

        try {
            registerReceiver (UiUpdated, new IntentFilter ("LOCATION_UPDATED"));

//            checkForPermission ();
        } catch (Exception e) {

        }


    }

    public void findViews() {
        mStartUpdatesButton = (Button) findViewById (R.id.start_updates_button);
        mPauseUpdatesButton = (Button) findViewById (R.id.pause_updates_button);
        tv_dual_sim = (TextView) findViewById (R.id.tv_dual_sim);
        tv_imei_main = (TextView) findViewById (R.id.tv_imei_main);
        tv_imei_secondary = (TextView) findViewById (R.id.tv_imei_secondary);
        tv_is_sim_one_ready = (TextView) findViewById (R.id.tv_is_sim_one_ready);
        tv_is_sim_two_ready = (TextView) findViewById (R.id.tv_is_sim_two_ready);
        tv_network_name = (TextView) findViewById (R.id.tv_network_name);
        tv_mobile_number = (TextView) findViewById (R.id.tv_mobile_number);
        tv_battery_percent = (TextView) findViewById (R.id.tv_battery_percent);
        tv_date_time = (TextView) findViewById (R.id.tv_date_time);
        tv_location = (TextView) findViewById (R.id.tv_location);
        tv_location_accuracy = (TextView) findViewById (R.id.tv_location_accuracy);
        tv_location_bearing = (TextView) findViewById (R.id.tv_location_bearing);
        tv_location_altitude = (TextView) findViewById (R.id.tv_location_altitude);
        buttonCamouflage = (Button) findViewById (R.id.button_camouflage);
        buttonSetPin = (Button) findViewById (R.id.button_set_pin);
        buttonStopUpdates = (Button) findViewById (R.id.button_stop_updates);

    }

    public void applyClickListeners() {
        buttonCamouflage.setOnClickListener (this);
        mStartUpdatesButton.setOnClickListener (this);
        mPauseUpdatesButton.setOnClickListener (this);
        buttonSetPin.setOnClickListener (this);
        buttonStopUpdates.setOnClickListener (this);
    }

    private void updateUI() {
        if (Utils.isMyServiceRunning (this, BackgroundLocationService.class)) {
            mStartUpdatesButton.setEnabled (false);
            mPauseUpdatesButton.setEnabled (true);
            buttonStopUpdates.setEnabled (true);
            buttonCamouflage.setEnabled (true);
        } else {
            mStartUpdatesButton.setEnabled (true);
            mPauseUpdatesButton.setEnabled (false);
            buttonCamouflage.setEnabled (false);
            buttonStopUpdates.setEnabled (false);
        }
    }

    private BroadcastReceiver UiUpdated = new BroadcastReceiver () {

        @Override
        public void onReceive(Context context, Intent intent) {
            setUpdatedData (intent);
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId ()) {

            case R.id.start_updates_button:
                Intent intent = new Intent (MyLocation.this, BackgroundLocationService.class);
                startService (intent);
                updateUI ();
                break;

            case R.id.pause_updates_button:
                Intent intent1 = new Intent (MyLocation.this, BackgroundLocationService.class);
                stopService (intent1);
                updateUI ();
                break;

            case R.id.button_camouflage:
                if (checkIfAppIsHiddenMode ()) {
                    onBackPressed ();
                    Toast.makeText (MyLocation.this, "Hiding the app.", Toast.LENGTH_SHORT).show ();
                } else {
                    setHideApplication (MyLocation.this, true);
                    Toast.makeText (MyLocation.this, "Please wait..!! This will take a few seconds.", Toast.LENGTH_SHORT).show ();
                    setAppHiddenStatus (true);
                }

                break;

            case R.id.button_set_pin:
                dialogSetPin (MyLocation.this);
                break;

            case R.id.button_stop_updates:
                dialogStopUpdate (MyLocation.this);
                break;
        }
    }


    public static void setHideApplication(Context c, boolean hide) {
        ComponentName cn = new ComponentName (c.getApplicationContext (),
                Splash.class);
        int setting = hide ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                : PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        int current = c.getPackageManager ().getComponentEnabledSetting (cn);
        if (current != setting) {
            c.getPackageManager ().setComponentEnabledSetting (cn, setting,
                    PackageManager.DONT_KILL_APP);
        }
    }

    public void setUpdatedData(Intent intent) {
        tv_location.setText (intent.getExtras ().getString ("tvLocation"));
        tv_dual_sim.setText (intent.getExtras ().getString ("tv_dual_sim"));
        tv_imei_main.setText (intent.getExtras ().getString ("tv_imei_main"));
        tv_imei_secondary.setText (intent.getExtras ().getString ("tv_imei_secondary"));
        tv_is_sim_one_ready.setText (intent.getExtras ().getString ("tv_is_sim_one_ready"));
        tv_is_sim_two_ready.setText (intent.getExtras ().getString ("tv_is_sim_two_ready"));
        tv_network_name.setText (intent.getExtras ().getString ("tv_network_name"));
        tv_mobile_number.setText (intent.getExtras ().getString ("tv_mobile_number"));
        tv_battery_percent.setText (intent.getExtras ().getString ("tv_battery_percent"));
        tv_date_time.setText (intent.getExtras ().getString ("tv_date_time"));
        tv_location.setText (intent.getExtras ().getString ("tv_location"));
        tv_location_accuracy.setText (intent.getExtras ().getString ("tv_location_accuracy"));
        tv_location_bearing.setText (intent.getExtras ().getString ("tv_location_bearing"));
        tv_location_altitude.setText (intent.getExtras ().getString ("tv_location_altitude"));
    }

    public void checkForPermission() {
        // API 23: we have to check if ACCESS_FINE_LOCATION and/or ACCESS_COARSE_LOCATION permission are granted
        if (ContextCompat.checkSelfPermission (this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mStartUpdatesButton.setEnabled (true);

        } else {

            if (ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions (this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            // The ACCESS_FINE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions (this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStartUpdatesButton.setEnabled (true);
                } else {
                    // permission denied
                }
                break;

            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    mStartUpdatesButton.setEnabled (true);
                } else {
                    // permission denied

                }
                break;


        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService (CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo ();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected ();
    }

    @Override
    protected void onPause() {
        super.onPause ();
//        unregisterReceiver(networkStateReceiver);
        try {
            unregisterReceiver (networkReceiver);
            unregisterReceiver (UiUpdated);
        } catch (Exception e) {
            e.printStackTrace ();
        }
//
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        try {
            unregisterReceiver (networkReceiver);
            unregisterReceiver (UiUpdated);
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }


    @Override
    protected void onResume() {
        super.onResume ();
        try {
            updateUI ();
//        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
            IntentFilter filter = new IntentFilter (ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver (networkReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace ();
        }

//
    }

    /*
   * Method for Receiving the Network State
   */
    private BroadcastReceiver networkReceiver = new BroadcastReceiver () {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            String status = CheckNetwork.getConnectivityStatusString (context);
            Log.d ("vyom", "onReceive status===" + status);
            if (status.equals ("WIFI") || status.equals ("MOBILE")) {
                isInternetPresent = true;
            } else if (status.equals ("No Connection")) {
                isInternetPresent = false;
            }
        }
    };

    public void dialogSetPin(Context context) {
        point = GeneralHelper.getInstance (this).getScreenSize ();
        final Dialog dialog = new Dialog (context);
        dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialog.setCancelable (true);
        dialog.setContentView (R.layout.dialog_enter_pin);
        Button buttonCancel = (Button) dialog.findViewById (R.id.button_cancel);
        final TextView tvPin = (TextView) dialog.findViewById (R.id.tv_pin);
        if (chechIfPinChanged ()) {
            tvPin.setText (getMyPin ());
        } else {
            tvPin.setText (getMyPin ());
        }

        buttonCancel.setVisibility (View.VISIBLE);
        final EditText etPassWord = (EditText) dialog.findViewById (R.id.et_enter_password);
        etPassWord.addTextChangedListener (new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length () == 0) {
                    tvPin.setText (getMyPin ());
                }
                if (s.length () >= 1) {
                    tvPin.setText ("##" + etPassWord.getText ().toString ());
                }
            }
        });


        dialog.findViewById (R.id.button_ok).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                if (etPassWord.getText ().toString () != null || !etPassWord.getText ().toString ().isEmpty ()) {
//                    Intent intent = new Intent (BarcodeScanner.this, MyLocation.class);
//                    startActivity (intent);
                    if (etPassWord.getText ().length () != 4) {
                        Toast.makeText (MyLocation.this, "Enter at least 4 digits.", Toast.LENGTH_SHORT).show ();
                    } else {
                        dialog.dismiss ();
                        setUpPin (etPassWord.getText ().toString ());
                    }


                } else {
                    Toast.makeText (MyLocation.this, "Enter a pin.", Toast.LENGTH_SHORT).show ();
                }

            }
        });

        buttonCancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Toast.makeText (MyLocation.this, "You may change the pin later.", Toast.LENGTH_SHORT).show ();
                dialog.dismiss ();
            }
        });
        dialog.show ();
        Window window = dialog.getWindow ();
        window.setBackgroundDrawableResource (R.color.transparent);
        window.setLayout (point.x / 2, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    public void dialogStopUpdate(Context context) {
        point = GeneralHelper.getInstance (this).getScreenSize ();
        final Dialog dialog = new Dialog (context);
        dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialog.setCancelable (true);
        dialog.setContentView (R.layout.dialog);

        dialog.findViewById (R.id.button_ok).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                dialog.dismiss ();

                Intent intent1 = new Intent (MyLocation.this, BackgroundLocationService.class);
                stopService (intent1);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (MyLocation.this);
                SharedPreferences.Editor editor = preferences.edit ();
                editor.putString ("accessToken", "");
                editor.putString ("ps_employee_id", "");
                editor.putString ("id", "");
                editor.apply ();

                Toast.makeText (MyLocation.this, "Auth cleared. Let's start again.", Toast.LENGTH_SHORT).show ();

                Intent intent = new Intent (MyLocation.this, BarcodeScanner.class);
                intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity (intent);
            }
        });

        dialog.findViewById (R.id.button_cancel).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Toast.makeText (MyLocation.this, "You may stop updates later.", Toast.LENGTH_SHORT).show ();
                dialog.dismiss ();
            }
        });
        dialog.show ();
        Window window = dialog.getWindow ();
        window.setBackgroundDrawableResource (R.color.transparent);
        window.setLayout (point.x / 2, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    public void setAppHiddenStatus(boolean isHidden) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (MyLocation.this);
        SharedPreferences.Editor editor = preferences.edit ();
        editor.putBoolean ("isHidden", isHidden);
        editor.apply ();
    }

    public void setUpPin(String pin) {
        String myPin = "##" + pin;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (MyLocation.this);
        SharedPreferences.Editor editor = preferences.edit ();
        editor.putString ("myPin", myPin);
        editor.putBoolean ("pinChanged", true);
        editor.apply ();
    }

    public boolean checkIfAppIsHiddenMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (MyLocation.this);
        boolean isHidden = preferences.getBoolean ("isHidden", false);
        return isHidden;
    }

    public boolean chechIfPinChanged() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (MyLocation.this);
        boolean isPinChanged = preferences.getBoolean ("pinChanged", false);
        return isPinChanged;
    }

    public String getMyPin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (MyLocation.this);
        String myPin = preferences.getString ("myPin", "##1111");
        return myPin;
    }


//    public void saveDataInSharedPreferences(Intent intent) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("tv_location", intent.getExtras().getString("tvLocation"));
//        editor.putString("tv_dual_sim", intent.getExtras().getString("tv_dual_sim"));
//        editor.putString("tv_imei_main", intent.getExtras().getString("tv_imei_main"));
//        editor.putString("tv_imei_secondary", intent.getExtras().getString("tv_imei_secondary"));
//        editor.putString("tv_is_sim_one_ready", intent.getExtras().getString("tv_is_sim_one_ready"));
//        editor.putString("tv_is_sim_two_ready", intent.getExtras().getString("tv_is_sim_two_ready"));
//        editor.putString("tv_network_name", intent.getExtras().getString("tv_network_name"));
//        editor.putString("tv_mobile_number", intent.getExtras().getString("tv_mobile_number"));
//        editor.putString("tv_battery_percent", intent.getExtras().getString("tv_battery_percent"));
//        editor.putString("tv_date_time", intent.getExtras().getString("tv_date_time"));
//        editor.putString("tv_location_accuracy", intent.getExtras().getString("tv_location_accuracy"));
//        editor.putString("tv_location_bearing", intent.getExtras().getString("tv_location_bearing"));
//        editor.putString("tv_location_altitude", intent.getExtras().getString("tv_location_altitude"));
//
//        editor.apply();
//    }
//
//    public void getDataFromSharedPreferences() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String name = preferences.getString("Name", null);
//        if (name != null) {
//
//        }
//        location = preferences.getString("tv_location", null);
//        dual_sim = preferences.getString("tv_dual_sim", null);
//        imei_main = preferences.getString("tv_imei_main", null);
//        imei_secondary = preferences.getString("tv_imei_secondary", null);
//        is_sim_one_ready = preferences.getString("tv_is_sim_one_ready", null);
//        is_sim_two_ready = preferences.getString("tv_is_sim_two_ready", null);
//        network_name = preferences.getString("tv_network_name", null);
//        mobile_number = preferences.getString("tv_mobile_number", null);
//        battery_percent = preferences.getString("tv_battery_percent", null);
//        date_time = preferences.getString("tv_date_time", null);
//        location_accuracy = preferences.getString("tv_location_accuracy", null);
//        location_bearing = preferences.getString("tv_location_bearing", null);
//        location_altitude = preferences.getString("tv_location_altitude", null);
//
//    }


}
