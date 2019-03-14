package com.ljt.coolweather.JavaBean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Basic   {
    //省
    @SerializedName("city")
    public String cityName;
    //市ID
    @SerializedName("cid")
    public String cid;

    public Update update;
    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }






}
