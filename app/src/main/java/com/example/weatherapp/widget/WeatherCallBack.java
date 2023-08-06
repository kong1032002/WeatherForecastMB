package com.example.weatherapp.widget;

import com.example.weatherapp.model.Weather;

public class WeatherCallBack implements IWeatherCallBack {
    @Override
    public void onSuccess(Weather weather) {
        // xử lý khi lấy dữ liệu thành công
    }

    @Override
    public void onError(String errorMessage) {
        // xử lý khi xảy ra lỗi
    }
}
