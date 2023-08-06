package com.example.weatherapp.widget;

import com.example.weatherapp.model.Weather;

public interface IWeatherCallBack {
    void onSuccess(Weather weather);
    void onError(String errorMessage);
}
