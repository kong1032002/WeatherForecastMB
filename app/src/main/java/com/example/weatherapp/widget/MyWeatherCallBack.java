package com.example.weatherapp.widget;

import com.example.weatherapp.model.WeatherForecast;

public class MyWeatherCallBack implements WeatherCallBack{
    @Override
    public void onSuccess(WeatherForecast weather) {
        // xử lý khi lấy dữ liệu thành công
    }

    @Override
    public void onError(String errorMessage) {
        // xử lý khi xảy ra lỗi
    }
}
