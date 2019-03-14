package com.ljt.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ljt.coolweather.JavaBean.Forecast;
import com.ljt.coolweather.JavaBean.Weather;
import com.ljt.coolweather.util.HttpUtil;
import com.ljt.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {


    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreetext;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView apiText;
    private TextView pm25Text;
    private TextView comfrotText;
    private TextView carwashText;
    private TextView sportText;
    private ImageView picImg;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String weatherId;
    public DrawerLayout drawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        if (Build.VERSION.SDK_INT>=21)
        {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        initView();

    }

    private void initView() {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_button);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        picImg = (ImageView)findViewById(R.id.pic_img);
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreetext = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        apiText = (TextView)findViewById(R.id.api_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfrotText = (TextView)findViewById(R.id.comfort_text);
        carwashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);

        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        String image = preferences.getString("image", null);
        if (image!=null)
        {
            Glide.with(this).load(image).into(picImg);
        }
        else {
            loadPicImage();
        }
        String weatherString = preferences.getString("weather", null);
        if (weatherString!=null)
        {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.cid;
            showWeatherInfo(weather);
        }
        else {
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });


    }

    private void loadPicImage() {
        String requestBingPic=Contant.Image_ADDRESS;
            HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("image","连接失败");
                    e.printStackTrace();

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String picImageUrl = response.body().string();

                    Log.d("image",picImageUrl);
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("image",picImageUrl);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(picImageUrl).into(picImg);
                        }
                    });
                }
            });

    }

    public void requestWeather( final String weather_id) {
        String weatherUrl=Contant.FIRST_ADDRESS+weather_id+Contant.END_ADDRESS;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d("okhttp response",responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }
                        else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();

                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        loadPicImage();
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "°C";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreetext.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast :weather.forecastList
             ) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }
        if (weather.aqi!=null)
        {
            apiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

       // Log.d("空指针判断 confort",weather.suggesstion.comfort.info);
      //  Log.d("空指针判断 carWash",weather.suggesstion.carWash.info);
      //  Log.d("空指针判断 sport",weather.suggesstion.sport.info);
        //String confort="舒适度"+weather.suggesstion.comfort.info;
        //Log.d("final",confort);
       // String carWash="洗车指数"+weather.suggesstion.carWash.info;
        //String sport="运动建议"+weather.suggesstion.sport.info;
       // comfrotText.setText(confort);
       // carwashText.setText(carWash);
       // sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

    }
}
