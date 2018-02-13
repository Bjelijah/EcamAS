package com.howell.di.ui.activity;

import com.howell.activity.Activities;
import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/13.
 */
@Module
public class SendWifiModule {
    @Provides
    Activities provideActivities(){
        return Activities.getInstance();
    }

    @Provides
    HomeKeyEventBroadCastReceiver provideReceive(){
       return new HomeKeyEventBroadCastReceiver();
    }

}
