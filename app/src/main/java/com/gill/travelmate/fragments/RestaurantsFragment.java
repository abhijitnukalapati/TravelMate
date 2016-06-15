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
import com.gill.travelmate.adapter.RestaurantAdapter;
import com.gill.travelmate.adapter.RestaurantFilterAdapter;
import com.gill.travelmate.arraylists.RestaurantsArraylist;
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

//Restaurant main fragment
public class RestaurantsFragment extends Fragment implements View.OnClickListener{

    private ProgressBar progressBar;
    private ImageView reload;
    private TextView tv_message;
    private Context mContext;
    private TinyDB tinyDB;
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private Dialog filterDialog;
    private ArrayList<String> categoriesArray, valuesArray;
    private ArrayList<Integer> images;

    public RestaurantsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_restaurants, container, false);
        FontHelper.applyFont(getActivity(), v.findViewById(R.id.container_restaurants_fragment), "bauhaus.ttf");

        mContext=getActivity();
        tinyDB=new TinyDB(mContext);

        initializeViews(v);
        setListener();
        setFilterArray();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //Call default api according to the internet availablity
        if(Utils.isNetworkConnected(mContext)){
            getRestaurantDetailsFromYelp("");
        }else{
            if(Utils.getRestaurantArr(tinyDB)==null||Utils.getRestaurantArr(tinyDB).size()<=0){
                progressBar.setVisibility(View.GONE);
                reload.setVisibility(View.VISIBLE);
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(getString(R.string.no_internet_connection_try_again));
            }else{
                progressBar.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);
                tv_message.setVisibility(View.GONE);
                adapter = new RestaurantAdapter(mContext, Utils.getRestaurantArr(tinyDB));
                recyclerView.setAdapter(adapter);
            }
        }

        return v;
    }

    //initialize all views
    public void initializeViews(View v){
        progressBar =(ProgressBar)v.findViewById(R.id.progress_bar);
        reload=(ImageView)v.findViewById(R.id.reload);
        tv_message=(TextView)v.findViewById(R.id.tv_message);
        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);

        filterDialog = new Dialog(mContext,R.style.DialogSlideAnim);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setContentView(R.layout.dialog_filter);
        filterDialog.setCancelable(true);
    }

    //set on click listener on views
    public void setListener(){
        reload.setOnClickListener(this);
    }

    //set filter list of restaurant
    public void setFilterArray(){
        categoriesArray =new ArrayList<>();
        categoriesArray.add("American");
        categoriesArray.add("Burgers");
        categoriesArray.add("Chinese");
        categoriesArray.add("Delis");
        categoriesArray.add("Indian");
        categoriesArray.add("Italian");
        categoriesArray.add("Japanese");
        categoriesArray.add("Mexican");
        categoriesArray.add("Pizza");
        categoriesArray.add("SeaFood");

        valuesArray =new ArrayList<>();
        valuesArray.add("newamerican");
        valuesArray.add("burgers");
        valuesArray.add("chinese");
        valuesArray.add("delis");
        valuesArray.add("indpak");
        valuesArray.add("italian");
        valuesArray.add("japanese");
        valuesArray.add("mexican");
        valuesArray.add("pizza");
        valuesArray.add("seafood");

        images = new ArrayList<>();
        images.add(R.drawable.american);
        images.add(R.drawable.burgers);
        images.add(R.drawable.chinese);
        images.add(R.drawable.delis);
        images.add(R.drawable.indian);
        images.add(R.drawable.italian);
        images.add(R.drawable.japanese);
        images.add(R.drawable.mexican);
        images.add(R.drawable.pizza);
        images.add(R.drawable.sea_food);
    }

    //add onclick functionality on views
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reload:
                if(Utils.isNetworkConnected(mContext)){
                    getRestaurantDetailsFromYelp("");
                }else{
                    progressBar.setVisibility(View.GONE);
                    reload.setVisibility(View.VISIBLE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(getString(R.string.no_internet_connection_try_again));
                }
                break;
            default:
                break;
        }
    }

    //call api to get restaurant data from server
    public void getRestaurantDetailsFromYelp(String sort){
        reload.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tv_message.setVisibility(View.VISIBLE);
        tv_message.setText(getString(R.string.loading));

        YelpAPIFactory apiFactory = new YelpAPIFactory(GeneralValues.YELP_CUSTOMER_KEY, GeneralValues.YELP_CUSTOMER_SECRET, GeneralValues.YELP_TOKEN, GeneralValues.YELP_TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();

        Map<String, String> params = new HashMap<>();
        params.put("term", "restaurants");
        if(!sort.equalsIgnoreCase("")){
            params.put("sort", sort);
        }
        if(!tinyDB.getString(GeneralValues.RESTAURANT_FILTER).equalsIgnoreCase("")){
            params.put("category_filter",tinyDB.getString(GeneralValues.RESTAURANT_FILTER));
        }

        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(tinyDB.getDouble(GeneralValues.USER_LAT_KEY,0))
                .longitude(tinyDB.getDouble(GeneralValues.USER_LONG_KEY,0)).build();

        Call<SearchResponse> call = yelpAPI.search(coordinate, params);

        Utils.show_log("url = "+call.request().url());

        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                // Update UI text with the searchResponse.
                Utils.show_log("in result "+searchResponse);

                reload.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                tv_message.setVisibility(View.GONE);

                ArrayList<RestaurantsArraylist> arr=new ArrayList<>();

                try{
                    for(int i=0;i<searchResponse.businesses().size();i++){
                        ArrayList<Category> cat_arr=searchResponse.businesses().get(i).categories();
                        String cat="";
                        if(cat_arr.size()>1){
                            cat=cat_arr.get(0).name()+","+cat_arr.get(1).name();
                        }else if(cat_arr.size()==0){
                            cat="";
                        }else{
                            cat=cat_arr.get(0).name();
                        }
                        arr.add(new RestaurantsArraylist(searchResponse.businesses().get(i).id(),
                                searchResponse.businesses().get(i).name(),""+
                                searchResponse.businesses().get(i).rating(),""+
                                searchResponse.businesses().get(i).location().displayAddress(),
                                searchResponse.businesses().get(i).imageUrl(),
                                searchResponse.businesses().get(i).location().coordinate().latitude(),
                                searchResponse.businesses().get(i).location().coordinate().longitude(),""+
                                searchResponse.businesses().get(i).reviewCount(),""+
                                searchResponse.businesses().get(i).distance(),
                                cat,
                                searchResponse.businesses().get(i).phone(),
                                searchResponse.businesses().get(i).mobileUrl()));
                    }

                    //Save data locally
                    Utils.setRestaurantArr(tinyDB,arr);

                    Utils.show_log("arr size = "+Utils.getRestaurantArr(tinyDB).size());

                    //set adapter
                    if(Utils.getRestaurantArr(tinyDB)!=null){
                        adapter = new RestaurantAdapter(mContext, Utils.getRestaurantArr(tinyDB));
                        recyclerView.setAdapter(adapter);
                    }
                    if(Utils.getRestaurantArr(tinyDB).size()<=0){
                        tv_message.setVisibility(View.VISIBLE);
                        tv_message.setText(getString(R.string.no_data_found));
                    }
                }catch (Exception e){
                    reload.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(getString(R.string.error_while_fetching));
                    Utils.show_log("Exp = "+e.getMessage().toString());
                }
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // HTTP error happened, do something to handle it.
                try{
                    Utils.show_log("in exp = "+t.getMessage().toString());
                    reload.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    if(t.getMessage().toString().equalsIgnoreCase(getString(R.string.bad_request))){
                        tv_message.setText(getString(R.string.no_data_found_location));
                    }else if(t.getMessage().toString().contains(getString(R.string.failed_to_connect_yelp))||t.getMessage().toString().contains(getString(R.string.unable_to_connect_host))){
                        tv_message.setText(getString(R.string.failed_to_connect));
                    }else{
                        tv_message.setText(getString(R.string.error_in_loading_data));
                    }
                }catch (Exception e){

                }
            }
        };
        call.enqueue(callback);
    }

    //restaurant sort dialog
    public void showRestaurantsSortDialog() {
        final Dialog dialog = new Dialog(mContext,R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sort);
        dialog.setCancelable(true);

        FontHelper.applyFont(getActivity(), dialog.findViewById(R.id.container_sort_dialog), "bauhaus.ttf");

        LinearLayout ll_rating = (LinearLayout) dialog.findViewById(R.id.ll_rating);

        ll_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isNetworkConnected(mContext)){

                }else{
                    Utils.showToast(mContext,getString(R.string.no_internet_connection));
                }
                ArrayList<RestaurantsArraylist> arr=new ArrayList<RestaurantsArraylist>();
                adapter = new RestaurantAdapter(mContext, arr);
                recyclerView.setAdapter(adapter);
                getRestaurantDetailsFromYelp("2");
                dialog.dismiss();
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

    //restaurant filter dialog
    public void showRestaurantsFilterDialog() {
        FontHelper.applyFont(getActivity(), filterDialog.findViewById(R.id.container_filter_dialog), "bauhaus.ttf");

        RecyclerView filterRecyclerView = (RecyclerView) filterDialog.findViewById(R.id.recyclerView);

        LinearLayoutManager filterLayoutManager;
        RestaurantFilterAdapter filterAdapter;
        filterLayoutManager = new LinearLayoutManager(mContext);
        filterLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        filterRecyclerView.setLayoutManager(filterLayoutManager);

        filterAdapter = new RestaurantFilterAdapter(mContext, categoriesArray, valuesArray, images,RestaurantsFragment.this);
        filterRecyclerView.setAdapter(filterAdapter);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = filterDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        filterDialog.show();
    }

    //on filter recycler view click call api to get filter data
    public void onRecyclerClick(int position){
        if(Utils.isNetworkConnected(mContext)){
            tinyDB.putString(GeneralValues.RESTAURANT_FILTER, valuesArray.get(position));

            ArrayList<RestaurantsArraylist> arr=new ArrayList<RestaurantsArraylist>();
            adapter = new RestaurantAdapter(mContext, arr);
            recyclerView.setAdapter(adapter);
            getRestaurantDetailsFromYelp("");
            filterDialog.dismiss();
        }else{
            Utils.showToast(mContext,getString(R.string.no_internet_connection));
        }
    }
}
