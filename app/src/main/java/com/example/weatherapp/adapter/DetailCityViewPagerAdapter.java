package com.example.weatherapp.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.weatherapp.fragment.DetailCityFragment;
import com.example.weatherapp.model.City;

import java.util.List;

public class DetailCityViewPagerAdapter extends FragmentStateAdapter {

    private List<City> listCity;

    public DetailCityViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<City> list) {
        super(fragmentActivity);
        this.listCity = list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (listCity == null || listCity.isEmpty()){
            return null;
        }

        City city = listCity.get(position);
        DetailCityFragment detailCityFragment = new DetailCityFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_city", city);
        detailCityFragment.setArguments(bundle);

        return detailCityFragment;
    }

    @Override
    public int getItemCount() {
        return listCity.size();
    }
}
