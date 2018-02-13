package com.howell.di.ui.activity;

import android.support.transition.Transition;

import com.howell.activity.Activities;
import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.howell.modules.device.IDeviceContract;
import com.howell.modules.device.presenter.DeviceSoapPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/13.
 */
@Module
public class MatchResultModule {
    @Provides
    IDeviceContract.IPresenter providePresenter(){
        return new DeviceSoapPresenter();
    }

    @Provides
    HomeKeyEventBroadCastReceiver provideReceive(){
        return new HomeKeyEventBroadCastReceiver();
    }

    @Provides
    Activities provideActivity(){
        return Activities.getInstance();
    }
}
