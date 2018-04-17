package com.awrtechnologies.androidvibratorservice.api.service;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by dalveersinghdaiya on 19/10/16.
 */

public interface DeviceService {

//    @Headers("Content-Type: Application/Json, charset=UTF-8")
//    @POST("registerdevice")
//    Call<JsonObject> register(@Header("skit") String skit, @Body RequestBody body);

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("token")
    Call<JsonObject> login(@FieldMap Map<String, String> fields);
}
