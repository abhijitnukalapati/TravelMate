package com.gill.travelmate.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gill.travelmate.R;
import com.gill.travelmate.arraylists.WeatherArraylist;
import com.gill.travelmate.fragments.WeatherFragment;
import com.gill.travelmate.utils.TinyDB;
import com.gill.travelmate.utils.Utils;

import java.util.ArrayList;

//Adapter to add data into WeatherFragment
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ChatHolder> {

    private Context mContext;
    private ArrayList<WeatherArraylist> list;
    WeatherFragment fragment;
    int item_position;
    TinyDB tinyDB;
    String check="";
    int date_pos=0;

    public WeatherAdapter(Context mContext, ArrayList<WeatherArraylist> list, WeatherFragment weatherFragment, int item_position, String check) {
        this.mContext = mContext;
        this.list = list;
        fragment = weatherFragment;
        this.item_position = item_position;
        tinyDB=new TinyDB(mContext);
        this.check=check;
        if(item_position>5){
            date_pos=6;
        }else{
            date_pos=5;
        }
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_weather, parent, false);

        final ChatHolder holder = new ChatHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String date=list.get(holder.getAdapterPosition()).getDate();
                            ArrayList<WeatherArraylist> arr=new ArrayList<WeatherArraylist>();
                            arr=Utils.getWeatherArr(tinyDB);
                            for(int i=0;i<arr.size();i++){
                                if(date.equalsIgnoreCase(arr.get(i).getDate())){
                                    fragment.setValues(i,check);
                                    break;
                                }
                            }
                        }catch (Exception e){

                        }
                    }
                });
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {
        holder.ll_outer.setVisibility(View.VISIBLE);
        try{
            if(position>date_pos){
                String date = Utils.getDayFromTimeStamp(list.get(position).getDate())+"\n"+Utils.getDateFromTimeStamp(list.get(position).getDate());
                holder.tv_day.setText(date);
            }else{
                holder.tv_day.setText(Utils.getDayFromTimeStamp(list.get(position).getDate()));
            }
        }catch (Exception ex){

        }
        String des="";
        if(list.get(position).getWeatherFullDescription()!=null&&!list.get(position).getWeatherFullDescription().equalsIgnoreCase("")){
            des = list.get(position).getWeatherFullDescription().substring(0,1).toUpperCase() + list.get(position).getWeatherFullDescription().substring(1);
        }
        holder.tv_weather.setText(des);

        if(check.equalsIgnoreCase("C")){
            //String celsius = "℃";
            holder.tv_max_temp.setText(Utils.convert_K2C(list.get(position).getMaximunTemperature()));
            holder.tv_min_temp.setText(Utils.convert_K2C(list.get(position).getMinTemperature()));
        }else{
            holder.tv_max_temp.setText( Utils.convert_K2F(list.get(position).getMaximunTemperature()));
            holder.tv_min_temp.setText( Utils.convert_K2F(list.get(position).getMinTemperature()));
        }
        String img = "http://openweathermap.org/img/w/" + list.get(position).getWeatherIcon() + ".png";
        Glide.with(mContext).load(img).placeholder(R.drawable.logo).into(holder.img_weather);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        TextView tv_day, tv_weather, tv_max_temp, tv_min_temp;
        ImageView img_weather;
        LinearLayout ll_outer;

        public ChatHolder(View itemView) {
            super(itemView);
            tv_day = (TextView) itemView.findViewById(R.id.tv_day);
            tv_weather = (TextView) itemView.findViewById(R.id.weather_view);
            tv_max_temp = (TextView) itemView.findViewById(R.id.tv_max_temp);
            tv_min_temp = (TextView) itemView.findViewById(R.id.tv_min_temp);
            img_weather = (ImageView) itemView.findViewById(R.id.weather_icon);
            ll_outer = (LinearLayout) itemView.findViewById(R.id.ll_outer);

            Utils.typeface_font(mContext, tv_day);
            Utils.typeface_font(mContext, tv_weather);
            Utils.typeface_font(mContext, tv_max_temp);
            Utils.typeface_font(mContext, tv_min_temp);
        }
    }
}
