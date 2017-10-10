package com.howell.modules.device;

import android.content.Context;
import android.support.annotation.Nullable;

import com.howell.bean.CameraItemBean;
import com.howell.bean.PlayType;
import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;

import java.util.List;

/**
 * Created by Administrator on 2017/9/15.
 */

public interface IDeviceContract {
    interface IVew extends ImpBaseView{
        void onQueryResult(List<CameraItemBean> beanList);
        void onAddResult(boolean isSuccess, PlayType type);
        void onRemoveResult(boolean isSuccess,int pos);
        void onError();
        void onUpdateCamBean(@Nullable Boolean isTurn,@Nullable Boolean isCrypto);
    }

    interface IPresenter extends ImpBasePresenter{
        void init(Context context);
        void queryDevices();
        void addDevice(CameraItemBean bean);
        void removeDevice(CameraItemBean bean,int pos);
    }
}
