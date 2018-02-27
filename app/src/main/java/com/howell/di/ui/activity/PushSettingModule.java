package com.howell.di.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.howell.service.MyService;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/27.
 */
@Module
public class PushSettingModule {

    @Provides
    Intent provideServerPushIntent(Context c){
        return new Intent(c, MyService.class);
    }


}
