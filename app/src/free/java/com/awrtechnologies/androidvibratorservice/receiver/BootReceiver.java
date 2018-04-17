package com.awrtechnologies.androidvibratorservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.awrtechnologies.androidvibratorservice.service.BackgroundLocationService;

/**
 * Created by dalveersinghdaiya on 02/05/17.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BackgroundLocationService.class);
        context.startService(serviceIntent);
    }
}