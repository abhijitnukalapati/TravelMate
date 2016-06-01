package com.gill.travelmate.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Praneeth on 5/20/2016.
 */
public class TravelContract {

    public static final String CONTENT_AUTHORITY = "com.gill.travelmate";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_HOTEL = "hotel";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_RESTAURANT = "restaurant";
    public static final String PATH_PLACESNEARBY = "placesNearBy";

    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";
        public static final String COLUMN_LOCATION_SETTING = "location_setting";
        public static final String COLUMN_COORD_LAT = "latitude";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_COORD_LONG = "longitude";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class HotelEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HOTEL).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HOTEL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HOTEL;

        public static final String TABLE_NAME = "hotel";
        public static final String COLUMN_LOC_KEY = "location_id";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_REVIEWS = "reviews";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_URL = "url";

        public static Uri buildHotelUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildHotelLocationUri(String location) {
            return CONTENT_URI.buildUpon().appendPath(location).build();
        }

        public static String getLocationFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class RestaurantEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESTAURANT).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESTAURANT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESTAURANT;

        public static final String TABLE_NAME = "restaurant";
        public static final String COLUMN_LOC_KEY = "location_id";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_REVIEWS = "reviews";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_URL = "url";

        public static Uri buildRestaurantUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildRestaurantLocationUri(String location) {
            return CONTENT_URI.buildUpon().appendPath(location).build();
        }

        public static String getLocationFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class PlacesNearByEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACESNEARBY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACESNEARBY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACESNEARBY;

        public static final String TABLE_NAME = "placesNearBy";
        public static final String COLUMN_LOC_KEY = "location_id";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_REVIEWS = "reviews";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_URL = "url";

        public static Uri buildPlacesNearByUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPlacesNearByLocationUri(String location) {
            return CONTENT_URI.buildUpon().appendPath(location).build();
        }

        public static String getLocationFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}

