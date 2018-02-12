package com.howell;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.howell.di.DaggerAppComponent;
import com.howell.pushlibrary.DaemonEnv;
import com.howell.service.MyService;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

/**
 * Created by Administrator on 2018/2/12.
 */

public class DaggerApp extends DaggerApplication {

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

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
