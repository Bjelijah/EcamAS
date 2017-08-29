package com.howell.modules.mcu.presenter;

import android.content.Context;


import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.mcu.IMcuContract;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Administrator on 2017/8/15.
 */

public class McuPresenter extends BasePresenter implements IMcuContract.IPresenter{
    private IMcuContract.IView mView;


    @Override
    public void bindView(ImpBaseView view) {
        mView = (IMcuContract.IView) view;
    }

    @Override
    public void unbindView() {
        dispose();
        mView = null;
    }
}
