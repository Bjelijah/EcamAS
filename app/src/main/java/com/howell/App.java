package com.howell;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.howell.di.AppComponent;
//import com.howell.di.AppModule;
//import com.howell.di.DaggerAppComponent;
import com.howell.pushlibrary.DaemonEnv;
import com.howell.service.MyService;

/**
 * Created by Administrator on 2017/6/8.
 */

public class App extends MultiDexApplication {


    private static AppComponent appComponent = null;

    @Override
    public void onCreate() {
        super.onCreate();
        DaemonEnv.initialize(this, MyService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
    }


    public static App get(Context c){
        return (App) c.getApplicationContext();
    }

    public AppComponent getAppComponent(){
        if (appComponent==null){
//            appComponent = DaggerAppComponent.create();
        }
        return appComponent;
    }

}
