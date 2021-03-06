package com.gill.travelmate.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gill.travelmate.R;
import com.gill.travelmate.adapter.NearPlacesAdapter;
import com.gill.travelmate.adapter.PlacesFilterAdapter;
import com.gill.travelmate.arraylists.PlacesNearArraylist;
import com.gill.travelmate.utils.FontHelper;
import com.gill.travelmate.utils.GeneralValues;
import com.gill.travelmate.utils.TinyDB;
import com.gill.travelmate.utils.Utils;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Category;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

//Places Near main fragment
public class PlacesNearFragment extends Fragment implements View.OnClickListener {

    ProgressBar progress_bar;
    ImageView reload;
    TextView tv_message;
    Context mContext;
    TinyDB tinyDB;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    NearPlacesAdapter adapter;
    Dialog filter_dialog;
    ArrayList<String> cat_arr, values_arr;
    ArrayList<Integer> images;

    public PlacesNearFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_places_near, container, false);
        FontHelper.applyFont(getActivity(), v.findViewById(R.id.container_placesnear_fragment), "bauhaus.ttf");

        mContext = getActivity();
        tinyDB = new TinyDB(mContext);

        initialize_views(v);
        set_listener();
        set_filter_array();

        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if (Utils.isNetworkConnected(mContext)) {
            call_nearplaces_api("");
        } else {
            if (Utils.getPlacesArr(tinyDB) == null || Utils.getPlacesArr(tinyDB).size() <= 0) {
                progress_bar.setVisibility(View.GONE);
                reload.setVisibility(View.VISIBLE);
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(getString(R.string.no_internet_connection_try_again));
            } else {
                progress_bar.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);
                tv_message.setVisibility(View.GONE);
                adapter = new NearPlacesAdapter(mContext, Utils.getPlacesArr(tinyDB));
                recyclerView.setAdapter(adapter);
            }
        }

        return v;
    }

    //initialize views
    public void initialize_views(View v) {
        progress_bar = (ProgressBar) v.findViewById(R.id.progress_bar);
        reload = (ImageView) v.findViewById(R.id.reload);
        tv_message = (TextView) v.findViewById(R.id.tv_message);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        filter_dialog = new Dialog(mContext, R.style.DialogSlideAnim);
        filter_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filter_dialog.setContentView(R.layout.dialog_filter);
        filter_dialog.setCancelable(true);
    }

    //sel on click listener on views
    public void set_listener() {
        reload.setOnClickListener(this);
    }

    //set places near filter options
    public void set_filter_array() {
        cat_arr = new ArrayList<>();
        cat_arr.add("Arts & Entertainment");
        cat_arr.add("Bars");
        cat_arr.add("Beauty & Spas");
        cat_arr.add("Car Rental");
        cat_arr.add("Cinema");
        cat_arr.add("Health & Medical");
        cat_arr.add("Nightlife");
        cat_arr.add("Shopping");

        values_arr = new ArrayList<>();
        values_arr.add("arts");
        values_arr.add("bars");
        values_arr.add("beautysvc");
        values_arr.add("carrental");
        values_arr.add("movietheaters");
        values_arr.add("health");
        values_arr.add("nightlife");
        values_arr.add("shopping");

        images = new ArrayList<>();
        images.add(R.drawable.arts_ntertainment);
        images.add(R.drawable.bars);
        images.add(R.drawable.beauty_spas);
        images.add(R.drawable.car_rental);
        images.add(R.drawable.cinema);
        images.add(R.drawable.health_medical);
        images.add(R.drawable.nightlife);
        images.add(R.drawable.shopping);
    }

    //set functionality onclick on views
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reload:
                if (Utils.isNetworkConnected(mContext)) {
                    call_nearplaces_api("");
                } else {
                    progress_bar.setVisibility(View.GONE);
                    reload.setVisibility(View.VISIBLE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(getString(R.string.no_internet_connection_try_again));
                }
                break;
            default:
                break;
        }
    }

    //call api to get nearplaces data
    public void call_nearplaces_api(String sort) {
        reload.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        tv_message.setVisibility(View.VISIBLE);
        tv_message.setText(getString(R.string.loading));

        YelpAPIFactory apiFactory = new YelpAPIFactory(GeneralValues.YELP_CUSTOMER_KEY, GeneralValues.YELP_CUSTOMER_SECRET, GeneralValues.YELP_TOKEN, GeneralValues.YELP_TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();

        Map<String, String> params = new HashMap<>();
        params.put("term", "shopping");
        if (!sort.equalsIgnoreCase("")) {
            params.put("sort", sort);
        }
        if (!tinyDB.getString(GeneralValues.PLACES_FILTER).equalsIgnoreCase("")) {
            params.put("category_filter", tinyDB.getString(GeneralValues.PLACES_FILTER));
        }

        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(tinyDB.getDouble(GeneralValues.USER_LAT_KEY, 0))
                .longitude(tinyDB.getDouble(GeneralValues.USER_LONG_KEY, 0)).build();

        Call<SearchResponse> call = yelpAPI.search(coordinate, params);

        Utils.show_log("url = " + call.request().url());

        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                // Update UI text with the searchResponse.
                Utils.show_log("in result " + searchResponse);

                reload.setVisibility(View.GONE);
                progress_bar.setVisibility(View.GONE);
                tv_message.setVisibility(View.GONE);

                ArrayList<PlacesNearArraylist> arr = new ArrayList<>();

                try {
                    for (int i = 0; i < searchResponse.businesses().size(); i++) {
                        ArrayList<Category> cat_arr = searchResponse.businesses().get(i).categories();
                        String cat = "";
                        if (cat_arr.size() > 1) {
                            cat = cat_arr.get(0).name() + "," + cat_arr.get(1).name();
                        } else if (cat_arr.size() == 0) {
                            cat = "";
                        } else {
                            cat = cat_arr.get(0).name();
                        }
                        arr.add(new PlacesNearArraylist(searchResponse.businesses().get(i).id(),
                                searchResponse.businesses().get(i).name(), "" +
                                searchResponse.businesses().get(i).rating(), "" +
                                searchResponse.businesses().get(i).location().displayAddress(),
                                searchResponse.businesses().get(i).imageUrl(),
                                searchResponse.businesses().get(i).location().coordinate().latitude(),
                                searchResponse.businesses().get(i).location().coordinate().longitude(), "" +
                                searchResponse.businesses().get(i).reviewCount(), "" +
                                searchResponse.businesses().get(i).distance(),
                                cat,
                                searchResponse.businesses().get(i).phone(),
                                searchResponse.businesses().get(i).mobileUrl()));
                    }

                    //save data locally
                    Utils.setPlacesArr(tinyDB, arr);

                    Utils.show_log("arr size = " + Utils.getPlacesArr(tinyDB).size());

                    //set adapter
                    if (Utils.getPlacesArr(tinyDB) != null) {
                        adapter = new NearPlacesAdapter(mContext, Utils.getPlacesArr(tinyDB));
                        recyclerView.setAdapter(adapter);
                    }
                    if (Utils.getPlacesArr(tinyDB).size() <= 0) {
                        tv_message.setVisibility(View.VISIBLE);
                        tv_message.setText(getString(R.string.no_data_found));
                    }
                } catch (Exception e) {
                    reload.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(getString(R.string.error_while_fetching));
                    Utils.show_log("Exp = " + e.getMessage().toString());
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // HTTP error happened, do something to handle it.
                try {
                    Utils.show_log("in exp = " + t.getMessage().toString());
                    reload.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    if (t.getMessage().toString().equalsIgnoreCase(getString(R.string.bad_request))) {
                        tv_message.setText(getString(R.string.no_data_found_location));
                    } else if (t.getMessage().toString().contains(getString(R.string.failed_to_connect_yelp)) || t.getMessage().toString().contains(getString(R.string.unable_to_connect_host))) {
                        tv_message.setText(getString(R.string.failed_to_connect));
                    } else {
                        tv_message.setText(getString(R.string.error_in_loading_data));
                    }
                } catch (Exception e) {

                }
            }
        };
        call.enqueue(callback);
    }

    //show places near sort dialog
    public void showPlacesNearBySortDialog() {
        final Dialog dialog = new Dialog(mContext, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sort);
        dialog.setCancelable(true);

        FontHelper.applyFont(getActivity(), dialog.findViewById(R.id.container_sort_dialog), "bauhaus.ttf");

        LinearLayout ll_rating = (LinearLayout) dialog.findViewById(R.id.ll_rating);

        ll_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(mContext)) {
                    ArrayList<PlacesNearArraylist> arr = new ArrayList<PlacesNearArraylist>();
                    adapter = new NearPlacesAdapter(mContext, arr);
                    recyclerView.setAdapter(adapter);
                    call_nearplaces_api("2");
                    dialog.dismiss();
                } else {
                    Utils.showToast(mContext, getString(R.string.no_internet_connection));
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }

    //show places near filter dialog
    public void showPlacesNearByFilterDialog() {
        FontHelper.applyFont(getActivity(), filter_dialog.findViewById(R.id.container_filter_dialog), "bauhaus.ttf");

        RecyclerView recyclerView1 = (RecyclerView) filter_dialog.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager1;
        PlacesFilterAdapter adapter1;
        layoutManager1 = new LinearLayoutManager(mContext);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView1.setLayoutManager(layoutManager1);

        adapter1 = new PlacesFilterAdapter(mContext, cat_arr, values_arr, images, PlacesNearFragment.this);
        recyclerView1.setAdapter(adapter1);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = filter_dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        filter_dialog.show();
    }

    //call api to get selected filter data for places near
    public void onRecyclerClick(int position) {
        if (Utils.isNetworkConnected(mContext)) {
            tinyDB.putString(GeneralValues.PLACES_FILTER, values_arr.get(position));

            ArrayList<PlacesNearArraylist> arr = new ArrayList<PlacesNearArraylist>();
            adapter = new NearPlacesAdapter(mContext, arr);
            recyclerView.setAdapter(adapter);
            call_nearplaces_api("");
            filter_dialog.dismiss();
        } else {
            Utils.showToast(mContext, getString(R.string.no_internet_connection));
        }
    }
}
