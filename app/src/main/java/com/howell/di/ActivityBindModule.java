package com.howell.di;

import com.howell.activity.LogoActivity;
import com.howell.activity.RegisterActivity;
import com.howell.di.ui.activity.LogoModule;
import com.howell.di.ui.activity.RegistModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Administrator on 2018/2/12.
 */
@Module
public abstract class ActivityBindModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = LogoModule.class)
    abstract LogoActivity logoActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = RegistModule.class)
    abstract RegisterActivity registerActivity();


}
