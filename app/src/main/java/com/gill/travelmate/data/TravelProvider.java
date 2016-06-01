package com.gill.travelmate.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.gill.travelmate.data.TravelContract.HotelEntry;
import com.gill.travelmate.data.TravelContract.LocationEntry;
import com.gill.travelmate.data.TravelContract.WeatherEntry;
import com.gill.travelmate.data.TravelContract.PlacesNearByEntry;
import com.gill.travelmate.data.TravelContract.RestaurantEntry;


/**
 * Created by Praneeth on 5/20/2016.
 */
public class TravelProvider extends ContentProvider {

    private TravelDbHelper mTravelDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int LOCATION = 100;
    static final int HOTEL = 200;
    static final int HOTEL_WITH_LOCATON = 201;
    //static final int HOTEL_WITH_LOCATON_AND_ID = 202;
    static final int RESTAURANT = 300;
    static final int RESTAURANT_WITH_LOCATON = 301;
    //static final int RESTAURANRT_WITH_LOCATON_AND_ID = 202;
    static final int PLACESNEARBY = 400;
    static final int PLACESNEARBY_WITH_LOCATON = 401;
    static final int WEATHER = 500;
    static final int WEATHER_WITH_LOCATION = 501;
    static final int WEATHER_WITH_LOCATION_AND_DATE = 502;
    //static final int PLACESNEARBY_WITH_LOCATON_AND_ID = 202;

    private static final SQLiteQueryBuilder sHotelByLocationQueryBuilder;
    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
    private static final SQLiteQueryBuilder sRestaurantByLocationQueryBuilder;
    private static final SQLiteQueryBuilder sPlacesNearByLocationQueryBuilder;

    static {
        sHotelByLocationQueryBuilder = new SQLiteQueryBuilder();
        sRestaurantByLocationQueryBuilder = new SQLiteQueryBuilder();
        sPlacesNearByLocationQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        LocationEntry.TABLE_NAME +
                        " ON " + WeatherEntry.TABLE_NAME +
                        "." + WeatherEntry.COLUMN_LOC_KEY +
                        " = " + LocationEntry.TABLE_NAME +
                        "." + LocationEntry._ID);

        sHotelByLocationQueryBuilder.setTables(
                LocationEntry.TABLE_NAME + "INNER JOIN" +
                        HotelEntry.TABLE_NAME + "ON" +
                        HotelEntry.TABLE_NAME + "." +
                        HotelEntry.COLUMN_LOC_KEY + "=" +
                        LocationEntry.TABLE_NAME + "." +
                        LocationEntry._ID);

        sRestaurantByLocationQueryBuilder.setTables(
                LocationEntry.TABLE_NAME + "INNER JOIN" +
                        RestaurantEntry.TABLE_NAME + "ON" +
                        RestaurantEntry.TABLE_NAME + "." +
                        RestaurantEntry.COLUMN_LOC_KEY + "=" +
                        LocationEntry.TABLE_NAME + "." +
                        LocationEntry._ID);

