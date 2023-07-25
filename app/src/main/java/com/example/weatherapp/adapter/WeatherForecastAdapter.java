package com.example.weatherapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.model.WeatherForecast;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherForecast> weatherForecastArrayList;

    public WeatherForecastAdapter(Context context, ArrayList<WeatherForecast> weatherForecastArrayList) {
        this.context = context;
        this.weatherForecastArrayList = weatherForecastArrayList;
    }

    @NonNull
    @Override
    public WeatherForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weatherforecast_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherForecastAdapter.ViewHolder holder, int position) {
        WeatherForecast model = weatherForecastArrayList.get(position);
        if(model == null) {
            return;
        }
        holder.wfTemperature.setText(model.getTemperature() + "°C");
        Picasso.get().load("http://openweathermap.org/img/wn/"+model.getIcon()+"@2x.png").into(holder.wfIcon);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat output1 = new SimpleDateFormat("EEEE");
        SimpleDateFormat output2 = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(model.getTime());
            holder.wfTime.setText(output1.format(t) +"\n"+ output2.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherForecastArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView wfTime, wfTemperature;
        private ImageView wfIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wfTime = itemView.findViewById(R.id.wfTime);
            wfTemperature = itemView.findViewById(R.id.wfTemperature);
            wfIcon = itemView.findViewById(R.id.wfIcon);
        }
    }

    public void release() {
        this.context = null;
    }
}
