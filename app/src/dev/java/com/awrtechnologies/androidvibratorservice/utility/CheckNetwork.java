package com.awrtechnologies.androidvibratorservice.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by dalveersinghdaiya on 10/24/14.
 */
public class CheckNetwork {

    private static int NOCONNECTION=0;
    private static int WIFI=1;
    private static int MOBILE=2;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null) {
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE;
            }
        }
        return NOCONNECTION;
    }

    public static String getConnectivityStatusString(Context context) {
        int connectionType = CheckNetwork.getConnectivityStatus(context);
        String connectionStatus = null;
        if(connectionType == CheckNetwork.WIFI) {
            connectionStatus = "WIFI";
        } else if (connectionType == CheckNetwork.MOBILE) {
            connectionStatus = "MOBILE";
        } else {
            connectionStatus = "No Connection";
        }
        return connectionStatus;
    }

}
