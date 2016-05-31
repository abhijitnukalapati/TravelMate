package com.gill.travelmate.utils;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


//Main application class

public class MyAppliction extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        FontsOverride.setDefaultFont(this, "MONOSPACE", "bauhaus.ttf");
        //MultiDex.install(this);
        //ACRA.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
