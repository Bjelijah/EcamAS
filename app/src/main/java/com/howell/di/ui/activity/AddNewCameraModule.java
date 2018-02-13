package com.howell.di.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.howell.activity.ApActivity;
import com.howell.activity.DeviceWifiActivity;
import com.howell.activity.SimpleCaptureActivity;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/12.
 */
@Module
public class AddNewCameraModule {
    public static final String INTENT_AP = "ap_intent";
    public static final String INTENT_QR = "qr_intent";
    public static final String INTENT_WIFI = "wif_intent";

    @Provides
    @Named(INTENT_AP)
    Intent provideAPIntent(Context c){
        return new Intent(c,ApActivity.class);
    }

    @Provides
    @Named(INTENT_QR)
    Intent provideQrIntent(Context c){
        return new Intent(c,SimpleCaptureActivity.class);
    }

    @Provides
    @Named(INTENT_WIFI)
    Intent provideWifiIntent(Context c){
        return new Intent(c, DeviceWifiActivity.class);
    }

}
