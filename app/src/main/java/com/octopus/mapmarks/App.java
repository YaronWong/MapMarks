package com.octopus.mapmarks;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        initLog();

    }

    private void initLog() {

        Logger.addLogAdapter(new AndroidLogAdapter());

    }


    public static Context getGlobalContext() {
        return context;
    }
}