package com.awrtechnologies.androidvibratorservice.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.awrtechnologies.androidvibratorservice.R;
import com.awrtechnologies.androidvibratorservice.utility.TelephonyInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    Button buttonPermissions;
    Button buttoninfo;


    //    For IMEI Data
    private static final int REQUEST_READ_PHONE_STATE = 110,
            REQUEST_ACCESS_FINE_LOCATION = 111,
            REQUEST_ACCESS_COARSE_LOCATION = 112,
            REQUEST_CAMERA_PERMISSION = 113,
            REQUEST_RECEIVE_BOOT_COMPLETED = 114,
            REQUEST_ACCESS_NETWORKSTATE = 115,
            REQUEST_CHANGE_NETWORK_STATE = 116,
            REQUEST_INTERNET = 117,
            REQUEST_PREOCESS_OUTGOING_CALLS = 119;


    //    For Battery Percentage
    int batteryLevel = 0;
    int batteryScale = 0;
    float batteryPercent = 0;

    boolean finelocationpermission = false;
    boolean phonestatepermission = false;
    boolean coarseloationpermission = false;


    private static final int WIFI_ENABLE_REQUEST = 0;
    private static final int Location_ENABLE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main2);
        findViews ();
        applyClickListeners ();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (this);
        boolean hasAllPermission = preferences.getBoolean ("hasAllPermission", false);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && !hasAllPermission) {
            // Marshmallow+
            buttoninfo.setEnabled (false);
            buttonPermissions.setEnabled (true);
        } else {
            buttonPermissions.setEnabled (false);
            setUpNetworkAndLocation ();
        }
    }

    public void findViews() {
        buttoninfo = (Button) findViewById (R.id.button_get_Info);
        buttonPermissions = (Button) findViewById (R.id.button_network_permission);
    }

    public void applyClickListeners() {
        buttoninfo.setOnClickListener (this);
        buttonPermissions.setOnClickListener (this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId ()) {

            case R.id.button_network_permission:
                requestReadPhoneStatePermission ();
                break;

            case R.id.button_get_Info:
                getBatteryPercent ();
                getUserData (true);
                getCurrentSystemDateTime ();
                Intent intent = new Intent (Main2Activity.this, BarcodeScanner.class);
                startActivity (intent);
                break;
        }
    }


    private void requestReadPhoneStatePermission() {

        boolean hasPermissionPhoneState = (ContextCompat.checkSelfPermission (getApplicationContext (),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionPhoneState) {
            ActivityCompat.requestPermissions (this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        }

        boolean hasPermissionLocationFine = (ContextCompat.checkSelfPermission (getApplicationContext (),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionLocationFine) {
            ActivityCompat.requestPermissions (this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
        }


        boolean hasPermissionCamera = (ContextCompat.checkSelfPermission (getApplicationContext (),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionCamera) {
            ActivityCompat.requestPermissions (this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }

        boolean hasbootPermissions = (ContextCompat.checkSelfPermission (getApplicationContext (),
                Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED);
        if (!hasbootPermissions) {
            ActivityCompat.requestPermissions (this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    REQUEST_RECEIVE_BOOT_COMPLETED);
        }

        boolean hasAccessNetwork = (ContextCompat.checkSelfPermission (getApplicationContext (),
                Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasAccessNetwork) {
            ActivityCompat.requestPermissions (this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    REQUEST_ACCESS_NETWORKSTATE);
        }

        boolean hasChangeNetwork = (ContextCompat.checkSelfPermission (getApplicationContext (),
                Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasChangeNetwork) {
            ActivityCompat.requestPermissions (this,
                    new String[]{Manifest.permission.CHANGE_NETWORK_STATE},
                    REQUEST_CHANGE_NETWORK_STATE);
        }

        boolean hasInternet = (ContextCompat.checkSelfPermission (getApplicationContext (),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED);
        if (!hasInternet) {
            ActivityCompat.requestPermissions (this,
                    new String[]{Manifest.permission.INTERNET},
                    REQUEST_INTERNET);
        }


        boolean hasOutGoingCalls = (ContextCompat.checkSelfPermission (getApplicationContext (),
                Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED);
        if (!hasOutGoingCalls) {
            ActivityCompat.requestPermissions (this,
                    new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS},
                    REQUEST_PREOCESS_OUTGOING_CALLS);
        }

        boolean hasPermissionLocationCoarse = (ContextCompat.checkSelfPermission (getApplicationContext (),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionLocationCoarse) {
            ActivityCompat.requestPermissions (this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText (this, "Permission granted.", Toast.LENGTH_SHORT).show ();
                    //reload my activity with permission granted or use the features what required the permission
//                    finish();
                    buttonPermissions.performClick ();
                } else {

                }
            }

            case REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText (this, "Permission granted.", Toast.LENGTH_SHORT).show ();
                    //reload my activity with permission granted or use the features what required the permission
//                    finish();
//                    startActivity(getIntent());
                    buttonPermissions.performClick ();
                } else {

                }
            }
            case REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText (this, "Permission granted.", Toast.LENGTH_SHORT).show ();
                    //reload my activity with permission granted or use the features what required the permission
//                    finish();
//                    startActivity(getIntent());
                    buttonPermissions.performClick ();
                } else {
                }
            }

            case REQUEST_RECEIVE_BOOT_COMPLETED: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText (this, "Permission granted.", Toast.LENGTH_SHORT).show ();
                    //reload my activity with permission granted or use the features what required the permission
//                    finish();
//                    startActivity(getIntent());
                    buttonPermissions.performClick ();
                } else {
                }
            }


            case REQUEST_ACCESS_NETWORKSTATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText (this, "Permission granted.", Toast.LENGTH_SHORT).show ();
                    //reload my activity with permission granted or use the features what required the permission
//                    finish();
//                    startActivity(getIntent());
                    buttonPermissions.performClick ();
                } else {
                }
            }

            case REQUEST_CHANGE_NETWORK_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText (this, "Permission granted.", Toast.LENGTH_SHORT).show ();
                    //reload my activity with permission granted or use the features what required the permission
//                    finish();
//                    startActivity(getIntent());
                    buttonPermissions.performClick ();
                } else {
                }
            }

            case REQUEST_INTERNET: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText (this, "Permission granted.", Toast.LENGTH_SHORT).show ();
                    //reload my activity with permission granted or use the features what required the permission
//                    finish();
//                    startActivity(getIntent());
                    buttonPermissions.performClick ();
                } else {
                }
            }

            case REQUEST_PREOCESS_OUTGOING_CALLS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText (this, "Permission granted.", Toast.LENGTH_SHORT).show ();
                    //reload my activity with permission granted or use the features what required the permission
