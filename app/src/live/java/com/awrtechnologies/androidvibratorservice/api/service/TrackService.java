package com.awrtechnologies.androidvibratorservice.api.service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by rakhipurohit on 26/10/16.
 */

//public interface AuthService {
//
////    @Headers("Content-Type: application/json")
////    @POST("api/AssessmentQuestion/AddOrUpdate")
////    Call<JSONObject> submitAnswer(@Header("Authorization") String token,@Body JSONObject body);
//
//
//    @Headers("Content-Type: application/json")
//    @POST("api/AssessmentQuestion/AddOrUpdate")
//    Call<ResponseBody> submitAnswer(@Header("Authorization") String token, @Body RequestBody body);
//
//
//}

public interface TrackService {
    @FormUrlEncoded
    @POST("track")
    Call<JsonObject> track(@Field("track[latitude]") String latitude,
                          @Field("track[longitude]") String longitude,
                          @Field("track[dual_sim]") String dual_sim,
                          @Field("track[imei_primary]") String imei_primary,
                          @Field("track[imei_secondaries]") String imei_secondary,
                          @Field("track[sim_one_ready]") String is_primary_sim_ready,
                          @Field("track[sim_others_ready]") String is_secondary_sim_ready,
                          @Field("track[network_name]") String network_name,
                          @Field("track[mobile_number]") String primary_mobile_num,
                          @Field("track[secondary_network_names]") String secondary_network_num,
                          @Field("track[secondary_mobile_numbers]") String secondary_mobile_num,
                          @Field("track[battery_percent]") String battery_percent,
                          @Field("track[datetime]") String date_time,
                          @Field("track[location_accuracy]") String location_accuracy,
                          @Field("track[location_bearing]") String location_bearing,
                          @Field("track[location_altitude]") String location_altitude,
                          @Field("auth[access_token]") String access_token);
}
