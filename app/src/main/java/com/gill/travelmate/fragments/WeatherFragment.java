package com.gill.travelmate.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gill.travelmate.HomeActivity;
import com.gill.travelmate.R;
import com.gill.travelmate.adapter.WeatherAdapter;
import com.gill.travelmate.arraylists.WeatherArraylist;
import com.gill.travelmate.utils.FontHelper;
import com.gill.travelmate.utils.GeneralValues;
import com.gill.travelmate.utils.TinyDB;
import com.gill.travelmate.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

//Weather main fragment
public class WeatherFragment extends Fragment {

    private static Context mContext;
    private TinyDB tinyDB;
    private ProgressBar progress_bar;
    private ImageView reload, backgroundImageView, weatherIcon;
    private TextView tv_message, dateTextView, descriptionTextView, temperatureTextView, humidityTextView;
    private CardView todayWeatherCardView;
    private RecyclerView recyclerView;

    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v=inflater.inflate(R.layout.fragment_weather, container, false);

        mContext=getActivity();
        tinyDB=new TinyDB(mContext);

        FontHelper.applyFont(mContext, v.findViewById(R.id.container_weather), "bauhaus.ttf");

        progress_bar=(ProgressBar)v.findViewById(R.id.progress_bar);
        reload=(ImageView)v.findViewById(R.id.reload);
        tv_message=(TextView)v.findViewById(R.id.tv_message);
        backgroundImageView =(ImageView)v.findViewById(R.id.background_image);
        dateTextView =(TextView)v.findViewById(R.id.date_view);
        descriptionTextView =(TextView)v.findViewById(R.id.weather_view);
        temperatureTextView =(TextView)v.findViewById(R.id.temperature_view);
        humidityTextView =(TextView)v.findViewById(R.id.humidity_view);
        todayWeatherCardView =(CardView)v.findViewById(R.id.cd_today_data);
        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);
        weatherIcon =(ImageView)v.findViewById(R.id.weather_icon);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //Call bydefault weather api if internet available
        if(Utils.isNetworkConnected(mContext)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getWeatherDetailsFromAPI();
                }
            }, 300);
        }else{
            if(Utils.getWeatherArr(tinyDB)!= null && Utils.getWeatherArr(tinyDB).size() > 0){
                setValues(1,"C");
            }else{
                progress_bar.setVisibility(View.GONE);
                reload.setVisibility(View.VISIBLE);
                tv_message.setVisibility(View.VISIBLE);
            }
            tv_message.setText(getString(R.string.no_internet_connection_try_again));
        }

        //Reload data if error while fetching data from server
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isNetworkConnected(mContext)){
                    getWeatherDetailsFromAPI();
                }else{
                    progress_bar.setVisibility(View.GONE);
                    reload.setVisibility(View.VISIBLE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(getString(R.string.no_internet_connection_try_again));
                }
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Infulate menu in weather fragment
        super.onCreateOptionsMenu(menu, inflater);
        HomeActivity.toolbar.getMenu().findItem(R.id.action_conversion).setVisible(true);
    }

    //on click on menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_conversion:
                if(Utils.getWeatherArr(tinyDB).size()>0){
                    if(item.getTitle().equals(getString(R.string.fahrenheit))){
                        item.setTitle(getString(R.string.celsius));
                        setValues(0,"F");
                    }else{
                        item.setTitle(getString(R.string.fahrenheit));
                        setValues(0,"C");
                    }
                }else{
                    Utils.showToast(mContext,getString(R.string.no_data_found));
                }
                return false;
            default:
                break;
        }

        return false;
    }

    //Api to get data from server
    public void getWeatherDetailsFromAPI(){
        reload.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        tv_message.setVisibility(View.VISIBLE);
        tv_message.setText(getString(R.string.loading));

        String weather_api = GeneralValues.WEATHER_BASE_URL +
                "daily?mode=json&lat=" + String.valueOf(tinyDB.getDouble(GeneralValues.USER_LAT_KEY,0)) +
                "&lon=" + String.valueOf(tinyDB.getDouble(GeneralValues.USER_LONG_KEY,0)) +
                "&APPID=" + GeneralValues.WEATHERMAP_KEY +
                "&cnt=14";

        Call<ResponseBody> call = Utils.requestApi_Weather().requestJson_withValues_WeatherGet(weather_api);

        Utils.show_log("url = "+call.request().url());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonResponse = response.body().string();

                    ArrayList<WeatherArraylist> arr=new ArrayList<WeatherArraylist>();

                    JSONObject result = new JSONObject(jsonResponse);
                    JSONArray listArray = new JSONArray(result.getString("list"));
                    Utils.show_log("arr = "+listArray+" len = "+listArray.length());

                    for(int i=0;i<listArray.length();i++){
                        String weatherDescription="", weatherIcon="", weatherFullDescription="";
                        JSONObject fullData = listArray.getJSONObject(i);
                        JSONObject tempjson = new JSONObject(fullData.getString("temp"));
                        JSONArray weatherArray = new JSONArray(fullData.getString("weather"));

                        if(weatherArray.length()>=0){
                            JSONObject weatherData = weatherArray.getJSONObject(0);
                            weatherDescription = weatherData.getString("main");
                            weatherIcon = weatherData.getString("icon");
                            weatherFullDescription = weatherData.getString("description");
                        } else {
                            weatherDescription="";
                            weatherIcon="";
                        }
                        arr.add(new WeatherArraylist(fullData.getString("dt"),
                                tempjson.getString("min"),
                                tempjson.getString("max"),
                                weatherDescription,
                                weatherIcon,
                                fullData.getString("humidity"),
                                weatherFullDescription));
                    }

                    //Save data locally
                    Utils.setWeatherArr(tinyDB,arr);

                    if(Utils.getWeatherArr(tinyDB)!=null&&Utils.getWeatherArr(tinyDB).size()>0){
                        setValues(0,"C");
                    }else{
                        reload.setVisibility(View.VISIBLE);
                        progress_bar.setVisibility(View.GONE);
                        tv_message.setVisibility(View.VISIBLE);
                        tv_message.setText(getString(R.string.no_data_found));

                        todayWeatherCardView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    reload.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(getString(R.string.error_while_fetching));
                    Log.e("exception", "" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                reload.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.GONE);
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(getString(R.string.error_while_fetching));
                Utils.show_log("Exp = "+t.getMessage());
            }
        });
    }

    //Function used to set values in recycler view that we get from server
    public void setValues(int position,String check){

        reload.setVisibility(View.GONE);
        progress_bar.setVisibility(View.GONE);
        tv_message.setVisibility(View.GONE);

        todayWeatherCardView.setVisibility(View.VISIBLE);

        ArrayList<WeatherArraylist> arr;
        arr = Utils.getWeatherArr(tinyDB);

        dateTextView.setText("" + Utils.getDayFromTimeStamp(arr.get(position).getDate()) + "," +
                Utils.getDateFromTimeStamp(arr.get(position).getDate()));

        String img="http://openweathermap.org/img/w/"+arr.get(position).getWeatherIcon()+".png";
        Glide.with(mContext).load(img).placeholder(R.drawable.logo).into(weatherIcon);

        String des = "";
        if(arr.get(position).getWeatherFullDescription()!= null &&
                !arr.get(position).getWeatherFullDescription().equalsIgnoreCase("")){
            des = arr.get(position).getWeatherFullDescription().substring(0,1).toUpperCase() +
                    arr.get(position).getWeatherFullDescription().substring(1);
        }
        descriptionTextView.setText(des);

        humidityTextView.setText(arr.get(position).getHumidity()+"%");

        if(check.equalsIgnoreCase("C")){
            temperatureTextView.setText(Utils.convert_K2C(arr.get(position).getMaximunTemperature()) + "/" +
                    Utils.convert_K2C(arr.get(position).getMinTemperature()) + "\u2103");
        }else{
            temperatureTextView.setText(Utils.convert_K2F(arr.get(position).getMaximunTemperature()) + "/" +
                    Utils.convert_K2F(arr.get(position).getMinTemperature()) + "\u2109");
        }

        arr.remove(position);

        WeatherAdapter adapter = new WeatherAdapter(mContext, arr, WeatherFragment.this, position, check);
        recyclerView.setAdapter(adapter);

        setBackground(position);
    }

    //Set backgrounds according to the selected weather
    public void setBackground(int position){

        ArrayList<WeatherArraylist> arr=new ArrayList<>();
        arr=Utils.getWeatherArr(tinyDB);
        String weather=arr.get(position).getWeatherDescription();

        if(weather.equalsIgnoreCase("Thunderstorm")){
            Glide.with(mContext).load(R.drawable.thunderstorm).into(backgroundImageView);
        }else if(weather.equalsIgnoreCase("Drizzle")){
            Glide.with(mContext).load(R.drawable.drizzle).into(backgroundImageView);
        }else if(weather.equalsIgnoreCase("Rain")){
            Glide.with(mContext).load(R.drawable.drizzle).into(backgroundImageView);
        }else if(weather.equalsIgnoreCase("Snow")){
            Glide.with(mContext).load(R.drawable.snow).into(backgroundImageView);
        }else if(weather.equalsIgnoreCase("Atmosphere")){
            Glide.with(mContext).load(R.drawable.atmosphere).into(backgroundImageView);
        }else if(weather.equalsIgnoreCase("Clear")){
            Glide.with(mContext).load(R.drawable.clear).into(backgroundImageView);
        }else if(weather.equalsIgnoreCase("Clouds")){
            Glide.with(mContext).load(R.drawable.clouds).into(backgroundImageView);
        }else if(weather.equalsIgnoreCase("Extreme")){
            Glide.with(mContext).load(R.drawable.extreme).into(backgroundImageView);
        }else{
            Glide.with(mContext).load(R.drawable.blur_weather).into(backgroundImageView);
        }
    }
}
