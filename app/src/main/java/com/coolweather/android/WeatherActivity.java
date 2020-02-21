package com.coolweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Hourly;
import com.coolweather.android.gson.Suggestion;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HourlyAdapter;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private List<Hourly>hourlyList=new ArrayList<>();
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private RelativeLayout titleLayout;
    private LinearLayout nowLayout;
    private LinearLayout forecastLayout;
    private LinearLayout suggestionLayout;
    private ImageView bingPicImg;
    private  String mWeatherId;
    public DrawerLayout drawerLayout;
    private Button navButton;
    public SwipeRefreshLayout swipeRefresh;
    private  String mWeatherName;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        suggestionLayout=(LinearLayout)findViewById(R.id.suggestion_layout);
        titleLayout=(RelativeLayout)findViewById(R.id.title_layout);
        nowLayout=(LinearLayout)findViewById(R.id.now_layout);
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if (weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            if (weather==null){
                Log.d("WeatherActivity","weather is null");
            }else {
                mWeatherName = weather.basic.cityName;
                showWeatherInfo(weather);
            }
        }else {
            mWeatherName=getIntent().getStringExtra("weather_name");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherName);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherName);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }
    public void requestWeather(final String weatherName){
        String weatherUrl1 ="https://free-api.heweather.net/s6/weather/now?location=" +weatherName+"&key=e8c5e9ab62e745c690db5c4cb2f0d969";
        String weatherUrl2 ="https://free-api.heweather.net/s6/weather/forecast?location=" +weatherName+"&key=e8c5e9ab62e745c690db5c4cb2f0d969";
        String weatherUrl3 ="https://free-api.heweather.net/s6/weather/lifestyle?location=" +weatherName+"&key=e8c5e9ab62e745c690db5c4cb2f0d969";
        String weatherUrl4 ="https://free-api.heweather.net/s6/weather/hourly?location=" +weatherName+"&key=e8c5e9ab62e745c690db5c4cb2f0d969";
        HttpUtil.sendOkHttpRequest(weatherUrl1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,
                                "获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText1=response.body().string();
                final Weather weather1=Utility.handleWeatherResponse(responseText1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather1!=null&&"ok".equals(weather1.status)){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText1);
                            editor.apply();
                            mWeatherName = weather1.basic.cityName;
                            showWeatherInfo1(weather1);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        HttpUtil.sendOkHttpRequest(weatherUrl2, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,
                                "获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText2=response.body().string();
                final Weather weather2=Utility.handleWeatherResponse(responseText2);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather2!=null&&"ok".equals(weather2.status)){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText2);
                            editor.apply();
                            mWeatherName = weather2.basic.cityName;
                            showWeatherInfo2(weather2);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        HttpUtil.sendOkHttpRequest(weatherUrl3, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,
                                "获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText3=response.body().string();
                final Weather weather3=Utility.handleWeatherResponse(responseText3);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather3!=null&&"ok".equals(weather3.status)){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText3);
                            editor.apply();
                            mWeatherName = weather3.basic.cityName;
                            showWeatherInfo3(weather3);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }
        });
        HttpUtil.sendOkHttpRequest(weatherUrl4, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,
                                "获取天气信息失败1",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText4=response.body().string();
                final Weather weather4=Utility.handleWeatherResponse(responseText4);
                Log.d("Tipwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww",weather4.status);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather4!=null&&"ok".equals(weather4.status)){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText4);
                            editor.apply();
                            mWeatherName = weather4.basic.cityName;
                            showWeatherInfo4(weather4);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败2",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    private  void loadBingPic(){
        String requestBingPic ="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    private void showWeatherInfo1(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime=weather.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        titleLayout.setVisibility(View.VISIBLE);
        nowLayout.setVisibility(View.VISIBLE);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void showWeatherInfo2(Weather weather){
        forecastLayout.removeAllViews();
        for (Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.info);
            maxText.setText(forecast.max);
            minText.setText(forecast.min);
            forecastLayout.addView(view);
        }
        forecastLayout.setVisibility(View.VISIBLE);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void showWeatherInfo3(Weather weather){
        suggestionLayout.removeAllViews();
        for (Suggestion suggestion:weather.suggestionList){
            View view= LayoutInflater.from(this).inflate(R.layout.suggestion_item,
                    suggestionLayout,false);
            TextView typeText=(TextView)view.findViewById(R.id.type_text);
            TextView jutiText=(TextView)view.findViewById(R.id.juti_text);
            typeText.setText(suggestion.type);
            jutiText.setText(suggestion.txt);
            suggestionLayout.addView(view);
        }
        suggestionLayout.setVisibility(View.VISIBLE);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
    private void showWeatherInfo4(Weather weather){
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        HourlyAdapter adapter=new HourlyAdapter(weather.hourlyList);
        recyclerView.setAdapter(adapter);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime=weather.update.updateTime.split(" ")[1];
        try {
            String degree=weather.now.temperature+"℃";
            degreeText.setText(degree);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            String weatherInfo=weather.now.info;
            weatherInfoText.setText(weatherInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        forecastLayout.removeAllViews();
        try {
            for (Forecast forecast:weather.forecastList){
                View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,
                        forecastLayout,false);
                TextView dateText=(TextView)view.findViewById(R.id.date_text);
                TextView infoText=(TextView)view.findViewById(R.id.info_text);
                TextView maxText=(TextView)view.findViewById(R.id.max_text);
                TextView minText=(TextView)view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.info);
                maxText.setText(forecast.max);
                minText.setText(forecast.min);
                forecastLayout.addView(view);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        suggestionLayout.removeAllViews();
        try {
            for (Suggestion suggestion:weather.suggestionList){
                View view= LayoutInflater.from(this).inflate(R.layout.suggestion_item,
                        suggestionLayout,false);
                TextView typeText=(TextView)view.findViewById(R.id.type_text);
                TextView jutiText=(TextView)view.findViewById(R.id.juti_text);
                typeText.setText(suggestion.type);
                jutiText.setText(suggestion.txt);
                suggestionLayout.addView(view);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
