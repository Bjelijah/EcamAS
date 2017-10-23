package com.howell;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.howell.pushlibrary.DaemonEnv;
import com.howell.service.MyService;

/**
 * Created by Administrator on 2017/6/8.
 */

public class App extends MultiDexApplication {



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaemonEnv.initialize(this, MyService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);

    }
}
