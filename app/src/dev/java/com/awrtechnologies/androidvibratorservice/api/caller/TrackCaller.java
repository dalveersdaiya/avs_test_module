package com.awrtechnologies.androidvibratorservice.api.caller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.awrtechnologies.androidvibratorservice.api.callback.ApiCallBack;
import com.awrtechnologies.androidvibratorservice.api.enums.ApiType;
import com.awrtechnologies.androidvibratorservice.api.params.ApiParams;
import com.awrtechnologies.androidvibratorservice.api.params.AuthParam;
import com.awrtechnologies.androidvibratorservice.api.params.TrackParam;
import com.awrtechnologies.androidvibratorservice.api.service.AuthService;
import com.awrtechnologies.androidvibratorservice.api.service.TrackService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kanikakachhawaha on 26/12/16.
 */

public class TrackCaller extends BaseCaller {
    @Override
    public void get(final Context context, final ApiParams apiParams, final ApiCallBack callback, final ApiType apiType) {


    }

    @Override
    public void post(final Context context, ApiParams apiParams, final ApiCallBack callback, final ApiType apiType) {
        final Gson gson = new GsonBuilder ().excludeFieldsWithoutExposeAnnotation ().create ();
        final TrackParam trackParam = (TrackParam) apiParams;

//        showLoading (context, true);

        TrackService trackService = retrofit.create (TrackService.class);
        Call<JsonObject> call = trackService.track (trackParam.latitude, trackParam.longitude, trackParam.dual_sim, trackParam.imei_primary, trackParam.imei_secondary,
                trackParam.is_primary_sim_ready, trackParam.is_secondary_sim_ready, trackParam.network_name, trackParam.primary_mobile_num, trackParam.secondary_network_num,
                trackParam.secondary_mobile_num, trackParam.battery_percent, trackParam.date_time, trackParam.location_accuracy, trackParam.location_bearing, trackParam.location_altitude,
                trackParam.access_token);
        call.enqueue (new Callback<JsonObject> () {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d ("Daiya", "Response on track " + response.code () + " " + response);
//                hideLoading ();
                if (response.code () == 200) {
                    Log.d ("Daiya", "Track response body : " + response.body ());
                    callback.onResult (response.body ().toString (), apiType, response.code ());
                } else {
                    callback.onResult ("INTERNAL ERROR", apiType, V_INTERNAL_ERROR);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                hideLoading ();
//                Toast.makeText (context, "Try Again!!", Toast.LENGTH_LONG).show ();

            }
        });
    }

    @Override
    public void put(Context context, ApiParams apiParams, ApiCallBack callback, ApiType apiType) {

    }

    @Override
    public void delete(Context context, ApiParams apiParams, ApiCallBack callback, ApiType apiType) {

    }

    public static TrackCaller instance() {
        return new TrackCaller ();
    }
}


