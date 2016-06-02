package com.gill.travelmate.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.gill.travelmate.R;
import com.gill.travelmate.apiinterface.Apis;
import com.gill.travelmate.arraylists.HotelsArraylist;
import com.gill.travelmate.arraylists.MySavesArraylist;
import com.gill.travelmate.arraylists.PlacesNearArraylist;
import com.gill.travelmate.arraylists.RestaurantsArraylist;
import com.gill.travelmate.arraylists.WeatherArraylist;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;


public class Utils {

    /**
     * Request api to get weather data
     *
     * @return Returns Apis
     */
    public static Apis requestApi_Weather() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(GeneralValues.WEATHER_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        Apis apis = retrofit.create(Apis.class);
        return apis;
    }

    /**
     * Default api to get data
     *
     * @return Returns Apis
     */
    public static Apis requestApi_Default() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(GeneralValues.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        Apis apis = retrofit.create(Apis.class);
        return apis;
    }

    /**
     * Api with very less response time
     *
     * @return Returns Apis
     */
//    public static Apis requestApi_lesstime() {
//        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
//                .writeTimeout(5, TimeUnit.SECONDS)
//                .readTimeout(5, TimeUnit.SECONDS)
//                .build();
//        Retrofit retrofit = new Retrofit.Builder().baseUrl(GeneralValues.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();
//        Apis apis = retrofit.create(Apis.class);
//        return apis;
//    }
//
//    public static Apis requestApi() {
//        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .build();
//        Retrofit retrofit = new Retrofit.Builder().baseUrl(GeneralValues.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();
//        Apis apis = retrofit.create(Apis.class);
//        return apis;
//    }

    /**
     * Check internet availabilty
     *
     * @param mContext Context of activity or fragment
     * @return Returns true is internet connected and false if no internet connected
     */
    public static boolean isNetworkConnected(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    /**
     * Show toast message
     *
     * @param mContext Context of activity or fragment
     * @param message  Message that show into the Toast
     */
    public static void showToast(Context mContext, String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Show alert dialog
     *
     * @param mContext         Context of activity o fragment
     * @param message          Message that shows on Dialog
     * @param title            Title for dialog
     * @param positiveText     Set positive text
     * @param positiveListener Set functionality on positive button click
     * @param negativeListener Set functionality on negative button click
     * @param negativeText     Negative button text
     * @param neutralText      Neturat button text
     * @param neutralListener  Set Netural button listener
     * @return dialog
     */
    public static AlertDialog.Builder showDialog(Context mContext, String message, String title, String positiveText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener, String negativeText, String neutralText, DialogInterface.OnClickListener neutralListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setNegativeButton(negativeText, negativeListener);
        alert.setPositiveButton(positiveText, positiveListener);
        alert.setNeutralButton(neutralText, neutralListener);
        alert.show();
        return alert;
    }

    /**
     * Make dialog with progress bar
     *
     * @param mContext Context of the Activity or fragment
     * @return Dialog with progress bar
     */
    public static Dialog get_progressDialog(Context mContext) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(lp);
        return dialog;
    }


    /**
     * Simple Share Intent
     *
     * @param mContext Context of the Activity or Fragment.
     * @param text     Text that you want to share with intent
     */
    public static void shareContent(Context mContext, String text, String name) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out " + name +" on TravelMate");
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        mContext.startActivity(sendIntent);
    }


