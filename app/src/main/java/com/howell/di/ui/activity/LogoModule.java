package com.howell.di.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.activity.HomeExActivity;
import com.howell.activity.LoginActivity;
import com.howell.activity.LogoActivity;
import com.howell.activity.NavigationActivity;
import com.howell.modules.login.ILoginContract;
import com.howell.modules.login.presenter.LoginHttpPresenter;
import com.howell.modules.login.presenter.LoginSoapPresenter;
import com.howell.service.MyService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/12.
 */
@Module
public class LogoModule {

    public static final String INTENT_NAVIGATION = "Navigation_intent";
    public static final String INTENT_LOGIN = "login_intent";
    public static final String INTENT_HOME = "home_intent";
    public static final String INTENT_SERVER = "server_intent";


    @Provides
    ILoginContract.IPresenter providePresenter(Context c){
        Log.e("123","providePresenter");
        switch (ConfigAction.getInstance(c).getMode()){
            case 0:
                return new LoginSoapPresenter();//// FIXME: 2017/9/14 add  http
            case 1:
                return new LoginHttpPresenter();//// FIXME: 2017/10/17 add  http
            default:
                return new LoginSoapPresenter();//// FIXME: 2017/9/14 add  http
        }
    }

    @Provides
    @Named(INTENT_NAVIGATION)
    Intent provideNavigationIntent(Context c){
        return new Intent(c,NavigationActivity.class);
    }

    @Provides
    @Named(INTENT_LOGIN)
    Intent provideLoginIntent(Context c){
        return new Intent(c,LoginActivity.class);
    }

    @Provides
    @Named(INTENT_HOME)
    Intent provideHomeIntent(Context c){
        return new Intent(c, HomeExActivity.class);
    }

    @Provides
    @Named(INTENT_SERVER)
    Intent provideServerIntent(Context c){return new Intent(c, MyService.class);}

}
