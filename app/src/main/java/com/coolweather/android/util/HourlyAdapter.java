package com.coolweather.android.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coolweather.android.R;
import com.coolweather.android.gson.Hourly;

import java.util.List;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {
    private List<Hourly>mHourlyList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView timeText;
        TextView condText;
        TextView tmpText;
        public ViewHolder(View view){
            super(view);
            timeText=(TextView)view.findViewById(R.id.time_text);
            condText=(TextView)view.findViewById(R.id.cond_text);
            tmpText=(TextView)view.findViewById(R.id.tmp_text);
        }
    }
    public HourlyAdapter(List<Hourly>hourlyList){
        mHourlyList=hourlyList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_item,parent,false);
        ViewHolder holder =new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Hourly hourly=mHourlyList.get(position);
        holder.timeText.setText(hourly.time);
        holder.condText.setText(hourly.cond_txt);
        holder.tmpText.setText(hourly.tmp);
    }
    @Override
    public int getItemCount(){
        return mHourlyList.size();
    }
}
