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

//First screen when app starts
public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView title;
    private Context mContext;
    private TinyDB tinyDB;

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

        //animation on logo
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

        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.gill.travelmate",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/
    }

    //stay 1500 milliseconds here before goto next screen
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
}
