package com.howell.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/11.
 */
@Module
public abstract class AppModule {
    @Binds
    abstract Context bindContext(Application app);
}
