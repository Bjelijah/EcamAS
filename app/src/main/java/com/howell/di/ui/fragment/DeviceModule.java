package com.howell.di.ui.fragment;

import android.content.Context;
import android.content.Intent;

import com.howell.action.ConfigAction;
import com.howell.activity.DeviceSettingActivity;
import com.howell.activity.PlayViewActivity;
import com.howell.activity.VideoListActivity;
import com.howell.modules.device.IDeviceContract;
import com.howell.modules.device.presenter.DeviceHttpPresenter;
import com.howell.modules.device.presenter.DeviceSoapPresenter;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/26.
 */
@Module
public class DeviceModule {

    public static final String INTENT_PLAY   = "play_intent";
    public static final String INTENT_DEVICE = "device_setting_intent";
    public static final String INTENT_LIST   = "video_list_intent";

    @Provides
    IDeviceContract.IPresenter provideDevicePresenter(Context c){

//        return new DeviceSoapPresenter();
        switch (ConfigAction.getInstance(c).getMode()){
            case 0:
                return new DeviceSoapPresenter();
            case 1:
               return new DeviceHttpPresenter();
        }
        return null;
    }

    @Provides
    @Named(INTENT_PLAY)
    Intent providePlayIntent(Context c){
        return new Intent(c,PlayViewActivity.class);
    }

    @Provides
    @Named(INTENT_DEVICE)
    Intent provideDeviceIntent(Context c){
        return new Intent(c,DeviceSettingActivity.class);
    }

    @Provides
    @Named(INTENT_LIST)
    Intent provideListIntent(Context c){
        return new Intent(c,VideoListActivity.class);
    }

}
