package com.howell.di;

import com.howell.activity.fragment.DeviceFragment;
import com.howell.activity.fragment.HomeBaseFragment;
import com.howell.activity.fragment.MediaFragment;
import com.howell.activity.fragment.NoticeFragment;
import com.howell.activity.fragment.VodFragment;
import com.howell.di.ui.fragment.DeviceModule;
import com.howell.di.ui.fragment.NoticeModule;
import com.howell.di.ui.fragment.VodModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Administrator on 2018/2/26.
 */
@Module
public abstract class FragmentBindModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = DeviceModule.class)
    abstract DeviceFragment homeFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract MediaFragment mediaFragment();

    @FragmentScope
    @ContributesAndroidInjector(modules = NoticeModule.class)
    abstract NoticeFragment noticeFragment();

    @FragmentScope
    @ContributesAndroidInjector(modules = VodModule.class)
    abstract VodFragment vodFragment();


}
