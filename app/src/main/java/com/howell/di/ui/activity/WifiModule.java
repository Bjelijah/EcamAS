package com.howell.di.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.howell.activity.FlashLighting;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/12.
 */
@Module
public class WifiModule {
    @Provides
    Intent provideIntent(Context c ){
        return new Intent(c,FlashLighting.class);
    }
}
