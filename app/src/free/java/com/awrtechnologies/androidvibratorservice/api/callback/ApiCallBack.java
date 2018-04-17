package com.awrtechnologies.androidvibratorservice.api.callback;


import com.awrtechnologies.androidvibratorservice.api.enums.ApiType;

/**
 * Created by dalveersinghdaiya on 02/01/17.
 */

public interface ApiCallBack {

    public void onResult(String result, ApiType apitype, int resultCode);
}
