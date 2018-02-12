package com.howell.di;

import android.app.Application;

import com.howell.DaggerApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by Administrator on 2018/2/11.
 */
@Singleton
@Component(modules = {AppModule.class,ActivityBindModule.class,AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<DaggerApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        AppComponent.Builder application(Application application);
        AppComponent build();
    }
}
