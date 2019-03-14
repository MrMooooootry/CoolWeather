package com.ljt.coolweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.ljt.coolweather.JavaBean.Weather;
import com.ljt.coolweather.util.HttpUtil;
import com.ljt.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;
         long triggerAtTime=SystemClock.elapsedRealtime()+anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);


        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = defaultSharedPreferences.getString("weather", null);
        if (weatherString!=null)
        {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String cid = weather.basic.cid;
            String weatherUrl=Contant.FIRST_ADDRESS+cid+Contant.END_ADDRESS;
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather!=null&&"ok".equals(weather.status))
                    {
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();

                    }
                }
            });
        }

    }

    private void updateWeather() {
        HttpUtil.sendOkHttpRequest(Contant.Image_ADDRESS, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String binpic = response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("image",binpic);
                editor.apply();
            }
        });
    }
}
