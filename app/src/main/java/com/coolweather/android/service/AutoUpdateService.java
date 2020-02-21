package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.coolweather.android.WeatherActivity;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand (Intent intent,int flags,int started){
        updateWeather();
        updateBingPic();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,started);
    }
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if (weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            if (weather!=null){
                String weatherName=weather.basic.cityName;
                String weatherUrl1 ="https://free-api.heweather.net/s6/weather/now?location=" +weatherName+"&key=e8c5e9ab62e745c690db5c4cb2f0d969";
                String weatherUrl2 ="https://free-api.heweather.net/s6/weather/forecast?location=" +weatherName+"&key=e8c5e9ab62e745c690db5c4cb2f0d969";
                String weatherUrl3 ="https://free-api.heweather.net/s6/weather/lifestyle?location=" +weatherName+"&key=e8c5e9ab62e745c690db5c4cb2f0d969";
                String weatherUrl4 ="https://free-api.heweather.net/s6/weather/hourly?location=" +weatherName+"&key=e8c5e9ab62e745c690db5c4cb2f0d969";
                HttpUtil.sendOkHttpRequest(weatherUrl1, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText1=response.body().string();
                        Weather weather1=Utility.handleWeatherResponse(responseText1);
                        if (weather1!=null&&"ok".equals(weather1.status)){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString("weather",responseText1);
                            editor.apply();
                        }
                    }
                });
                HttpUtil.sendOkHttpRequest(weatherUrl2, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText2=response.body().string();
                        Weather weather2=Utility.handleWeatherResponse(responseText2);
                        if (weather2!=null&&"ok".equals(weather2.status)){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString("weather",responseText2);
                            editor.apply();
                        }
                    }
                });
                HttpUtil.sendOkHttpRequest(weatherUrl3, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText3=response.body().string();
                        Weather weather3=Utility.handleWeatherResponse(responseText3);
                        if (weather3!=null&&"ok".equals(weather3.status)){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString("weather",responseText3);
                            editor.apply();
                        }
                    }
                });
                HttpUtil.sendOkHttpRequest(weatherUrl4, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText4=response.body().string();
                        Weather weather4=Utility.handleWeatherResponse(responseText4);
                        if (weather4!=null&&"ok".equals(weather4.status)){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString("weather",responseText4);
                            editor.apply();
                        }
                    }
                });
            }

        }
    }
    private void updateBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.
                        getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }

}
