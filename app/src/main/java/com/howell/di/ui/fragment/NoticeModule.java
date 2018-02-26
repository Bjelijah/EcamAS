package com.howell.di.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.android.howell.webcam.R;
import com.howell.action.ConfigAction;
import com.howell.activity.BigImagesActivity;
import com.howell.datetime.WheelMain;
import com.howell.di.FragmentScope;
import com.howell.modules.notice.INoticeContract;
import com.howell.modules.notice.presenter.NoticeHttpPresenter;
import com.howell.modules.notice.presenter.NoticeSoapPresenter;

import java.util.Calendar;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/2/26.
 */

@Module
public class NoticeModule {

    View mView = null;

    public View getView(Context c){
        if (mView == null){
            mView = LayoutInflater.from(c).inflate(R.layout.timepicker, null);
        }
        return mView;
    }


    @Provides
    INoticeContract.IPresenter provideNoticePresenter(Context c){
        switch (ConfigAction.getInstance(c).getMode()){
            case 0:
               return new NoticeSoapPresenter();
            case 1:
               return new NoticeHttpPresenter();
        }
        return null;
    }

    @Provides
    Intent provideIntent(Context c){
        return new Intent(c, BigImagesActivity.class);
    }

    @Provides
    @FragmentScope
    View provideTimepickerView(Context c){
        return getView(c);
    }

    @Provides
    @FragmentScope
    WheelMain provideWheelMain(Context c){
        return WheelMain.builder()
                .setView(getView(c))
                .setCountry(c.getResources().getConfiguration().locale.getCountry())
                .setDate(Calendar.getInstance().get(Calendar.YEAR)
                        ,Calendar.getInstance().get(Calendar.MONTH)
                        ,Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .build();
    }


}
