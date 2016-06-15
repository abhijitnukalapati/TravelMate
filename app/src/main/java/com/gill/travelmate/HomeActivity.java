package com.gill.travelmate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gill.travelmate.fragments.MainFragment;
import com.gill.travelmate.fragments.MySavesFragment;
import com.gill.travelmate.fragments.WeatherFragment;
import com.gill.travelmate.utils.FontHelper;
import com.gill.travelmate.utils.GeneralValues;
import com.gill.travelmate.utils.TinyDB;
import com.gill.travelmate.utils.Utils;

//main activity after login
public class    HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Context mContext;
    public static TinyDB tinyDB;
    public static Intent i;
    public static Toolbar toolbar;
    public static TextView title;
    public static ImageView sortIcon, filterIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FontHelper.applyFont(this, findViewById(R.id.container_home), "bauhaus.ttf");

        mContext=HomeActivity.this;
        tinyDB=new TinyDB(mContext);

        //drawer setup
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //navigation view setup
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        //Navigation view header setup
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_home);
        TextView nav_name=(TextView)headerLayout.findViewById(R.id.nav_name);
        TextView nav_email=(TextView)headerLayout.findViewById(R.id.nav_email);
        Utils.typeface_font(mContext,nav_name);
        Utils.typeface_font(mContext,nav_email);
        nav_name.setText(tinyDB.getString(GeneralValues.USER_NAME_KEY).toUpperCase());
        nav_email.setText(tinyDB.getString(GeneralValues.USER_EMAIL_KEY));

        title=(TextView)toolbar.findViewById(R.id.title);
        filterIcon =(ImageView)toolbar.findViewById(R.id.filter_icon);
        sortIcon =(ImageView)toolbar.findViewById(R.id.sort_icon);

        initialSetting();
    }

    //set initial settings and set initial fragment
    public void initialSetting(){
        Utils.show_log("Id = "+tinyDB.getString(GeneralValues.USER_ID_KEY)+" name = "+
                tinyDB.getString(GeneralValues.USER_NAME_KEY)+
                " des = "+tinyDB.getString(GeneralValues.USER_DESTINATION_KEY));

        Utils.showToast(mContext,"Welcome "+tinyDB.getString(GeneralValues.USER_NAME_KEY).toUpperCase());

        title.setText(getString(R.string.home));
        sortIcon.setVisibility(View.VISIBLE);
        filterIcon.setVisibility(View.GONE);

        tinyDB.putString(GeneralValues.RESTAURANT_FILTER,"");
        tinyDB.putString(GeneralValues.PLACES_FILTER,"");

        tinyDB.putBoolean(GeneralValues.HOTEL_FRAGMENT,true);
        tinyDB.putBoolean(GeneralValues.RESTAURANT_FRAGMENT,false);
        tinyDB.putBoolean(GeneralValues.PLACES_FRAGMENT,false);

        android.support.v4.app.FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, new MainFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        toolbar.getMenu().findItem(R.id.action_conversion).setVisible(false);
        return true;
    }

    //set functionality on navigation item click
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int id = item.getItemId();
                if (id == R.id.nav_home){
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_container);
                    if (!(fragment instanceof MainFragment)){
                        Utils.goToFragment(mContext,new MainFragment(),R.id.home_container);
                        title.setText(getString(R.string.home));
                        sortIcon.setVisibility(View.VISIBLE);
                        filterIcon.setVisibility(View.GONE);
                        tinyDB.putString(GeneralValues.RESTAURANT_FILTER,"");
                        tinyDB.putString(GeneralValues.PLACES_FILTER,"");
                        tinyDB.putBoolean(GeneralValues.HOTEL_FRAGMENT,true);
                        tinyDB.putBoolean(GeneralValues.RESTAURANT_FRAGMENT,false);
                        tinyDB.putBoolean(GeneralValues.PLACES_FRAGMENT,false);
                    }
                    toolbar.getMenu().findItem(R.id.action_conversion).setVisible(false);
                } else if (id == R.id.nav_location) {
                    i=new Intent(mContext,SelectDestinationActivity.class);
                    i.putExtra("uid",""+tinyDB.getString(GeneralValues.USER_ID_KEY));
                    i.putExtra("uname",""+tinyDB.getString(GeneralValues.USER_NAME_KEY));
                    i.putExtra("email",""+tinyDB.getString(GeneralValues.USER_EMAIL_KEY));
                    startActivity(i);
                    overridePendingTransition(R.anim.to_leftin, R.anim.to_leftout);
                    toolbar.getMenu().findItem(R.id.action_conversion).setVisible(false);
                } else if (id == R.id.nav_weather) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_container);
                    if (!(fragment instanceof WeatherFragment)){
                        Utils.goToFragment(mContext,new WeatherFragment(),R.id.home_container);
                        title.setText(getString(R.string.weather));
                    }
                    sortIcon.setVisibility(View.GONE);
                    filterIcon.setVisibility(View.GONE);
                    toolbar.getMenu().findItem(R.id.action_conversion).setVisible(true);
                } else if (id == R.id.nav_saved) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_container);
                    if (!(fragment instanceof MySavesFragment)){
                        Utils.goToFragment(mContext,new MySavesFragment(),R.id.home_container);
                        title.setText(getString(R.string.my_saves));
                    }
                    sortIcon.setVisibility(View.GONE);
                    filterIcon.setVisibility(View.GONE);
                    toolbar.getMenu().findItem(R.id.action_conversion).setVisible(false);
                } else if (id == R.id.nav_logout) {
                    Utils.showDialog(mContext, getString(R.string.want_to_logout), "", getString(R.string.logout), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.showToast(mContext,getString(R.string.logout_success));

                            tinyDB.putString(GeneralValues.USER_ID_KEY,"");
                            tinyDB.putString(GeneralValues.USER_NAME_KEY,"");
                            tinyDB.putString(GeneralValues.USER_EMAIL_KEY,"");
                            tinyDB.putString(GeneralValues.USER_DESTINATION_KEY,"");
                            tinyDB.putDouble(GeneralValues.USER_LAT_KEY,0);
                            tinyDB.putDouble(GeneralValues.USER_LONG_KEY,0);

                            i=new Intent(mContext,SplashActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            HomeActivity.this.finish();
                            overridePendingTransition(R.anim.to_leftin, R.anim.to_leftout);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },getString(R.string.cancel),"",null);
                }
            }
        }, 300);
        return true;
    }
}
