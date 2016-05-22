package com.gill.travelmate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.gill.travelmate.utils.FontHelper;
import com.gill.travelmate.utils.GeneralValues;
import com.gill.travelmate.utils.TinyDB;


public class SplashActivity extends AppCompatActivity {

    ImageView logo;
    TextView title;
    Context mContext;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        FontHelper.applyFont(this, findViewById(R.id.container_splash), "bauhaus.ttf");

        mContext=SplashActivity.this;
        tinyDB=new TinyDB(mContext);

        logo=(ImageView)findViewById(R.id.logo);
        title=(TextView)findViewById(R.id.title);

        logo.setDrawingCacheEnabled(true);
        Animation anim_zoom = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        AnimationSet growShrink = new AnimationSet(true);
        growShrink.addAnimation(anim_zoom);
        logo.startAnimation(growShrink);

        final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        title.setDrawingCacheEnabled(true);

        anim_zoom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                title.setVisibility(View.VISIBLE);
                title.startAnimation(animationFadeIn);
                start_timer();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void start_timer(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tinyDB.getString(GeneralValues.USER_ID_KEY) == null || tinyDB.getString(GeneralValues.USER_ID_KEY).equalsIgnoreCase("")) {
                    startActivity(new Intent(getApplicationContext(), SigningActivity.class));
                }else{
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
                overridePendingTransition(R.anim.to_leftin, R.anim.to_leftout);
                SplashActivity.this.finish();
            }
        }, 1500);
    }

/*    public void call_api(){
        YelpAPIFactory apiFactory = new YelpAPIFactory("xVGAIFUbD2r9kjgHCUM7ww", "EVjGuFiMaHIEeu5Ln94frqgCnkQ", "_lg0YkcnO7DZQ6oju2jV-21p-WUFgmYo", "OdMtiyxepMY7V13beRcRfGYN6xQ");
        YelpAPI yelpAPI = apiFactory.createAPI();

        Map<String, String> params = new HashMap<>();
        params.put("term", "food");
        params.put("limit", "3");
        params.put("lang", "fr");

        Call<SearchResponse> call = yelpAPI.search("San Francisco", params);

        Utils.show_log("url = "+call.request().url());

        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                // Update UI text with the searchResponse.
                Utils.show_log("in result "+searchResponse);
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // HTTP error happened, do something to handle it.
                Utils.show_log("in exp = "+t.getMessage().toString());
            }
        };

        call.enqueue(callback);
    }*/
}
