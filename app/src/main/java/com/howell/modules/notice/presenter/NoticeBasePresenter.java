package com.howell.modules.notice.presenter;

import android.content.Context;

import com.howell.action.ConfigAction;
import com.howell.modules.BasePresenter;
import com.howell.modules.ImpBaseView;
import com.howell.modules.notice.INoticeContract;

/**
 * Created by Administrator on 2017/9/18.
 */

public abstract class NoticeBasePresenter extends BasePresenter implements INoticeContract.IPresenter {
    Context mContext;
    INoticeContract.IVew mView;
    String mURL;
    String mAccount;
    @Override
    public void bindView(ImpBaseView view) {
        mView = (INoticeContract.IVew) view;
    }

    @Override
    public void unbindView() {
        dispose();
        mView = null;
    }

    @Override
    public void init(Context context) {
        mContext = context;
        ConfigAction cf = ConfigAction.getInstance(context);
        mURL = cf.getURL();
        mAccount = cf.getName();
    }
}
