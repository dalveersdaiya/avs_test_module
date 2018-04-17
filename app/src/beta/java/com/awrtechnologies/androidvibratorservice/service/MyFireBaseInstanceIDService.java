package com.awrtechnologies.androidvibratorservice.service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by dalveersinghdaiya on 31/05/17.
 */

public class MyFireBaseInstanceIDService extends FirebaseInstanceIdService {

    String refreshedToken = "";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        refreshedToken = FirebaseInstanceId.getInstance ().getToken ();
        Log.d ("Daiya", "Refreshed token: " + refreshedToken);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (this);
        SharedPreferences.Editor editor = preferences.edit ();
        editor.putString ("RefreshedToken", refreshedToken);
        editor.apply ();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }

    public String getToken() {
        refreshedToken = FirebaseInstanceId.getInstance ().getToken ();
        return refreshedToken;
    }
}
