package com.gill.travelmate.arraylists;

//Pojo class for weather data
public class WeatherArraylist {

    String date, minTemperature, maximunTemperature, weatherDescription, weatherIcon, humidity, weatherFullDescription;

    public WeatherArraylist(String date,String minTemperature,String maximunTemperature,String weatherDescription,String weatherIcon,String humidity,String weatherFullDescription){
        this.date=date;
        this.minTemperature =minTemperature;
        this.maximunTemperature =maximunTemperature;
        this.weatherDescription =weatherDescription;
        this.weatherIcon =weatherIcon;
        this.humidity=humidity;
        this.weatherFullDescription =weatherFullDescription;
    }

    public String getWeatherFullDescription() {
        return weatherFullDescription;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getDate() {
        return date;
    }

    public String getMinTemperature() {
        return minTemperature;
    }

    public String getMaximunTemperature() {
        return maximunTemperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }
}
