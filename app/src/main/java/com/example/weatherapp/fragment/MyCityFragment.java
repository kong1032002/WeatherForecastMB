package com.example.weatherapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.weatherapp.R;
import com.example.weatherapp.adapter.CityAdapter;
import com.example.weatherapp.database.CityDatabase;
import com.example.weatherapp.model.City;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCityFragment extends Fragment {

    RecyclerView rvCitiesList;
    ItemTouchHelper itemTouchHelper;
    public CityAdapter cityAdapter;
    public List<City> listCity;
    public MyCityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_city, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        rvCitiesList = view.findViewById(R.id.rvCitiesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvCitiesList.setLayoutManager(linearLayoutManager);

        listCity = CityDatabase.getInstance(getActivity()).cityDAO().getListCity();
        cityAdapter = new CityAdapter(getActivity(), listCity);
        rvCitiesList.setAdapter(cityAdapter);


        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                City city = listCity.get(viewHolder.getAbsoluteAdapterPosition());
                CityDatabase.getInstance(getActivity()).cityDAO().deleteCity(city);
                listCity = CityDatabase.getInstance(getActivity()).cityDAO().getListCity();
                cityAdapter.setData(listCity);
                Toast.makeText(getActivity(), "Delete city successfully", Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(rvCitiesList);

        updateListCity();
    }

    //update list city
    private void updateListCity() {
        getParentFragmentManager().setFragmentResultListener("upd_my_city", getActivity(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if(result.getBoolean("updMC")) {
                    listCity = CityDatabase.getInstance(getActivity()).cityDAO().getListCity();
                    cityAdapter.setData(listCity);
                }
            }
        });
    }
}