//                    finish();
//                    startActivity(getIntent());
                    buttonPermissions.performClick ();
                } else {
                }
            }

            case REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText (this, "Permission granted.", Toast.LENGTH_SHORT).show ();
                    //reload my activity with permission granted or use the features what required the permission
//                    finish();
//                    startActivity(getIntent());
//                    setUpLocation ();
                    setUpNetworkAndLocation ();
                    buttonPermissions.setEnabled (false);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (Main2Activity.this);
                    SharedPreferences.Editor editor = preferences.edit ();
                    editor.putBoolean ("hasAllPermission", true);
                    editor.apply ();

                } else {

                }
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        switch (requestCode) {

            case Location_ENABLE_REQUEST:
//                setUpNetwork ();
//                setUpLocation ();
                setUpNetworkAndLocation ();
                break;

            case WIFI_ENABLE_REQUEST:
//                setUpNetwork ();
//                setUpLocation ();
//                buttoninfo.setEnabled (true);
                setUpNetworkAndLocation ();
                break;
        }
    }

    public float getBatteryPercent() {
        IntentFilter ifilter = new IntentFilter (Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver (null, ifilter);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra (BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra (BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        batteryLevel = batteryStatus.getIntExtra (BatteryManager.EXTRA_LEVEL, -1);
        batteryScale = batteryStatus.getIntExtra (BatteryManager.EXTRA_SCALE, -1);

        batteryPercent = batteryLevel / (float) batteryScale;
        return batteryPercent * 100;

    }

    public String getCurrentSystemDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat ("EEE, d MMM yyyy HH:mm:ss");
        String currentDateandTime = sdf.format (new Date ());
        return currentDateandTime;
    }


    public void getUserData(boolean showlogs) {

        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance (this);
        String imsiSIM1 = telephonyInfo.getImsiSIM1 ();
        String imsiSIM2 = telephonyInfo.getImsiSIM2 ();

        boolean isSIM1Ready = telephonyInfo.isSIM1Ready ();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready ();

        boolean isDualSIM = telephonyInfo.isDualSIM ();
        if (showlogs) {
            Log.d ("Daiya", "User Data" + " IME1 : " + imsiSIM1 + "\n" +
                    " IME2 : " + imsiSIM2 + "\n" +
                    " IS DUAL SIM : " + isDualSIM + "\n" +
                    " IS SIM1 READY : " + isSIM1Ready + "\n" +
                    " IS SIM2 READY : " + isSIM2Ready + "\n" +
                    "Network Name : " + telephonyInfo.getcarrierName () + "\n"
                    + "Mobile Number : " + telephonyInfo.getPhoneNumberMain ());

            Log.d ("Daiya", "Battery percentage : " + getBatteryPercent ());
            Log.d ("Daiya", "Battery batteryScale : " + batteryScale);
            Log.d ("Daiya", "Battery batteryLevel : " + batteryLevel);
            Log.d ("Daiya", "getCurrentSystemDateTime : " + getCurrentSystemDateTime ());
        }
    }

    public void setUpLocation() {
        if (!isLocationEnabled (this)) {
//            checkIfGpsEnabled and if not, enable the location
            enableLocation (this);
        }
    }

    public void setUpNetwork() {
        if (!isNetworkAvailable ()) {
//            checkIfGpsEnabled();
            showNoInternetDialog ();
        }
    }

    public void setUpNetworkAndLocation() {
        if (!isLocationEnabled (this)) {
            enableLocation (this);
        } else if (!isNetworkAvailable ()) {
            showNoInternetDialog ();
        } else {
            buttoninfo.setEnabled (true);

        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt (context.getContentResolver (), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace ();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString (context.getContentResolver (), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty (locationProviders);
        }

    }

    public void enableLocation(final Context context) {
        LocationManager lm = (LocationManager) context.getSystemService (Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled (LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled (LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder (context);
            dialog.setMessage ("Location not enabled. Please Enable Location settings.");
            dialog.setPositiveButton (("Open"), new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult (myIntent, Location_ENABLE_REQUEST);
                    //get gps
                }
            });
            dialog.setCancelable (false);
            dialog.show ();
        }
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setTitle ("Internet Disabled!");
        builder.setMessage ("No active Internet connection found.");
        builder.setPositiveButton ("Turn On", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$DataUsageSummaryActivity"));
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                Intent gpsOptionsIntent = new Intent (Settings.ACTION_WIRELESS_SETTINGS);
                startActivityForResult (gpsOptionsIntent, WIFI_ENABLE_REQUEST);
            }
        });
        builder.setCancelable (false);
        builder.create ().show ();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService (CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo ();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected ();
    }

    public static void displayPromptForEnablingGPS(final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder (activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Please enable GPS setting";

        builder.setMessage (message)
                .setPositiveButton ("OK",
                        new DialogInterface.OnClickListener () {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity (new Intent (action));
                                d.dismiss ();
                            }
                        });
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface d, int id) {
//                                d.cancel();
//                            }
//                        });
        builder.create ().show ();
    }

    public void checkIfGpsEnabled() {
//        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
//        startActivity(intent);
        String GpsProvider = Settings.Secure.getString (getContentResolver (),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (GpsProvider.equals ("")) {
            //GPS Disabled
//            gpsState.setText("GPS Disable");
            Intent intent = new Intent (Settings.ACTION_SECURITY_SETTINGS);
            startActivity (intent);
        } else {
            //GPS Enabled
//            gpsState.setText("GPS Enable");
        }
    }

}
