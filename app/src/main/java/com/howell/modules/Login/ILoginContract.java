package com.howell.modules.Login;

import android.content.Context;
import android.support.annotation.Nullable;

import com.howell.bean.Custom;
import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.Login.bean.Type;


/**
 * Created by Administrator on 2017/8/15.
 */

public interface ILoginContract {
    interface IView extends ImpBaseView {
        void onError();
        void onLoginResult(Type type);
        void onLogoutResult(Type type);
    }
    interface IPresenter extends ImpBasePresenter {
        void init(Context context);
        void login(@Nullable String name, @Nullable String pwd,@Nullable Custom custom);
        void logout();
    }
}
