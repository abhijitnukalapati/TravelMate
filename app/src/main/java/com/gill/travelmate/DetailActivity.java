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

    private Context mContext;
    private String id;
    private String name;
    private String rating;
    private String address;
    private String review;
    private String category;
    private String image;
    private String phone;
    private String url;
    private String distance;
    private String share_text;
    private double latitude = 0, longitude = 0;
    private TinyDB tinyDB;
    private CollapsingToolbarLayout toolbar_layout;
    private ImageView detail_image;
    private TextView addressTextView, categoriesTextView, reviewstextView, phoneTextView, urlTextView, ratingView;
    private RatingBar ratingBar;

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

        Intent i = getIntent();
        int position = Integer.parseInt(i.getStringExtra("position"));
        String check = i.getStringExtra("check");

        try {
            //Set detail page data accorting to selected item on previous page
            if (check.equalsIgnoreCase(GeneralValues.HOTEL_CHECK)) {

                ArrayList<HotelsArraylist> arr;
                arr = Utils.getHotelArr(tinyDB);

                latitude = arr.get(position).getLatitude();
                longitude = arr.get(position).getLongitude();
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
                share_text = "Here is a place I found on TravelMate\n\n" +
                        name + "\n" + url + "\n\n" +
                        address.substring(1, address.length() - 1);

            } else if (check.equalsIgnoreCase(GeneralValues.RESTAURANT_CHECK)) {

                ArrayList<RestaurantsArraylist> arr ;
                arr = Utils.getRestaurantArr(tinyDB);

                latitude = arr.get(position).getLatitude();
                longitude = arr.get(position).getLongitude();
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
                share_text = "Here is a place I found on TravelMate\n\n" +
                        name + "\n" + url + "\n\n" +
                        address.substring(1, address.length() - 1);

            } else if (check.equalsIgnoreCase(GeneralValues.PLACES_CHECK)) {
                ArrayList<PlacesNearArraylist> arr ;
                arr = Utils.getPlacesArr(tinyDB);

                latitude = arr.get(position).getLatitude();
                longitude = arr.get(position).getLongitude();
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
                share_text = "Here is a place I found on TravelMate\n\n" +
                        name + "\n" + url + "\n\n" +
                        address.substring(1, address.length() - 1);

            } else if (check.equalsIgnoreCase(GeneralValues.MYSAVES_CHECK)) {
                ArrayList<MySavesArraylist> arr ;
                arr = Utils.getMySavesArr(tinyDB);

                latitude = arr.get(position).getLatitude();
                longitude = arr.get(position).getLongitude();
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
                share_text = "Here is a place I found on TravelMate\n\n" +
                        name + "\n" + url + "\n\n" +
                        address.substring(1, address.length() - 1);
            }

            initializeViews();
            setListener();

            //Floating button for save functionality
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            if (fab != null) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkIfRecordIsSaved(id)) {
                            ArrayList<MySavesArraylist> arr;
                            arr = Utils.getMySavesArr(tinyDB);
                            arr.remove(getIndex(id));
                            Utils.setMySavesArr(tinyDB, arr);
                            fab.setImageResource(R.drawable.heart_empty);
                            Utils.showToast(mContext, getString(R.string.record_remove));
                        } else {
                            ArrayList<MySavesArraylist> arr = new ArrayList<>();
                            if (Utils.getMySavesArr(tinyDB) != null && Utils.getMySavesArr(tinyDB).size() > 0) {
                                arr = Utils.getMySavesArr(tinyDB);
                            }
                            arr.add(new MySavesArraylist(id, name, rating, address, image, latitude, longitude, review, distance, category, phone, url));
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
            addressTextView.setText(name + "\n\n" + address.substring(1, address.length() - 1));
            categoriesTextView.setText(category);

            String reviews = review + " " + mContext.getString(R.string.reviews_save);
            reviewstextView.setText(reviews);

            if (rating.equalsIgnoreCase("") || rating.equalsIgnoreCase("0.0")) {
                ratingView.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.GONE);
                ratingView.setText(getString(R.string.no_rating_available));
            } else {
                ratingView.setVisibility(View.GONE);
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(Float.parseFloat(rating));
            }

            if (phone == null || phone.equalsIgnoreCase("")) {
                phoneTextView.setText(getString(R.string.no_number_available));
            } else {
                phoneTextView.setText(phone);
            }

            if (url == null || url.equalsIgnoreCase("")) {
                urlTextView.setText(getString(R.string.no_url_available));
            } else {
                urlTextView.setText(R.string.details_website);
            }

            if (fab != null) {
                if (checkIfRecordIsSaved(id)) {

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
    public void initializeViews() {
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        detail_image = (ImageView) findViewById(R.id.detail_image);
        addressTextView = (TextView) findViewById(R.id.tv_address);
        categoriesTextView = (TextView) findViewById(R.id.tv_categories);
        reviewstextView = (TextView) findViewById(R.id.tv_review);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        urlTextView = (TextView) findViewById(R.id.tv_url);
        phoneTextView = (TextView) findViewById(R.id.tv_phone);
        ratingView = (TextView) findViewById(R.id.tv_rating_msg);

        Typeface tf = Typeface.createFromAsset(getAssets(), "bauhaus.ttf");
        toolbar_layout.setCollapsedTitleTypeface(tf);
        toolbar_layout.setExpandedTitleTypeface(tf);
    }

    //set listener on all views
    public void setListener() {
        phoneTextView.setOnClickListener(this);
        urlTextView.setOnClickListener(this);
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
                    Utils.intentToPhone(mContext, phoneTextView.getText().toString());
                }
                break;
            case R.id.tv_url:
                if (url == null || url.equalsIgnoreCase("")) {
                    Utils.showToast(mContext, getString(R.string.no_url_available));
                } else {
                    Utils.intentToBrowser(mContext, url);
                }
                break;
            default:
                break;
        }
    }

    //check record is already into MySaves or not
    public boolean checkIfRecordIsSaved(String id) {
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

    public int getIndex(String id) {
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
        GoogleMap mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker
        if (latitude == 0) {
            Utils.showToast(mContext, getString(R.string.can_not_find_location));
        } else {
            LatLng location = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(location).title(name));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
        }
    }
}
