package com.howell.modules.notice;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.howell.adapter.NoticeRecyclerViewAdapter;
import com.howell.bean.NoticeItemBean;
import com.howell.modules.ImpBasePresenter;
import com.howell.modules.ImpBaseView;

import java.util.List;

/**
 * Created by Administrator on 2017/9/18.
 */

public interface INoticeContract {
    interface IVew extends ImpBaseView{
        void onQueryResult(List<NoticeItemBean> lists,boolean needResetList);
        void onError(int flag);
        void onStatusError();
        void onPicture(NoticeRecyclerViewAdapter.ViewHoder hoder, Bitmap bit, String path,int index);
    }
    interface IPresenter extends ImpBasePresenter{
        void init(Context context);
        IPresenter reset();
        void queryNotice(@Nullable String searchID,@Nullable Boolean isRead,@Nullable String time,@Nullable String sender,boolean needResetList);
        void setNoticeStatus(String id, boolean isRead);
        void getPicture(NoticeRecyclerViewAdapter.ViewHoder hoder,List<String> picIDs,int width,int height);

    }
}
