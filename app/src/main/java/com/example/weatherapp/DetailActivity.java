package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.Toast;

import com.example.weatherapp.adapter.DetailCityViewPagerAdapter;
import com.example.weatherapp.database.CityDatabase;
import com.example.weatherapp.model.City;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    public List<City> listCity;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        listCity = CityDatabase.getInstance(this).cityDAO().getListCity();
        position = bundle.getInt("position");

        viewPager = findViewById(R.id.view_pager_detail_city);
        DetailCityViewPagerAdapter viewPagerAdapter = new DetailCityViewPagerAdapter(this, listCity);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}