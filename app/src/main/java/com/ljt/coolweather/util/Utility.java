package com.ljt.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.ljt.coolweather.db.City;
import com.ljt.coolweather.db.County;
import com.ljt.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    public static boolean handleProvinceResponse(String reponse)
    {
        if (!TextUtils.isEmpty(reponse))
        {
            try {
                JSONArray jsonArray = new JSONArray(reponse);
                Log.d("ljt59731 length",jsonArray.length()+"");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject provinceObject=jsonArray.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                    Log.d("ljt59731 province",province.getProvinceName());
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCityResponse(String reponse,int provinceId)
    {
        if (!TextUtils.isEmpty(reponse))
        {
            try {
                JSONArray jsonArray = new JSONArray(reponse);
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject cityObject=jsonArray.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountyResponse(String reponse,int cityId)
    {
        if (!TextUtils.isEmpty(reponse))
        {
            try {
                JSONArray jsonArray = new JSONArray(reponse);
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject provinceObject=jsonArray.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(provinceObject.getString("name"));
                    county.setWeatherId(provinceObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