        sPlacesNearByLocationQueryBuilder.setTables(
                LocationEntry.TABLE_NAME + "INNER JOIN" +
                        PlacesNearByEntry.TABLE_NAME + "ON" +
                        PlacesNearByEntry.TABLE_NAME + "." +
                        PlacesNearByEntry.COLUMN_LOC_KEY + "=" +
                        LocationEntry.TABLE_NAME + "." +
                        LocationEntry._ID);
    }

    private static final String sLocationSelection =
            LocationEntry.TABLE_NAME + "." +
                    LocationEntry.COLUMN_LOCATION_SETTING + "=?";

    private static final String sLocationSettingWithStartDateSelection =
            LocationEntry.TABLE_NAME+
                    "." + LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherEntry.COLUMN_DATE + " >= ? ";

    //location.location_setting = ? AND date = ?
    private static final String sLocationSettingAndDaySelection =
            LocationEntry.TABLE_NAME +
                    "." + LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                   WeatherEntry.COLUMN_DATE + " = ? ";

    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherEntry.getLocationSettingFromUri(uri);
        long startDate = WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sLocationSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sWeatherByLocationSettingQueryBuilder.query(mTravelDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationSettingAndDate(
            Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherEntry.getLocationSettingFromUri(uri);
        long date = WeatherEntry.getDateFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(mTravelDbHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndDaySelection,
                new String[]{locationSetting, Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getHotelByLocation(
            Uri uri, String[] projection, String sortOrder) {

        String location = HotelEntry.getLocationFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sLocationSelection;
        selectionArgs = new String[]{location};

        return sHotelByLocationQueryBuilder.query(mTravelDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getRestaurantByLocation(
            Uri uri, String[] projection, String sortOrder) {

        String location = RestaurantEntry.getLocationFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sLocationSelection;
        selectionArgs = new String[]{location};

        return sRestaurantByLocationQueryBuilder.query(mTravelDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPlacesNearByLocation(
            Uri uri, String[] projection, String sortOrder) {

        String location = PlacesNearByEntry.getLocationFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sLocationSelection;
        selectionArgs = new String[]{location};

        return sPlacesNearByLocationQueryBuilder.query(mTravelDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TravelContract.CONTENT_AUTHORITY;

        sUriMatcher.addURI(authority, TravelContract.PATH_LOCATION, LOCATION);
        sUriMatcher.addURI(authority, TravelContract.PATH_HOTEL, HOTEL);
        sUriMatcher.addURI(authority, TravelContract.PATH_PLACESNEARBY, PLACESNEARBY);
        sUriMatcher.addURI(authority, TravelContract.PATH_RESTAURANT, RESTAURANT);
        sUriMatcher.addURI(authority, TravelContract.PATH_WEATHER, WEATHER);
        sUriMatcher.addURI(authority, TravelContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        sUriMatcher.addURI(authority, TravelContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);
        sUriMatcher.addURI(authority, TravelContract.PATH_HOTEL + "/*", HOTEL_WITH_LOCATON);
        sUriMatcher.addURI(authority, TravelContract.PATH_RESTAURANT + "/*", RESTAURANT_WITH_LOCATON);
        sUriMatcher.addURI(authority, TravelContract.PATH_PLACESNEARBY + "/*", PLACESNEARBY_WITH_LOCATON);

        return sUriMatcher;
    }

    @Override
    public boolean onCreate() {
        mTravelDbHelper = new TravelDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LOCATION:
                return LocationEntry.CONTENT_TYPE;
            case HOTEL:
                return HotelEntry.CONTENT_TYPE;
            case HOTEL_WITH_LOCATON:
                return HotelEntry.CONTENT_TYPE;
            case RESTAURANT:
                return RestaurantEntry.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherEntry.CONTENT_TYPE;
            case RESTAURANT_WITH_LOCATON:
                return RestaurantEntry.CONTENT_TYPE;
            case PLACESNEARBY:
                return PlacesNearByEntry.CONTENT_TYPE;
            case PLACESNEARBY_WITH_LOCATON:
                return PlacesNearByEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)) {
            case LOCATION:
                retCursor = mTravelDbHelper.getReadableDatabase().query(
                        LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case HOTEL:
                retCursor = mTravelDbHelper.getReadableDatabase().query(
                        LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case RESTAURANT:
                retCursor = mTravelDbHelper.getReadableDatabase().query(
                        LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PLACESNEARBY:
                retCursor = mTravelDbHelper.getReadableDatabase().query(
                        LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case WEATHER_WITH_LOCATION_AND_DATE:
            {
                retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case WEATHER_WITH_LOCATION: {
                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            }
            // "weather"
            case WEATHER: {
                retCursor = mTravelDbHelper.getReadableDatabase().query(
                        WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case HOTEL_WITH_LOCATON:
                retCursor = getHotelByLocation(uri, projection, sortOrder);
                break;
            case RESTAURANT_WITH_LOCATON:
                retCursor = getRestaurantByLocation(uri, projection, sortOrder);
                break;
            case PLACESNEARBY_WITH_LOCATON:
                retCursor = getPlacesNearByLocation(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(WeatherEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(WeatherEntry.COLUMN_DATE);
            values.put(WeatherEntry.COLUMN_DATE, TravelContract.normalizeDate(dateValue));
        }
    }
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mTravelDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match) {
            case LOCATION: {
                long _id = db.insert(LocationEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = LocationEntry.buildLocationUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case WEATHER: {
                normalizeDate(values);
                long _id = db.insert(WeatherEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case HOTEL: {
                long _id = db.insert(HotelEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = HotelEntry.buildHotelUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case RESTAURANT: {
                long _id = db.insert(RestaurantEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = RestaurantEntry.buildRestaurantUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PLACESNEARBY: {
                long _id = db.insert(PlacesNearByEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = PlacesNearByEntry.buildPlacesNearByUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mTravelDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match) {
            case LOCATION:
                rowsDeleted = db.delete(LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WEATHER:
                rowsDeleted = db.delete(
                        WeatherEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HOTEL:
                rowsDeleted = db.delete(HotelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RESTAURANT:
                rowsDeleted = db.delete(RestaurantEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLACESNEARBY:
                rowsDeleted = db.delete(PlacesNearByEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mTravelDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case LOCATION:
                rowsUpdated = db.delete(LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WEATHER:
                normalizeDate(values);
                rowsUpdated = db.update(WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case HOTEL:
                rowsUpdated = db.delete(HotelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RESTAURANT:
                rowsUpdated = db.delete(RestaurantEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLACESNEARBY:
                rowsUpdated = db.delete(PlacesNearByEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mTravelDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case WEATHER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case HOTEL:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(HotelEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case RESTAURANT:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RestaurantEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case PLACESNEARBY:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PlacesNearByEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
