package com.howell.di.ui.fragment;

import android.content.Context;
import android.content.Intent;

import com.howell.activity.PlayBackActivity;
import com.howell.modules.player.IPlayContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/26.
 */
@Module
public class VodModule {
    @Provides
    Intent provideIntent(Context c){
        return new Intent(c, PlayBackActivity.class);
    }

}
