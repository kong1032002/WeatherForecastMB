package com.example.weatherapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import com.example.weatherapp.model.City;

@Dao
public interface CityDAO {

    @Insert
    void insertCity(City city);

    @Query("SELECT * FROM city")
    List<City> getListCity();

    @Query("SELECT * FROM city WHERE cityName= :cityName")
    List<City> checkCity(String cityName);

    @Delete
    void deleteCity(City city);
}
