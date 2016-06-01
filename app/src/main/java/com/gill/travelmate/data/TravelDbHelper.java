package com.gill.travelmate.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gill.travelmate.data.TravelContract.HotelEntry;
import com.gill.travelmate.data.TravelContract.LocationEntry;
import com.gill.travelmate.data.TravelContract.PlacesNearByEntry;
import com.gill.travelmate.data.TravelContract.RestaurantEntry;

/**
 * Created by Praneeth on 5/20/2016.
 */
public class TravelDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "travel.db";

    public TravelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE "+ LocationEntry.TABLE_NAME + "("+
                LocationEntry._ID + " INTEGER PRIMARY KEY," +
                LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL," +
                LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL," +
                LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL," +
                LocationEntry.COLUMN_CITY + " REAL NOT NULL," +
                ");";

        final String SQL_CREATE_HOTEL_TABLE = "CREATE TABLE "+ HotelEntry.TABLE_NAME + "("+
                HotelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HotelEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL," +
                HotelEntry.COLUMN_ADDRESS + " TEXT NOT NULL," +
                HotelEntry.COLUMN_CATEGORY + " TEXT NOT NULL," +
                HotelEntry.COLUMN_DISTANCE + " REAL NOT NULL," +
                HotelEntry.COLUMN_ID + " REAL NOT NULL," +
                HotelEntry.COLUMN_IMAGE + " TEXT NOT NULL," +
                HotelEntry.COLUMN_LATITUDE + " REAL NOT NULL," +
                HotelEntry.COLUMN_LONGITUDE + " REAL NOT NULL," +
                HotelEntry.COLUMN_NAME + " TEXT NOT NULL," +
                HotelEntry.COLUMN_PHONE + " TEXT NOT NULL," +
                HotelEntry.COLUMN_RATING + " REAL NOT NULL," +
                HotelEntry.COLUMN_REVIEWS + " INTEGER NOT NULL," +
                HotelEntry.COLUMN_URL + " TEXT NOT NULL," +
                " FOREIGN KEY (" + HotelEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + HotelEntry._ID +

                ");";

        final String SQL_CREATE_PLACESNEARBY_TABLE = "CREATE TABLE "+ PlacesNearByEntry.TABLE_NAME + "("+
                PlacesNearByEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PlacesNearByEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL," +
                PlacesNearByEntry.COLUMN_ADDRESS + " TEXT NOT NULL," +
                PlacesNearByEntry.COLUMN_CATEGORY + " TEXT NOT NULL," +
                PlacesNearByEntry.COLUMN_DISTANCE + " REAL NOT NULL," +
                PlacesNearByEntry.COLUMN_ID + " REAL NOT NULL," +
                PlacesNearByEntry.COLUMN_IMAGE + " TEXT NOT NULL," +
                PlacesNearByEntry.COLUMN_LATITUDE + " REAL NOT NULL," +
                PlacesNearByEntry.COLUMN_LONGITUDE + " REAL NOT NULL," +
                PlacesNearByEntry.COLUMN_NAME + " TEXT NOT NULL," +
                PlacesNearByEntry.COLUMN_PHONE + " TEXT NOT NULL," +
                PlacesNearByEntry.COLUMN_RATING + " REAL NOT NULL," +
                PlacesNearByEntry.COLUMN_REVIEWS + " INTEGER NOT NULL," +
                PlacesNearByEntry.COLUMN_URL + " TEXT NOT NULL," +
                " FOREIGN KEY (" + PlacesNearByEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + HotelEntry._ID +

                ");";

        final String SQL_CREATE_RESTAURANT_TABLE = "CREATE TABLE "+ RestaurantEntry.TABLE_NAME + "("+
                RestaurantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RestaurantEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL," +
                RestaurantEntry.COLUMN_ADDRESS + " TEXT NOT NULL," +
                RestaurantEntry.COLUMN_CATEGORY + " TEXT NOT NULL," +
                RestaurantEntry.COLUMN_DISTANCE + " REAL NOT NULL," +
                RestaurantEntry.COLUMN_ID + " REAL NOT NULL," +
                RestaurantEntry.COLUMN_IMAGE + " TEXT NOT NULL," +
                RestaurantEntry.COLUMN_LATITUDE + " REAL NOT NULL," +
                RestaurantEntry.COLUMN_LONGITUDE + " REAL NOT NULL," +
                RestaurantEntry.COLUMN_NAME + " TEXT NOT NULL," +
                RestaurantEntry.COLUMN_PHONE + " TEXT NOT NULL," +
                RestaurantEntry.COLUMN_RATING + " REAL NOT NULL," +
                RestaurantEntry.COLUMN_REVIEWS + " INTEGER NOT NULL," +
                RestaurantEntry.COLUMN_URL + " TEXT NOT NULL," +
                " FOREIGN KEY (" + RestaurantEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + HotelEntry._ID + ");";

        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_HOTEL_TABLE);
        db.execSQL(SQL_CREATE_PLACESNEARBY_TABLE);
        db.execSQL(SQL_CREATE_RESTAURANT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HotelEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlacesNearByEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RestaurantEntry.TABLE_NAME);
        onCreate(db);
    }
}
