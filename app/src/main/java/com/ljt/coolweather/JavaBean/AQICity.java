package com.ljt.coolweather.JavaBean;

import com.google.gson.annotations.SerializedName;

public class AQICity {
    @SerializedName("aqi")
    public String aqi;

    @SerializedName("pm25")
    public String pm25;

    @SerializedName("qlty")
    public String qualty;
}