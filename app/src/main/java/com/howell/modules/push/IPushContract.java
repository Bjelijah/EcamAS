package com.howell.modules.push;

import android.content.Context;

import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;

/**
 * Created by Administrator on 2017/9/29.
 */

public interface IPushContract {
    interface IVew extends ImpBaseView{

    }
    interface IPresenter extends ImpBasePresenter{
        IPresenter init(Context c,String url, String imei);
        void connect();
        void disconnect();

    }
}
