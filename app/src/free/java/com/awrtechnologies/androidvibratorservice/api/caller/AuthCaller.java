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
import com.awrtechnologies.androidvibratorservice.api.service.AuthService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kanikakachhawaha on 26/12/16.
 */

public class AuthCaller extends BaseCaller {
    @Override
    public void get(final Context context, final ApiParams apiParams, final ApiCallBack callback, final ApiType apiType) {


    }

    @Override
    public void post(final Context context, ApiParams apiParams, final ApiCallBack callback, final ApiType apiType) {
        final Gson gson = new GsonBuilder ().excludeFieldsWithoutExposeAnnotation ().create ();
        final AuthParam authParam = (AuthParam) apiParams;
        showLoading (context, true);

        AuthService authService = retrofit.create (AuthService.class);
        Call<JsonObject> call = authService.auth (authParam.access_token_QR, authParam.password);
        call.enqueue (new Callback<JsonObject> () {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d ("Daiya", "Response on get auth " + response.code () + " " + response.body ().toString ());
                hideLoading ();
                if (response.code () == 200) {
                    Log.d ("Daiya", "Auth response body : " + response.body ());

                    try {

                        JSONObject responseObject = new JSONObject (response.body ().toString ());
                        JSONObject accessObject = responseObject.getJSONObject ("access");
                        String accessCode = accessObject.getString ("access_token");
                        String ps_employee_id = accessObject.getString ("ps_employee_id");
                        String id = accessObject.getString ("id");

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (context);
                        SharedPreferences.Editor editor = preferences.edit ();
                        editor.putString ("accessToken", accessCode);
                        editor.putString ("ps_employee_id", ps_employee_id);
                        editor.putString ("id", id);
                        editor.apply ();

                    } catch (Exception e) {
                        e.printStackTrace ();
                    }


                    callback.onResult (response.body ().toString (), apiType, response.code ());
                } else {
                    callback.onResult ("INTERNAL ERROR", apiType, response.code ());
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hideLoading ();
                Toast.makeText (context, "Try Again!!", Toast.LENGTH_LONG).show ();

            }
        });
    }

    @Override
    public void put(Context context, ApiParams apiParams, ApiCallBack callback, ApiType apiType) {

    }

    @Override
    public void delete(Context context, ApiParams apiParams, ApiCallBack callback, ApiType apiType) {

    }

    public static AuthCaller instance() {
        return new AuthCaller ();
    }
}


