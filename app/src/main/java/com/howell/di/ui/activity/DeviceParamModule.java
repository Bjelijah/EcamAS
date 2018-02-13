package com.howell.di.ui.activity;

import com.howell.modules.param.IParamContract;
import com.howell.modules.param.presenter.ParamSoapPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/12.
 */
@Module
public class DeviceParamModule {
    @Provides
    IParamContract.IPresenter provideParamPresenter(){
        return  new ParamSoapPresenter();
    }
}
