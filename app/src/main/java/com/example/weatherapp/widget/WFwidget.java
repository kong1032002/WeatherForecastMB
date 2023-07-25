package com.example.weatherapp.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.weatherapp.DetailActivity;
import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.model.WeatherForecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of App Widget functionality.
 */
public class WFwidget extends AppWidgetProvider {
    public static final String WIDGET_IDS_KEY ="mywidgetproviderwidgetids";

    public static final String ACTION_WIDGET_CLICK = "com.example.weatherapp.widget.WIDGET_CLICK";

    public static boolean isCitySet = false;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String city) {
        getWeather(city, context, new MyWeatherCallBack() {
            @Override
            public void onSuccess(WeatherForecast weatherForecast) {
                // Construct the RemoteViews object
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.w_fwidget);

                if (weatherForecast.isNight()) {
                    views.setInt(R.id.widget_container, "setBackgroundResource", R.drawable.app_widget_background_night);
                } else if (weatherForecast.getMain().equals("Thunderstorm") ||
                        weatherForecast.getMain().equals("Drizzle") ||
                        weatherForecast.getMain().equals("Rain") ||
                        weatherForecast.getDescription().equals("Overcast clouds")){
                    views.setInt(R.id.widget_container, "setBackgroundResource", R.drawable.app_widget_background);
                } else if (weatherForecast.getMain().equals("Clear") ||
                        weatherForecast.getMain().equals("Clouds")) {
                    views.setInt(R.id.widget_container, "setBackgroundResource", R.drawable.app_widget_background_sunny);
                } else {
                    views.setInt(R.id.widget_container, "setBackgroundResource", R.drawable.app_widget_background_cold);
                }

                views.setTextViewText(R.id.widget_city, weatherForecast.getCity());
                views.setTextViewText(R.id.widget_temp, weatherForecast.getTemperature() + "°C");
                views.setTextViewText(R.id.widget_desc, weatherForecast.getDescription());
                views.setTextViewText(R.id.widget_airQua, "Air Quality: " + weatherForecast.getAirQuality());
                String iconUrl = "http://openweathermap.org/img/w/" + weatherForecast.getIcon() + ".png";
                //views.setImageViewUri(R.id.widget_icon, Uri.parse(iconUrl));
                AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.widget_icon, views, appWidgetId) {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);
                    }
                };

                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .load(iconUrl)
                        .into(appWidgetTarget);

                // Instruct the widget manager to update the widget
                //ComponentName componentName = new ComponentName(context, WFwidget.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (appWidgetIds == null || appWidgetIds.length == 0) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT ).show();
            return;
        }

        for (int appWidgetId : appWidgetIds) {
            try {
                RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.w_fwidget);

                Intent configIntent = new Intent(context, MainActivity.class);
                configIntent.setAction(WFwidget.ACTION_WIDGET_CLICK);
                configIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(configIntent);
                PendingIntent configPendingIntent = PendingIntent.getActivity(context, 69 , configIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(R.id.widget_container, configPendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, views);
                if (!isCitySet) {
                    Toast.makeText(context, "Please configure the widget", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String city = AppWidgetConfigurationActivity.loadTitlePref(context, appWidgetId);
                    updateAppWidget(context, appWidgetManager, appWidgetId, city);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) ||
                intent.getAction().equals(WIDGET_IDS_KEY) ||
                intent.getAction().equals(WFwidget.ACTION_WIDGET_CLICK)) {
            int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void getWeather(String city, Context context, MyWeatherCallBack callback) {
        final String API_KEY = "bffca17bcb552b8c8e4f3b82f64cccd2";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("weather");
                        JSONObject weatherObj = jsonArray.getJSONObject(0);
                        JSONObject mainObj = response.getJSONObject("main");
                        JSONObject coorObj = response.getJSONObject("coord");
                        JSONObject sysObj = response.getJSONObject("sys");
                        String description = weatherObj.getString("description");
                        String main = weatherObj.getString("main");
                        String temperature = mainObj.getString("temp");
                        boolean night = response.getLong("dt") > sysObj.getLong("sunset");
                        description = description.substring(0, 1).toUpperCase() + description.substring(1);
                        double temp = Double.parseDouble(temperature);
                        temp -= 273.15;
                        double lat_find = coorObj.getDouble("lat");
                        double long_find = coorObj.getDouble("lon");
                        String icon = weatherObj.getString("icon");
                        String aqiUrl = "https://api.openweathermap.org/data/2.5/air_pollution?lat=" + lat_find + "&lon=" + long_find + "&appid=" + API_KEY;
                        @SuppressLint("DefaultLocale") String finalTemperature = String.format("%.0f", temp);
                        String finalDescription = description;
                        JsonObjectRequest aqiJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, aqiUrl, null,
                                aqiResponse -> {
                                    try {
                                        String airQualityIndex = aqiResponse.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("aqi");
                                        airQualityIndex = getAqiCategory(Double.parseDouble(airQualityIndex));
                                        WeatherForecast wf = new WeatherForecast(city, finalTemperature, finalDescription, icon, airQualityIndex, main, night);
                                        callback.onSuccess(wf);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }, error -> Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                        RequestQueue aqiQueue = Volley.newRequestQueue(context);
                        aqiQueue.add(aqiJsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            String errorMessage = error != null ? error.getLocalizedMessage() : "Unknown error";
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Unknown", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }

    public static String getAqiCategory(Double aqi) {
        if (aqi >= 1 && aqi <= 3) {
            return "Good";
        } else if (aqi >= 4 && aqi <= 6) {
            return "Moderate";
        } else if (aqi >= 7 && aqi <= 9) {
            return "Poor";
        } else if (aqi == 10) {
            return "Very bad";
        } else {
            return "Bad";
        }
    }

}