    /**
     * Simple Browser Intent
     *
     * @param mContext Context of the Activity or Fragment.
     * @param url      Url that you want to open in Browser
     */
    public static void intent_to_Browser(Context mContext, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(intent);
        } catch (Exception e) {

        }
    }

    /**
     * Intent to Phone
     *
     * @param mContext Context of the Activity or Fragment.
     * @param number   Number on which want to make a call
     */
    public static void intent_to_phone(Context mContext, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        mContext.startActivity(intent);
    }

    /**
     * Check weather device is Tablet or not.
     *
     * @param mContext Context of the Activity.
     * @return Returns true if device is Tablet and false when its not.
     */
    public static boolean isTablet(Context mContext) {
        return (mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * Show Log
     *
     * @param message Message that want to show into Log
     */
    public static void show_log(String message) {
        Log.e("Log Message", "" + message);
    }

    /**
     * Goto any Fragment
     *
     * @param mContext  Context of the Activity of Fragment.
     * @param fragment  Fragment that want to open
     * @param container Container in which want to infulate Fragment
     */
    public static void goToFragment(Context mContext, Fragment fragment, int container) {
        android.support.v4.app.FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.exit_anim, R.anim.enter_anim);
        transaction.replace(container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    /**
     * Hide Soft Keyboard
     *
     * @param mContext Context of the Activity or Fragment.
     * @param view     Current focus of View
     */
    public static void hideKeyboard(Context mContext, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Set Typeface into TextView
     *
     * @param mContext Context of the Activity or Fragment.
     * @param tv       TextView to set Typeface
     */
    public static void typeface_font(Context mContext, TextView tv) {
        Typeface faceb = Typeface.createFromAsset(mContext.getAssets(), "bauhaus.ttf");
        tv.setTypeface(faceb);
    }

    /**
     * Get HotelArray from TinyDB(Local Storage)
     *
     * @param db TinyDB object
     * @return Returns Arraylist of HotelsArraylist Type
     */
    public static ArrayList<HotelsArraylist> getHotelArr(TinyDB db) {
        Gson gson = new Gson();
        String userStr = db.getString(GeneralValues.HOTEL_ARR);
        return gson.fromJson(userStr, new TypeToken<ArrayList<HotelsArraylist>>() {
        }.getType());
    }

    /**
     * Save HotelsArraylist into TinyDB(Local Storage)
     *
     * @param db       TinyDB object
     * @param userPojo Pojo class of HotelsArraylist Type
     */
    public static void setHotelArr(TinyDB db, ArrayList<HotelsArraylist> userPojo) {
        Gson gson = new Gson();
        db.putString(GeneralValues.HOTEL_ARR, gson.toJson(userPojo));
    }

    /**
     * Get RestaurantArray from TinyDB(Local Storage)
     *
     * @param db TinyDB object
     * @return Returns Arraylist of RestaurantArraylist Type
     */
    public static ArrayList<RestaurantsArraylist> getRestaurantArr(TinyDB db) {
        Gson gson = new Gson();
        String userStr = db.getString(GeneralValues.RESTAURANT_ARR);
        return gson.fromJson(userStr, new TypeToken<ArrayList<RestaurantsArraylist>>() {
        }.getType());
    }

    /**
     * Save RestaurantsArraylist into TinyDB(Local Storage)
     *
     * @param db       TinyDB object
     * @param userPojo Pojo class of RestaurantsArraylist Type
     */
    public static void setRestaurantArr(TinyDB db, ArrayList<RestaurantsArraylist> userPojo) {
        Gson gson = new Gson();
        db.putString(GeneralValues.RESTAURANT_ARR, gson.toJson(userPojo));
    }

    /**
     * Get PlacesNearArray from TinyDB(Local Storage)
     *
     * @param db TinyDB object
     * @return Returns Arraylist of PlacesNearArraylist Type
     */
    public static ArrayList<PlacesNearArraylist> getPlacesArr(TinyDB db) {
        Gson gson = new Gson();
        String userStr = db.getString(GeneralValues.PLACES_NEAR_ARR);
        return gson.fromJson(userStr, new TypeToken<ArrayList<PlacesNearArraylist>>() {
        }.getType());
    }

    /**
     * Save PlacesNearArraylist into TinyDB(Local Storage)
     *
     * @param db       TinyDB object
     * @param userPojo Pojo class of PlacesNearArraylist Type
     */
    public static void setPlacesArr(TinyDB db, ArrayList<PlacesNearArraylist> userPojo) {
        Gson gson = new Gson();
        db.putString(GeneralValues.PLACES_NEAR_ARR, gson.toJson(userPojo));
    }

    /**
     * Get MySavesArray from TinyDB(Local Storage)
     *
     * @param db TinyDB object
     * @return Returns Arraylist of MySavesArraylist Type
     */
    public static ArrayList<MySavesArraylist> getMySavesArr(TinyDB db) {
        Gson gson = new Gson();
        String userStr = db.getString(GeneralValues.MY_SAVES_ARR);
        return gson.fromJson(userStr, new TypeToken<ArrayList<MySavesArraylist>>() {
        }.getType());
    }

    /**
     * Save MySavesArraylist into TinyDB(Local Storage)
     *
     * @param db       TinyDB object
     * @param userPojo Pojo class of MySavesArraylist Type
     */
    public static void setMySavesArr(TinyDB db, ArrayList<MySavesArraylist> userPojo) {
        Gson gson = new Gson();
        db.putString(GeneralValues.MY_SAVES_ARR, gson.toJson(userPojo));
    }

    /**
     * Get WeatherArray from TinyDB(Local Storage)
     *
     * @param db TinyDB object
     * @return Returns Arraylist of WeatherArraylist Type
     */
    public static ArrayList<WeatherArraylist> getWeatherArr(TinyDB db) {
        Gson gson = new Gson();
        String userStr = db.getString(GeneralValues.WEATHER_ARR);
        return gson.fromJson(userStr, new TypeToken<ArrayList<WeatherArraylist>>() {
        }.getType());
    }

    /**
     * Save WeatherArraylist into TinyDB(Local Storage)
     *
     * @param db       TinyDB object
     * @param userPojo Pojo class of WeatherArraylist Type
     */
    public static void setWeatherArr(TinyDB db, ArrayList<WeatherArraylist> userPojo) {
        Gson gson = new Gson();
        db.putString(GeneralValues.WEATHER_ARR, gson.toJson(userPojo));
    }

    /**
     * Convert Temperature from Kelvin to Celsius
     *
     * @param temp Temperature that want to convert
     * @return Returns Temperature in Celsius
     */
    public static String convert_K2C(String temp) {
        float result = 0;
        result = Float.parseFloat(temp) - Float.parseFloat("273.15");
        return String.format("%.1f", result);
    }

    /**
     * Convert Temprature from Kelvin to Fahrenheit
     *
     * @param temp Temperature that want to convert
     * @return Returns Temperature in Fahrenheit
     */
    public static String convert_K2F(String temp) {
        float result = 0;
        result = (Float.parseFloat(temp) - Float.parseFloat("273.15")) * Float.parseFloat("1.8000") + Float.parseFloat("32.00");
        return String.format("%.1f", result);
    }

    /**
     * Get day from Timestamp
     *
     * @param TimeInMilis TimeStamp
     * @return Returns day according to give Timestamp
     */
    public static String getDayFromTimeStamp(String TimeInMilis) {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(TimeInMilis) * 1000);
        weekDay = dayFormat.format(calendar.getTime());
        return weekDay;
    }

    /**
     * Get Date from Timestamp
     *
     * @param TimeInMilis Timestamp
     * @return Returns Date according to give Timestamp
     */
    public static String getDateFromTimeStamp(String TimeInMilis) {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(TimeInMilis) * 1000);
        weekDay = dayFormat.format(calendar.getTime());
        return weekDay;
    }
}
