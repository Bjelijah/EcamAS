package com.howell.di.ui.activity;

import com.howell.modules.device.IDeviceContract;
import com.howell.modules.device.presenter.DeviceSoapPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/12.
 */
@Module
public class AddApCameraModule {
    @Provides
    IDeviceContract.IPresenter provideDevicePresenter(){
        return new DeviceSoapPresenter();
    }
}
