package com.awrtechnologies.androidvibratorservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.awrtechnologies.androidvibratorservice.activity.BarcodeScanner;
import com.awrtechnologies.androidvibratorservice.activity.MyLocation;
import com.awrtechnologies.androidvibratorservice.utility.Utils;

/**
 * Created by dalveersinghdaiya on 04/05/17.
 */

public class LaunchAppViaDialReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        Bundle bundle = intent.getExtras ();
        if (null == bundle)
            return;

        String phoneNubmer = intent.getStringExtra (Intent.EXTRA_PHONE_NUMBER);
        String defaultPin = "##1111";

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (context);
        String myPin = preferences.getString ("myPin", defaultPin);

        //here change the number to your desired number
        if (phoneNubmer.equals (myPin)) {
            Utils.setHideApplication (context, false);
            Toast.makeText (context, "Launching app.", Toast.LENGTH_SHORT).show ();
            setResultData (null);
            new Handler ().postDelayed (new Runnable () {

                @Override
                public void run() {
                    Intent appIntent = new Intent (context, MyLocation.class);
                    appIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity (appIntent);
                }
            }, 2000);

        }

    }
}