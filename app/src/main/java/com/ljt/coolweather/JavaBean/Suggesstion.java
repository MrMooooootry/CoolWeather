package com.ljt.coolweather.JavaBean;

import com.google.gson.annotations.SerializedName;

public class Suggesstion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("sport")
    public Sport sport;
}
