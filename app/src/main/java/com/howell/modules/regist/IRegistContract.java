package com.howell.modules.regist;

import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.regist.bean.Type;

/**
 * Created by Administrator on 2017/9/15.
 */

public interface IRegistContract {
    interface IVew extends ImpBaseView{
        void onError();
        void onRegistResult(Type type);
    }
    interface IPresenter extends ImpBasePresenter{
        void register(String name,String password,String email);
    }

}
