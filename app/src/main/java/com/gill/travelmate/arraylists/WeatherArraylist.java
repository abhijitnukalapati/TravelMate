package com.gill.travelmate.arraylists;

//Pojo class for weather data
public class WeatherArraylist {

    String date,min_temp,max_temp,w_des,w_icon,humidity,w_full_des;

    public WeatherArraylist(String date,String min_temp,String max_temp,String w_des,String w_icon,String humidity,String w_full_des){
        this.date=date;
        this.min_temp=min_temp;
        this.max_temp=max_temp;
        this.w_des=w_des;
        this.w_icon=w_icon;
        this.humidity=humidity;
        this.w_full_des=w_full_des;
    }

    public String getW_full_des() {
        return w_full_des;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getDate() {
        return date;
    }

    public String getMin_temp() {
        return min_temp;
    }

    public String getMax_temp() {
        return max_temp;
    }

    public String getW_des() {
        return w_des;
    }

    public String getW_icon() {
        return w_icon;
    }
}
