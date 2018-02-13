package com.howell.di;

import com.howell.activity.AddNewCameraActivity;
import com.howell.activity.AddNormalCameraActivity;
import com.howell.activity.ApActivity;
import com.howell.activity.DeviceSettingActivity;
import com.howell.activity.DeviceWifiActivity;
import com.howell.activity.FlashLighting;
import com.howell.activity.GetMatchResult;
import com.howell.activity.HomeExActivity;
import com.howell.activity.LogoActivity;
import com.howell.activity.RegisterActivity;
import com.howell.activity.SendWifi;
import com.howell.activity.VideoListActivity;
import com.howell.di.ui.activity.AddApCameraModule;
import com.howell.di.ui.activity.AddNewCameraModule;
import com.howell.di.ui.activity.AddNormalCameraModule;
import com.howell.di.ui.activity.DeviceParamModule;
import com.howell.di.ui.activity.FlashLightModule;
import com.howell.di.ui.activity.HomeModule;
import com.howell.di.ui.activity.LogoModule;
import com.howell.di.ui.activity.MatchResultModule;
import com.howell.di.ui.activity.RegistModule;
import com.howell.di.ui.activity.SendWifiModule;
import com.howell.di.ui.activity.VideoListModule;
import com.howell.di.ui.activity.WifiModule;

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

    @ActivityScope
    @ContributesAndroidInjector(modules = HomeModule.class)
    abstract HomeExActivity homeExActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = AddNewCameraModule.class)
    abstract AddNewCameraActivity addNewCameraActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = AddNormalCameraModule.class)
    abstract AddNormalCameraActivity addNormalCameraActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = AddApCameraModule.class)
    abstract ApActivity apActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = DeviceParamModule.class)
    abstract DeviceSettingActivity deviceSettingActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = WifiModule.class)
    abstract DeviceWifiActivity deviceWifiActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = FlashLightModule.class)
    abstract FlashLighting flashLightingActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = MatchResultModule.class)
    abstract GetMatchResult matchResultActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = SendWifiModule.class)
    abstract SendWifi sendWifiActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = VideoListModule.class)
    abstract VideoListActivity videoListActivity();
}
