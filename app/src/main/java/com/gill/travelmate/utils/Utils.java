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

    public static String formatTemperature(Context context, double temperature) {
        // Data stored in Celsius by default.  If user prefers to see in Fahrenheit, convert
        // the values here.
        String suffix = "\u00B0";
        if (!isMetric(context)) {
            temperature = (temperature * 1.8) + 32;
        }

        // For presentation, assume the user doesn't care about tenths of a degree.
        return String.format(context.getString(R.string.format_temperature), temperature);
    }

    private static boolean isMetric(Context context) {
        return true;
    }

    public static String getStringForWeatherCondition(Context context, int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        int stringId;
        if (weatherId >= 200 && weatherId <= 232) {
            stringId = R.string.condition_2xx;
        } else if (weatherId >= 300 && weatherId <= 321) {
            stringId = R.string.condition_3xx;
        } else switch (weatherId) {
            case 500:
                stringId = R.string.condition_500;
                break;
            case 501:
                stringId = R.string.condition_501;
                break;
            case 502:
                stringId = R.string.condition_502;
                break;
            case 503:
                stringId = R.string.condition_503;
                break;
            case 504:
                stringId = R.string.condition_504;
                break;
            case 511:
                stringId = R.string.condition_511;
                break;
            case 520:
                stringId = R.string.condition_520;
                break;
            case 531:
                stringId = R.string.condition_531;
                break;
            case 600:
                stringId = R.string.condition_600;
                break;
            case 601:
                stringId = R.string.condition_601;
                break;
            case 602:
                stringId = R.string.condition_602;
                break;
            case 611:
                stringId = R.string.condition_611;
                break;
            case 612:
                stringId = R.string.condition_612;
                break;
            case 615:
                stringId = R.string.condition_615;
                break;
            case 616:
                stringId = R.string.condition_616;
                break;
            case 620:
                stringId = R.string.condition_620;
                break;
            case 621:
                stringId = R.string.condition_621;
                break;
            case 622:
                stringId = R.string.condition_622;
                break;
            case 701:
                stringId = R.string.condition_701;
                break;
            case 711:
                stringId = R.string.condition_711;
                break;
            case 721:
                stringId = R.string.condition_721;
                break;
            case 731:
                stringId = R.string.condition_731;
                break;
            case 741:
                stringId = R.string.condition_741;
                break;
            case 751:
                stringId = R.string.condition_751;
                break;
            case 761:
                stringId = R.string.condition_761;
                break;
            case 762:
                stringId = R.string.condition_762;
                break;
            case 771:
                stringId = R.string.condition_771;
                break;
            case 781:
                stringId = R.string.condition_781;
                break;
            case 800:
                stringId = R.string.condition_800;
                break;
            case 801:
                stringId = R.string.condition_801;
                break;
            case 802:
                stringId = R.string.condition_802;
                break;
            case 803:
                stringId = R.string.condition_803;
                break;
            case 804:
                stringId = R.string.condition_804;
                break;
            case 900:
                stringId = R.string.condition_900;
                break;
            case 901:
                stringId = R.string.condition_901;
                break;
            case 902:
                stringId = R.string.condition_902;
                break;
            case 903:
                stringId = R.string.condition_903;
                break;
            case 904:
                stringId = R.string.condition_904;
                break;
            case 905:
                stringId = R.string.condition_905;
                break;
            case 906:
                stringId = R.string.condition_906;
                break;
            case 951:
                stringId = R.string.condition_951;
                break;
            case 952:
                stringId = R.string.condition_952;
                break;
            case 953:
                stringId = R.string.condition_953;
                break;
            case 954:
                stringId = R.string.condition_954;
                break;
            case 955:
                stringId = R.string.condition_955;
                break;
            case 956:
                stringId = R.string.condition_956;
                break;
            case 957:
                stringId = R.string.condition_957;
                break;
            case 958:
                stringId = R.string.condition_958;
                break;
            case 959:
                stringId = R.string.condition_959;
                break;
            case 960:
                stringId = R.string.condition_960;
                break;
            case 961:
                stringId = R.string.condition_961;
                break;
            case 962:
                stringId = R.string.condition_962;
                break;
            default:
                return context.getString(R.string.condition_unknown, weatherId);
        }
        return context.getString(stringId);
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
