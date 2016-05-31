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
import com.gill.travelmate.adapter.HotelsAdapter;
import com.gill.travelmate.arraylists.HotelsArraylist;
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

//Hotel main fragment
public class HotelsFragment extends Fragment implements View.OnClickListener{

    ProgressBar progress_bar;
    ImageView reload;
    TextView tv_message;
    Context mContext;
    TinyDB tinyDB;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    HotelsAdapter adapter;

    public HotelsFragment() {
        // Requires empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_hotels, container, false);
        FontHelper.applyFont(getActivity(), v.findViewById(R.id.container_hotel_fragment), "bauhaus.ttf");

        mContext=getActivity();
        tinyDB=new TinyDB(mContext);

        initialize_views(v);
        set_listener();

        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //call default api according to internet availablilty
        if(Utils.isNetworkConnected(mContext)){
            call_hotel_api("");
        }else{
            if(Utils.getHotelArr(tinyDB)==null||Utils.getHotelArr(tinyDB).size()<=0){
                progress_bar.setVisibility(View.GONE);
                reload.setVisibility(View.VISIBLE);
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(getString(R.string.no_internet_connection_try_again));
            }else{
                progress_bar.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);
                tv_message.setVisibility(View.GONE);
                adapter = new HotelsAdapter(mContext, Utils.getHotelArr(tinyDB));
                recyclerView.setAdapter(adapter);
            }
        }

        return v;
    }

    //initialize views
    public void initialize_views(View v){
        progress_bar=(ProgressBar)v.findViewById(R.id.progress_bar);
        reload=(ImageView)v.findViewById(R.id.reload);
        tv_message=(TextView)v.findViewById(R.id.tv_message);
        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);
    }

    //set listener on views
    public void set_listener(){
        reload.setOnClickListener(this);
    }

    //set functionality on click on views
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reload:
                if(Utils.isNetworkConnected(mContext)){
                    call_hotel_api("");
                }else{
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

    //call api to get hotel data from server
    public void call_hotel_api(String sort){
        reload.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        tv_message.setVisibility(View.VISIBLE);
        tv_message.setText(getString(R.string.loading));

        YelpAPIFactory apiFactory = new YelpAPIFactory(GeneralValues.YELP_CUSTOMER_KEY, GeneralValues.YELP_CUSTOMER_SECRET, GeneralValues.YELP_TOKEN, GeneralValues.YELP_TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();

        Map<String, String> params = new HashMap<>();
        params.put("term", "hotels");
        if(!sort.equalsIgnoreCase("")){
            params.put("sort", sort);
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
                progress_bar.setVisibility(View.GONE);
                tv_message.setVisibility(View.GONE);

                ArrayList<HotelsArraylist> arr=new ArrayList<>();

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
                        arr.add(new HotelsArraylist(searchResponse.businesses().get(i).id(),
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

                    //save data locally
                    Utils.setHotelArr(tinyDB,arr);

                    Utils.show_log("arr size = "+Utils.getHotelArr(tinyDB).size());

                    //set adapter
                    if(Utils.getHotelArr(tinyDB)!=null){
                        adapter = new HotelsAdapter(mContext, Utils.getHotelArr(tinyDB));
                        recyclerView.setAdapter(adapter);
                    }
                    if(Utils.getHotelArr(tinyDB).size()<=0){
                        tv_message.setVisibility(View.VISIBLE);
                        tv_message.setText(getString(R.string.no_data_found));
                    }
                }catch (Exception e){
                    reload.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);
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
                    progress_bar.setVisibility(View.GONE);
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

    //show hotel sort dialog
    public void show_sort_dialog() {
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
                    ArrayList<HotelsArraylist> arr=new ArrayList<HotelsArraylist>();
                    adapter = new HotelsAdapter(mContext, arr);
                    recyclerView.setAdapter(adapter);
                    call_hotel_api("2");
                    dialog.dismiss();
                }else{
                    Utils.showToast(mContext,getString(R.string.no_internet_connection));
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
}
