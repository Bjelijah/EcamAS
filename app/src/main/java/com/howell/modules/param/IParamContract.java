package com.howell.modules.param;

import com.howell.bean.CameraItemBean;
import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;

/**
 * Created by Administrator on 2017/9/27.
 */

public interface IParamContract {
    interface IVew extends ImpBaseView{

    }
    interface IPresenter extends ImpBasePresenter{
        IPresenter init(CameraItemBean bean);

    }
}
