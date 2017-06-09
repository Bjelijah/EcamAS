package com.howell;

import android.app.Application;

import com.howell.pushlibrary.DaemonEnv;
import com.howell.service.MyService;

/**
 * Created by Administrator on 2017/6/8.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DaemonEnv.initialize(this, MyService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);

    }
}
