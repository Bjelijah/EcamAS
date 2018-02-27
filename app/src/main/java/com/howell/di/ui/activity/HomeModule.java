package com.howell.di.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.android.howell.webcam.R;
import com.howell.action.ConfigAction;
import com.howell.activity.AddNewCameraActivity;
import com.howell.activity.CenterActivity;
import com.howell.activity.DeviceShareActivity;
import com.howell.activity.LineChartActivity;
import com.howell.activity.LoginActivity;
import com.howell.activity.PushSettingActivity;
import com.howell.activity.ServerSetActivity;
import com.howell.activity.TestActivity;
import com.howell.activity.fragment.DeviceFragment;
import com.howell.activity.fragment.HomeBaseFragment;
import com.howell.activity.fragment.MediaFragment;
import com.howell.activity.fragment.NoticeFragment;
import com.howell.modules.login.ILoginContract;
import com.howell.modules.login.presenter.LoginHttpPresenter;
import com.howell.modules.login.presenter.LoginSoapPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/12.
 */
@Module
public class HomeModule {

    public static final String INTENT_LOGIN  = "loin_intent";
    public static final String INTENT_CENTER = "center_intent";
    public static final String INTENT_SERVER_SET = "server_set_intent";
    public static final String INTENT_PUSH_SET = "push_set_intent";
    public static final String INTENT_ADD_CAM = "add_cam_intent";
    public static final String INTENT_SHARE = "share_intent";
    public static final String INTENT_TEST = "test_intent";
    public static final String INTENT_CHART = "chart_intent";

    @Provides
    ILoginContract.IPresenter provideLoginPresenter(Context c){
        switch (ConfigAction.getInstance(c).getMode()){
            case 0:
                return new LoginSoapPresenter();
            case 1:
                return new LoginHttpPresenter();
            default:
                return new LoginSoapPresenter();
        }
    }

    @Provides
    int [] provideUserIcon(){
        return new int[]{ R.drawable.profile2,R.drawable.profile3,R.drawable.profile4,R.drawable.profile5,R.drawable.profile6 };
    }

    @Provides
    List<HomeBaseFragment> provideFragment(){
        List<HomeBaseFragment> list = new ArrayList<>();
        list.add(new DeviceFragment());
        list.add(new MediaFragment());
        list.add(new NoticeFragment());
        return list;
    }

    @Provides
    @Named(INTENT_LOGIN)
    Intent provideLoginIntent(Context c){
        return new Intent(c,LoginActivity.class);
    }

    @Provides
    @Named(INTENT_CENTER)
    Intent provideCenterIntent(Context c){
        return new Intent(c,CenterActivity.class);
    }

    @Provides
    @Named(INTENT_SERVER_SET)
    Intent provideServerSetIntent(Context c){
        return new Intent(c,ServerSetActivity.class);
    }

    @Provides
    @Named(INTENT_PUSH_SET)
    Intent providePushSetIntent(Context c){
        return new Intent(c,PushSettingActivity.class);
    }

    @Provides
    @Named(INTENT_ADD_CAM)
    Intent provideAddCamIntent(Context c){
        return new Intent(c,AddNewCameraActivity.class);
    }

    @Provides
    @Named(INTENT_SHARE)
    Intent provideShareIntent(Context c){
        return new  Intent(c,DeviceShareActivity.class);
    }

    @Provides
    @Named(INTENT_TEST)
    Intent provideTestIntent(Context c){
        return new Intent(c,TestActivity.class);
    }

    @Provides
    @Named(INTENT_CHART)
    Intent provideChartIntent(Context c){
        return new Intent(c,LineChartActivity.class);
    }

}
