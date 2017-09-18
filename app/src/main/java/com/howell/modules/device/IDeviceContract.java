package com.howell.modules.device;

import android.content.Context;

import com.howell.bean.CameraItemBean;
import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;

import java.util.List;

/**
 * Created by Administrator on 2017/9/15.
 */

public interface IDeviceContract {
    interface IVew extends ImpBaseView{
        void queryResult(List<CameraItemBean> beanList);
    }

    interface IPresenter extends ImpBasePresenter{
        void init(Context context);
        void queryDevices(String account);
    }
}
