package com.gill.travelmate.fragments;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.gill.travelmate.data.TravelContract;
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
public class WeatherFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    Context mContext;
    TinyDB tinyDB;
    ProgressBar progress_bar;
    ImageView reload,img_bg,img_weather;
    TextView tv_message,tv_date,tv_weather,tv_temp,tv_humidity;
    CardView cd_today_data;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    WeatherAdapter adapter;

    private static final int FORECAST_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            TravelContract.WeatherEntry.TABLE_NAME + "." + TravelContract.WeatherEntry._ID,
            TravelContract.WeatherEntry.COLUMN_DATE,
            TravelContract.WeatherEntry.COLUMN_SHORT_DESC,
            TravelContract.WeatherEntry.COLUMN_MAX_TEMP,
            TravelContract.WeatherEntry.COLUMN_MIN_TEMP,
            TravelContract.LocationEntry.COLUMN_LOCATION_SETTING,
            TravelContract.WeatherEntry.COLUMN_WEATHER_ID,
            TravelContract.LocationEntry.COLUMN_COORD_LAT,
            TravelContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_CONDITION_ID = 6;
    public static final int COL_COORD_LAT = 7;
    public static final int COL_COORD_LONG = 8;

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
        img_bg=(ImageView)v.findViewById(R.id.img_bg);
        tv_date=(TextView)v.findViewById(R.id.tv_date);
        tv_weather=(TextView)v.findViewById(R.id.tv_weather);
        tv_temp=(TextView)v.findViewById(R.id.tv_temp);
        tv_humidity=(TextView)v.findViewById(R.id.tv_humidity);
        cd_today_data=(CardView)v.findViewById(R.id.cd_today_data);
        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);
        img_weather=(ImageView)v.findViewById(R.id.img_weather);

        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //Call bydefault weather api if internet available
        if(Utils.isNetworkConnected(mContext)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    api_get_weather();
                }
            }, 300);
        }else{
            if(Utils.getWeatherArr(tinyDB)!=null&&Utils.getWeatherArr(tinyDB).size()>0){
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
                    api_get_weather();
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = TravelContract.WeatherEntry.COLUMN_DATE + " ASC";

        String locationSetting = String.valueOf(tinyDB.getDouble(GeneralValues.USER_LAT_KEY,0));
        Uri weatherForLocationUri = TravelContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    //Api to get data from server
    public void api_get_weather(){
        reload.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        tv_message.setVisibility(View.VISIBLE);
        tv_message.setText(getString(R.string.loading));

        String weather_api=GeneralValues.WEATHER_BASE_URL+"daily?mode=json&lat="+String.valueOf(tinyDB.getDouble(GeneralValues.USER_LAT_KEY,0))+"&lon="+String.valueOf(tinyDB.getDouble(GeneralValues.USER_LONG_KEY,0))+"&APPID="+GeneralValues.WEATHERMAP_KEY+"&cnt=14";

        Call<ResponseBody> call = Utils.requestApi_Weather().requestJson_withValues_WeatherGet(weather_api);

        Utils.show_log("url = "+call.request().url());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonResponse = response.body().string();

                    ArrayList<WeatherArraylist> arr=new ArrayList<WeatherArraylist>();

                    JSONObject result = new JSONObject(jsonResponse);
                    JSONArray list_arr=new JSONArray(result.getString("list"));
                    Utils.show_log("arr = "+list_arr+" len = "+list_arr.length());

                    for(int i=0;i<list_arr.length();i++){
                        String w_des="",w_icon="",w_full_des="";
                        JSONObject full_data = list_arr.getJSONObject(i);
                        JSONObject tempjson = new JSONObject(full_data.getString("temp"));
                        JSONArray w_array=new JSONArray(full_data.getString("weather"));
                        if(w_array.length()>=0){
                            JSONObject w_data = w_array.getJSONObject(0);
                            w_des=w_data.getString("main");
                            w_icon=w_data.getString("icon");
                            w_full_des=w_data.getString("description");
                        }else{
                            w_des="";
                            w_icon="";
                        }
                        arr.add(new WeatherArraylist(full_data.getString("dt"),tempjson.getString("min"),tempjson.getString("max"),w_des,w_icon,full_data.getString("humidity"),w_full_des));
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

                        cd_today_data.setVisibility(View.GONE);
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

    //Here this function is used to set values in recycler view that get from server
    public void setValues(int position,String check){
        reload.setVisibility(View.GONE);
        progress_bar.setVisibility(View.GONE);
        tv_message.setVisibility(View.GONE);

        cd_today_data.setVisibility(View.VISIBLE);

        ArrayList<WeatherArraylist> arr;
        arr=Utils.getWeatherArr(tinyDB);
        tv_date.setText(""+Utils.getDayFromTimeStamp(arr.get(position).getDate())+","+Utils.getDateFromTimeStamp(arr.get(position).getDate()));
        String img="http://openweathermap.org/img/w/"+arr.get(position).getW_icon()+".png";
        Glide.with(mContext).load(img).placeholder(R.drawable.logo).into(img_weather);
        String des="";
        if(arr.get(position).getW_full_des()!=null&&!arr.get(position).getW_full_des().equalsIgnoreCase("")){
            des = arr.get(position).getW_full_des().substring(0,1).toUpperCase() + arr.get(position).getW_full_des().substring(1);
        }
        tv_weather.setText(des);
        tv_humidity.setText(arr.get(position).getHumidity()+"%");
        if(check.equalsIgnoreCase("C")){
            tv_temp.setText(Utils.convert_K2C(arr.get(position).getMax_temp())+"/"+Utils.convert_K2C(arr.get(position).getMin_temp())+"\u2103");
        }else{
            tv_temp.setText(Utils.convert_K2F(arr.get(position).getMax_temp())+"/"+Utils.convert_K2F(arr.get(position).getMin_temp())+"\u2109");
        }

        arr.remove(position);
        adapter = new WeatherAdapter(mContext);
        recyclerView.setAdapter(adapter);

        setBackground(position);
    }

    //Set bacgrounds according to the selected weather
    public void setBackground(int position){
        ArrayList<WeatherArraylist> arr=new ArrayList<>();
        arr=Utils.getWeatherArr(tinyDB);
        String weather=arr.get(position).getW_des();
        if(weather.equalsIgnoreCase("Thunderstorm")){
            Glide.with(mContext).load(R.drawable.thunderstorm).into(img_bg);
        }else if(weather.equalsIgnoreCase("Drizzle")){
            Glide.with(mContext).load(R.drawable.drizzle).into(img_bg);
        }else if(weather.equalsIgnoreCase("Rain")){
            Glide.with(mContext).load(R.drawable.drizzle).into(img_bg);
        }else if(weather.equalsIgnoreCase("Snow")){
            Glide.with(mContext).load(R.drawable.snow).into(img_bg);
        }else if(weather.equalsIgnoreCase("Atmosphere")){
            Glide.with(mContext).load(R.drawable.atmosphere).into(img_bg);
        }else if(weather.equalsIgnoreCase("Clear")){
            Glide.with(mContext).load(R.drawable.clear).into(img_bg);
        }else if(weather.equalsIgnoreCase("Clouds")){
            Glide.with(mContext).load(R.drawable.clouds).into(img_bg);
        }else if(weather.equalsIgnoreCase("Extreme")){
            Glide.with(mContext).load(R.drawable.extreme).into(img_bg);
        }else{
            Glide.with(mContext).load(R.drawable.blur_weather).into(img_bg);
        }
    }
}
