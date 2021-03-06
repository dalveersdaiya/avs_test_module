package com.awrtechnologies.androidvibratorservice.utility;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.awrtechnologies.androidvibratorservice.R;
import com.awrtechnologies.androidvibratorservice.activity.MyLocation;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by DSD on 27-11-2015.
 */
public class Utils {

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void enableLocation(final Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!gps_enabled) {
            Intent gpsOptionsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, gpsOptionsIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_warning_white_24dp)
                    .setTicker("Enable Location.")
                    .setContentTitle("Enable Location.")
                    .setContentText("Location not enabled. Please Enable Location settings with high Accuracy.")
                    .addAction(R.drawable.ic_warning_white_24dp, "Open", pIntent)
                    .setContentIntent(pIntent)
                    .setAutoCancel(false);
            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationmanager.notify(0, builder.build());
        }
    }

    public static void showNoInternetDialog(final Context context) {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, gpsOptionsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_warning_white_24dp)
                .setTicker("Enable Internet.")
                .setContentTitle("Enable Internet.")
                .setContentText("No active Internet connection found. Please enable your Wifi or Mobile Data.")
                .addAction(R.drawable.ic_warning_white_24dp, "Open", pIntent)
                .setContentIntent(pIntent)
                .setAutoCancel(false);
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(0, builder.build());
    }

    public static void setHideApplication(Context c, boolean hide) {
        ComponentName cn = new ComponentName(c.getApplicationContext(),
                MyLocation.class);
        int setting = hide ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                : PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        int current = c.getPackageManager().getComponentEnabledSetting(cn);
        if (current != setting) {
            c.getPackageManager().setComponentEnabledSetting(cn, setting,
                    PackageManager.DONT_KILL_APP);
        }
    }

}
