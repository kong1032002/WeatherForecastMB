package com.example.weatherapp.model;

public class WeatherForecast {
    private String time;
    private String temperature;

    private String description;

    private String main;
    private String icon;

    private String city;
    private String airQuality;

    private boolean night;
    public WeatherForecast(String time, String temperature, String icon) {
        this.icon = icon;
        this.time = time;
        this.temperature = temperature;
    }

    public WeatherForecast(String city, String temperature, String description, String icon, String airQuality, String main, boolean night) {
        this.city = city;
        this.description = description;
        this.temperature = temperature;
        this.icon = icon;
        this.airQuality = airQuality;
        this.main = main;
        this.night = night;
    }

    public String getCity() {
        return city;
    }

    public String getTime() {
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }


    public String getIcon() {
        return icon;
    }

    public String getAirQuality() {
        return airQuality;
    }

    public String getMain() {
        return main;
    }

    public boolean isNight() {
        return night;
    }
}
