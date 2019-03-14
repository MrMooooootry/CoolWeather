package com.ljt.coolweather.JavaBean;

import com.google.gson.annotations.SerializedName;

public class Suggesstion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;


    public Sport sport;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }
    public class CarWash {
        @SerializedName("txt")
        public String info;
    }
    public class Sport {
        @SerializedName("txt")
        public String info;

    }


}
