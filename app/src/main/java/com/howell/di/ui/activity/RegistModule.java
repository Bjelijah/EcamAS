package com.howell.di.ui.activity;

import com.howell.modules.regist.IRegistContract;
import com.howell.modules.regist.presenter.RegistPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/12.
 */
@Module
public class RegistModule {
    @Provides
    IRegistContract.IPresenter provideRegistPresenter(){
        return new RegistPresenter();
    }
}
