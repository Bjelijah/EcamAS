package com.howell.modules.pdc;


import android.content.Context;

import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/11/23.
 */

public interface IPDCContract {
    interface IVew extends ImpBaseView{

    }

    interface IPresent extends ImpBasePresenter{
        void init(Context context);
        void test() throws UnsupportedEncodingException, NoSuchAlgorithmException;
    }
}
