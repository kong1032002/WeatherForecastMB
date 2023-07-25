package com.example.weatherapp.widget;

import com.example.weatherapp.model.WeatherForecast;

public interface WeatherCallBack {
    void onSuccess(WeatherForecast weather);
    void onError(String errorMessage);
}
