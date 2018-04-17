package com.awrtechnologies.androidvibratorservice.api.params;

import retrofit2.http.Field;

/**
 * Created by dalveersinghdaiya on 02/01/17.
 */

public class TrackParam extends ApiParams {
    public String latitude;
    public String longitude;
    public String dual_sim;
    public String imei_primary;
    public String imei_secondary;
    public String is_primary_sim_ready;
    public String is_secondary_sim_ready;
    public String network_name;
    public String primary_mobile_num;
    public String secondary_network_num;
    public String secondary_mobile_num;
    public String battery_percent;
    public String date_time;
    public String location_accuracy;
    public String location_bearing;
    public String location_altitude;
    public String access_token;

}
