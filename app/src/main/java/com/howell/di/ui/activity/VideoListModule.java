package com.howell.di.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.android.howell.webcam.R;
import com.howell.activity.VideoListActivity;
import com.howell.activity.fragment.VodFragment;
import com.howell.datetime.JudgeDate;
import com.howell.datetime.ScreenInfo;
import com.howell.datetime.WheelMain;
import com.howell.di.ActivityScope;
import com.howell.di.FragmentScope;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Administrator on 2018/2/13.
 */
@Module
public abstract class VideoListModule {

    static View mView=null;

    public static View getmView(Context c) {
        if (mView == null){
            mView = LayoutInflater.from(c).inflate(R.layout.timepicker, null);
        }
        return mView;
    }

    @Provides
    @ActivityScope
    static WheelMain provideWheelMain(Context c){
        return WheelMain.builder()
                .setView(getmView(c))
                .setCountry(c.getResources().getConfiguration().locale.getCountry())
                .setDate(Calendar.getInstance().get(Calendar.YEAR)
                        ,Calendar.getInstance().get(Calendar.MONTH)
                        ,Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .build();
    }
    @Provides
    @ActivityScope
    static View provideTimePackerView(Context c){
        return getmView(c);
    }

//    @FragmentScope
//    @ContributesAndroidInjector
//    abstract VodFragment addVodFragment();

}
