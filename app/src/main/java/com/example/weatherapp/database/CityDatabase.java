package com.example.weatherapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.weatherapp.model.City;

@Database(entities = {City.class}, version = 1)
public abstract class CityDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "cityDB";
    private static CityDatabase instance;

    public static synchronized CityDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), CityDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract CityDAO cityDAO();
}
