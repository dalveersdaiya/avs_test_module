package com.awrtechnologies.androidvibratorservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.awrtechnologies.androidvibratorservice.utility.NetworkUtil;

/**
 * Created by dalveersinghdaiya on 19/10/16.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent intent) {

        boolean status = NetworkUtil.getConnectivityStatusString(context);

        if (status) {
            try {

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }


}
