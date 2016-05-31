package com.gill.travelmate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gill.travelmate.arraylists.HotelsArraylist;
import com.gill.travelmate.arraylists.MySavesArraylist;
import com.gill.travelmate.arraylists.PlacesNearArraylist;
import com.gill.travelmate.arraylists.RestaurantsArraylist;
import com.gill.travelmate.utils.FontHelper;
import com.gill.travelmate.utils.GeneralValues;
import com.gill.travelmate.utils.TinyDB;
import com.gill.travelmate.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

//Detail activity to show detail of everything
public class DetailActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    Context mContext;
    Intent i;
    String id = "", name = "", rating = "", address = "", review = "", category = "", check = "", image = "", phone = "", url = "", distance = "", share_text = "";
    double lat = 0, lng = 0;
    int position = 0;
    TinyDB tinyDB;
    CollapsingToolbarLayout toolbar_layout;
    ImageView detail_image;
    TextView tv_address, tv_categories, tv_review, tv_phone, tv_url, tv_rating_msg;
    RatingBar ratingBar;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = DetailActivity.this;
        tinyDB = new TinyDB(mContext);

        FontHelper.applyFont(this, findViewById(R.id.container_detail), "bauhaus.ttf");

        i = getIntent();
        position = Integer.parseInt(i.getStringExtra("position"));
        check = i.getStringExtra("check");

        try {
            //Set detail page data accorting to selected item on previous page
            if (check.equalsIgnoreCase(GeneralValues.HOTEL_CHECK)) {
                ArrayList<HotelsArraylist> arr;
                arr = Utils.getHotelArr(tinyDB);
                lat = arr.get(position).getLatitude();
                lng = arr.get(position).getLongitude();
                id = arr.get(position).getId();
                name = arr.get(position).getName();
                rating = arr.get(position).getRating();
                address = arr.get(position).getAddress();
                review = arr.get(position).getReviews();
                category = arr.get(position).getCategory();
                image = arr.get(position).getImage();
                if (image != null) {
                    image = image.substring(0, image.length() - 6) + "o.jpg";
                } else {
                    image = "";
                }
                phone = arr.get(position).getPhone();
                url = arr.get(position).getUrl();
                distance = arr.get(position).getDistance();

                share_text = "Here is a place I found on TravelMate\n\n" + name + "\n" + url + "\n\n" + address.substring(1, address.length() - 1);
            } else if (check.equalsIgnoreCase(GeneralValues.RESTAURANT_CHECK)) {
                ArrayList<RestaurantsArraylist> arr ;
                arr = Utils.getRestaurantArr(tinyDB);
                lat = arr.get(position).getLatitude();
                lng = arr.get(position).getLongitude();
                id = arr.get(position).getId();
                name = arr.get(position).getName();
                rating = arr.get(position).getRating();
                address = arr.get(position).getAddress();
                review = arr.get(position).getReviews();
                category = arr.get(position).getCategory();
                image = arr.get(position).getImage();
                if (image != null) {
                    image = image.substring(0, image.length() - 6) + "o.jpg";
                } else {
                    image = "";
                }
                phone = arr.get(position).getPhone();
                url = arr.get(position).getUrl();
                distance = arr.get(position).getDistance();

                share_text = "Here is a place I found on TravelMate\n\n" + name + "\n" + url + "\n\n" + address.substring(1, address.length() - 1);
            } else if (check.equalsIgnoreCase(GeneralValues.PLACES_CHECK)) {
                ArrayList<PlacesNearArraylist> arr ;
                arr = Utils.getPlacesArr(tinyDB);
                lat = arr.get(position).getLatitude();
                lng = arr.get(position).getLongitude();
                id = arr.get(position).getId();
                name = arr.get(position).getName();
                rating = arr.get(position).getRating();
                address = arr.get(position).getAddress();
                review = arr.get(position).getReviews();
                category = arr.get(position).getCategory();
                image = arr.get(position).getImage();
                if (image != null) {
                    image = image.substring(0, image.length() - 6) + "o.jpg";
                } else {
                    image = "";
                }
                phone = arr.get(position).getPhone();
                url = arr.get(position).getUrl();
                distance = arr.get(position).getDistance();

                share_text = "Here is a place I found on TravelMate\n\n" + name + "\n" + url + "\n\n" + address.substring(1, address.length() - 1);
            } else if (check.equalsIgnoreCase(GeneralValues.MYSAVES_CHECK)) {
                ArrayList<MySavesArraylist> arr ;
                arr = Utils.getMySavesArr(tinyDB);
                lat = arr.get(position).getLatitude();
                lng = arr.get(position).getLongitude();
                id = arr.get(position).getId();
                name = arr.get(position).getName();
                rating = arr.get(position).getRating();
                address = arr.get(position).getAddress();
                review = arr.get(position).getReviews();
                category = arr.get(position).getCategory();
                image = arr.get(position).getImage();
                phone = arr.get(position).getPhone();
                url = arr.get(position).getUrl();
                distance = arr.get(position).getDistance();

                share_text = "Here is a place I found on TravelMate\n\n" + name + "\n" + url + "\n\n" + address.substring(1, address.length() - 1);
            }

            initialize_views();
            set_listener();

            //Floating button for save functionality
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            if (fab != null) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (check_record_in_MySaves(id)) {
                            ArrayList<MySavesArraylist> arr;
                            arr = Utils.getMySavesArr(tinyDB);
                            arr.remove(get_index(id));
                            Utils.setMySavesArr(tinyDB, arr);
                            fab.setImageResource(R.drawable.heart_empty);
                            Utils.showToast(mContext, getString(R.string.record_remove));
                        } else {
                            ArrayList<MySavesArraylist> arr = new ArrayList<>();
                            if (Utils.getMySavesArr(tinyDB) != null && Utils.getMySavesArr(tinyDB).size() > 0) {
                                arr = Utils.getMySavesArr(tinyDB);
                            }
                            arr.add(new MySavesArraylist(id, name, rating, address, image, lat, lng, review, distance, category, phone, url));
                            Utils.setMySavesArr(tinyDB, arr);
                            fab.setImageResource(R.drawable.heart_filled);
                            Utils.showToast(mContext, getString(R.string.record_added));
                        }
                    }
                });
            }

            //Set all views values
            toolbar_layout.setTitle(name);
            Glide.with(mContext).load(image).placeholder(R.drawable.logo).error(R.drawable.no_image).into(detail_image);
            tv_address.setText(name + "\n\n" + address.substring(1, address.length() - 1));
            tv_categories.setText(category);
            String reviews = review + " " + mContext.getString(R.string.reviews_save);
            tv_review.setText(reviews);
            if (rating.equalsIgnoreCase("") || rating.equalsIgnoreCase("0.0")) {
                tv_rating_msg.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.GONE);
                tv_rating_msg.setText(getString(R.string.no_rating_available));
            } else {
                tv_rating_msg.setVisibility(View.GONE);
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(Float.parseFloat(rating));
            }

            if (phone == null || phone.equalsIgnoreCase("")) {
                tv_phone.setText(getString(R.string.no_number_available));
            } else {
                tv_phone.setText(phone);
            }
            if (url == null || url.equalsIgnoreCase("")) {
                tv_url.setText(getString(R.string.no_url_available));
            } else {
                tv_url.setText(R.string.details_website);
            }
            if (fab != null) {
                if (check_record_in_MySaves(id)) {

                    fab.setImageResource(R.drawable.heart_filled);
                } else {
                    fab.setImageResource(R.drawable.heart_empty);
                }
            }

            //Set map view
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (Exception e) {

        }
    }

    //initialize all views
    public void initialize_views() {
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        detail_image = (ImageView) findViewById(R.id.detail_image);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_categories = (TextView) findViewById(R.id.tv_categories);
        tv_review = (TextView) findViewById(R.id.tv_review);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tv_url = (TextView) findViewById(R.id.tv_url);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_rating_msg = (TextView) findViewById(R.id.tv_rating_msg);

        Typeface tf = Typeface.createFromAsset(getAssets(), "bauhaus.ttf");
        toolbar_layout.setCollapsedTitleTypeface(tf);
        toolbar_layout.setExpandedTitleTypeface(tf);
    }

    //set listener on all views
    public void set_listener() {
        tv_phone.setOnClickListener(this);
        tv_url.setOnClickListener(this);
    }

    //setting menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DetailActivity.this.finish();
                overridePendingTransition(R.anim.toright_in, R.anim.toright_out);
                return true;
            case R.id.menu_item_share:
                Utils.shareContent(mContext, share_text, name);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DetailActivity.this.finish();
        overridePendingTransition(R.anim.toright_in, R.anim.toright_out);
    }

    //on click on views
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_phone:
                if (phone == null || phone.equalsIgnoreCase("")) {
                    Utils.showToast(mContext, getString(R.string.no_number_available));
                } else {
                    Utils.intent_to_phone(mContext, tv_phone.getText().toString());
                }
                break;
            case R.id.tv_url:
                if (url == null || url.equalsIgnoreCase("")) {
                    Utils.showToast(mContext, getString(R.string.no_url_available));
                } else {
                    Utils.intent_to_Browser(mContext, url);
                }
                break;
            default:
                break;
        }
    }

    //check record is already into MySaves or not
    public boolean check_record_in_MySaves(String id) {
        boolean token = false;
        if (Utils.getMySavesArr(tinyDB) != null && Utils.getMySavesArr(tinyDB).size() > 0) {
            ArrayList<MySavesArraylist> arr;
            arr = Utils.getMySavesArr(tinyDB);
            for (int i = 0; i < arr.size(); i++) {
                if (id.equalsIgnoreCase(arr.get(i).getId())) {
                    token = true;
                    break;
                } else {
                    token = false;
                }
            }
        } else {
            token = false;
        }
        return token;
    }

    public int get_index(String id) {
        int index = 0;
        ArrayList<MySavesArraylist> arr;
        arr = Utils.getMySavesArr(tinyDB);
        for (int i = 0; i < arr.size(); i++) {
            if (id.equalsIgnoreCase(arr.get(i).getId())) {
                index = i;
                break;
            } else {
                index = 0;
            }
        }
        return index;
    }

    //initialize map and add location
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker
        if (lat == 0) {
            Utils.showToast(mContext, getString(R.string.can_not_find_location));
        } else {
            LatLng location = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(location).title(name));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
        }
    }
}
