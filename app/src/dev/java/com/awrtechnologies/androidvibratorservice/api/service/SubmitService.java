package com.awrtechnologies.androidvibratorservice.api.service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by rakhipurohit on 26/10/16.
 */

public interface SubmitService  {

//    @Headers("Content-Type: application/json")
//    @POST("api/AssessmentQuestion/AddOrUpdate")
//    Call<JSONObject> submitAnswer(@Header("Authorization") String token,@Body JSONObject body);


    @Headers("Content-Type: application/json")
    @POST("api/AssessmentQuestion/AddOrUpdate")
    Call<ResponseBody> submitAnswer(@Header("Authorization") String token, @Body RequestBody body);
}
