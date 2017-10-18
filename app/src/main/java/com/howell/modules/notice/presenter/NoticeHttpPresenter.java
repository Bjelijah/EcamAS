package com.howell.modules.notice.presenter;

import android.support.annotation.Nullable;

import com.howell.adapter.NoticeRecyclerViewAdapter;
import com.howell.modules.notice.INoticeContract;

import java.util.List;

/**
 * Created by Administrator on 2017/10/17.
 */

public class NoticeHttpPresenter extends NoticeBasePresenter {



    @Override
    public INoticeContract.IPresenter reset() {
        return this;
    }

    @Override
    public void queryNotice(@Nullable String searchID, @Nullable Boolean isRead, @Nullable String time, @Nullable String sender) {

    }

    @Override
    public void setNoticeStatus(String id, boolean isRead) {

    }

    @Override
    public void getPicture(NoticeRecyclerViewAdapter.ViewHoder hoder, List<String> picIDs, int width, int height) {

    }
}
