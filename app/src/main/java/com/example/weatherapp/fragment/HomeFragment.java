package com.example.weatherapp.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.R;
import com.example.weatherapp.adapter.WeatherForecastAdapter;
import com.example.weatherapp.model.WeatherForecast;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    final String APP_ID = "bffca17bcb552b8c8e4f3b82f64cccd2";
    final int REQUEST_CODE = 100;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageView imageView;
    ScrollView bgLayout;
    TextView temptv, time, longitude, latitude, humidity, sunrise, sunset, pressure, wind, country, city_nam, max_temp, min_temp, feels, visibility, co, so2, pm2_5, air_quality;
    RecyclerView rvWeatherForecast;
    private ArrayList<WeatherForecast> weatherForecastArrayList;
    private WeatherForecastAdapter weatherForecastAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        bgLayout = view.findViewById(R.id.bgLayout);
        imageView = view.findViewById(R.id.imageView);
        temptv = view.findViewById(R.id.textView3);

        humidity = view.findViewById(R.id.humidity);
        pressure = view.findViewById(R.id.pressure);
        wind = view.findViewById(R.id.wind);
        country = view.findViewById(R.id.country);
        city_nam = view.findViewById(R.id.city_nam);
        sunrise = view.findViewById(R.id.sunrise);
        sunset = view.findViewById(R.id.sunset);
        max_temp = view.findViewById(R.id.max_temp);
        min_temp = view.findViewById(R.id.min_temp);
        visibility = view.findViewById(R.id.visibility);
        feels = view.findViewById(R.id.feels);

        rvWeatherForecast = view.findViewById(R.id.rvDetailWeatherForecast);
        weatherForecastArrayList = new ArrayList<>();
        weatherForecastAdapter = new WeatherForecastAdapter(getActivity(), weatherForecastArrayList);
        rvWeatherForecast.setAdapter(weatherForecastAdapter);

        air_quality = view.findViewById(R.id.airQuality);
        so2 = view.findViewById(R.id.so2);
        co = view.findViewById(R.id.co);
        pm2_5 = view.findViewById(R.id.pm2_5);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();

        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.

                            return;
                        }
                        //fuse location
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult location) {
                                super.onLocationResult(location);
                                if (location != null) {
                                    getWeather(location.getLastLocation().getLatitude(), location.getLastLocation().getLongitude());
                                    getAirQuality(location.getLastLocation().getLatitude(), location.getLastLocation().getLongitude());
                                }
                            }
                        }, Looper.getMainLooper());
                    } else {
                        Toast.makeText(getActivity(), "Required Permission",Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    public void getWeather(double lat, double lon)
    {
        String url ="http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid="+APP_ID+"&units=metric";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //find temperature
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject object = jsonObject.getJSONObject("main");
                            int temp = object.getInt("temp");
                            temptv.setText(temp+"°");

                            //find country
                            JSONObject object8 = jsonObject.getJSONObject("sys");
                            String count = object8.getString("country");
                            country.setText(count);

                            //find city
                            String city = jsonObject.getString("name");
                            city_nam.setText(city+",  ");

                            //find icon
                            JSONArray jsonArray = jsonObject.getJSONArray("weather");
                            JSONObject obj = jsonArray.getJSONObject(0);
                            String icon = obj.getString("icon");
//                            Picasso.get().load("http://openweathermap.org/img/wn/"+icon+"@2x.png").into(imageView);

                            //find weather
                            int id = obj.getInt("id");
                            updateBackGround(id, icon);

                            //find humidity
                            JSONObject object4 = jsonObject.getJSONObject("main");
                            int humidity_find = object4.getInt("humidity");
                            humidity.setText(humidity_find+"%");

                            //find sunrise
                            JSONObject object5 = jsonObject.getJSONObject("sys");
                            Long sunrise_find = object5.getLong("sunrise");
                            Date sunrise_date = new Date(sunrise_find*1000L);
                            SimpleDateFormat stdd = new SimpleDateFormat("HH:mm");
                            String sunrise_result = stdd.format(sunrise_date);
                            sunrise.setText(sunrise_result);

                            //find sunset
                            JSONObject object6 = jsonObject.getJSONObject("sys");
                            Long sunset_find = object6.getLong("sunset");
                            Date sunset_date = new Date(sunset_find*1000L);
                            String sunset_result = stdd.format(sunset_date);
                            sunset.setText(sunset_result);

                            //find pressure
                            JSONObject object7 = jsonObject.getJSONObject("main");
                            String pressure_find = object7.getString("pressure");
                            pressure.setText(pressure_find);

                            //find wind speed
                            JSONObject object9 = jsonObject.getJSONObject("wind");
                            String wind_find = object9.getString("speed");
                            wind.setText(wind_find);

                            //find visibility
                            int visibility_find = jsonObject.getInt("visibility") / 1000;
                            visibility.setText(Integer.toString(visibility_find) + "km");

                            //find min temperature
                            JSONObject object10 = jsonObject.getJSONObject("main");
                            int mintemp = object10.getInt("temp_min") ;
                            min_temp.setText("L: "+mintemp+"°");

                            //find max temperature
                            JSONObject object12 = jsonObject.getJSONObject("main");
                            int maxtemp = object12.getInt("temp_max");
                            max_temp.setText("H: "+maxtemp+"°");

                            //find feels
                            JSONObject object13 = jsonObject.getJSONObject("main");
                            int feels_find = object13.getInt("feels_like");
                            feels.setText("Feel like: "+feels_find+"°C");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        getWeatherForecast(lat, lon);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    //get weather forecast
    private void getWeatherForecast(double lat, double lon) {
        String url ="http://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+lon+"&appid="+APP_ID+"&units=metric";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        weatherForecastArrayList.clear();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray forecastArray = jsonObject.getJSONArray("list");
                            for(int i = 0; i <forecastArray.length(); i++) {
                                JSONObject forecastObj = forecastArray.getJSONObject(i);
                                String time = forecastObj.getString("dt_txt");
                                String temp =Integer.toString(forecastObj.getJSONObject("main").getInt("temp"));
                                String icon = forecastObj.getJSONArray("weather").getJSONObject(0).getString("icon");
                                weatherForecastArrayList.add(new WeatherForecast(time, temp, icon));

                            }
                            weatherForecastAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    //get air quality
    public void getAirQuality(double lat, double lon)
    {
        String url ="http://api.openweathermap.org/data/2.5/air_pollution?lat="+lat+"&lon="+lon+"&appid="+APP_ID;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            JSONObject obj = jsonArray.getJSONObject(0);
                            JSONObject mainObj = obj.getJSONObject("main");
                            int air_pollution = mainObj.getInt("aqi");
                            air_quality.setText(getDesAirQuality((air_pollution)));

                            JSONObject componentsObj = obj.getJSONObject("components");
                            so2.setText(componentsObj.getString("so2"));
                            co.setText(componentsObj.getString("co"));
                            pm2_5.setText(componentsObj.getString("pm2_5"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    //change background
    private void updateBackGround(int id, String icon) {
        if(200<=id && id <= 232) {
            imageView.setImageResource(R.drawable.icon_thunderstorm);
            bgLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_thunderstorm));
        } else if (300<= id && id <= 321) {
            imageView.setImageResource(R.drawable.icon_drizzle);
            bgLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_drizzle));
        } else if (500<= id && id <= 521) {
            imageView.setImageResource(R.drawable.icon_rain);
            bgLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_rain));
        } else if (600<= id && id <= 622) {
            imageView.setImageResource(R.drawable.icon_snow);
            bgLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_snow));
        } else if (701<= id && id <= 781) {
            imageView.setImageResource(R.drawable.icon_fog);
            bgLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_fog));
        } else if (id == 800) {
            if (icon.contains("d")) {
                imageView.setImageResource(R.drawable.icon_clear);
                bgLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_clear));
            } else {
                imageView.setImageResource(R.drawable.icon_moon);
                bgLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_night_clear));
            }
        } else if (801<= id && id <= 804) {
            if (icon.contains("d")) {
                imageView.setImageResource(R.drawable.icon_day_cloudy);
                bgLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_cloudy));
            }
            else {
                imageView.setImageResource(R.drawable.icon_night_cloudy);
                bgLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_night_cloudy));
            }
        }
    }

    //get describe air quality
    private String getDesAirQuality(int air_pollution) {
        switch (air_pollution) {
            case 1:
                return "Good, pollution is low and poses little or no risk.";

            case 2:
                return "Fair, air quality is acceptable. However, there may be a risk for some people, particularly those who are unusually sensitive to air pollution.";

            case 3:
                return "Moderate, members of sensitive groups may experience health effects. The general public is less likely to be affected.";

            case 4:
                return "Poor, sensitive groups may experience serious health issues, and the general public will also be affected.";

            case 5:
                return "Very Poor, everyone is at risk of experiencing health effects.";
        }
        return null;
    }

    //destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (weatherForecastAdapter != null ) {
            weatherForecastAdapter.release();
        }

    }
}