package com.howell.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.howell.adapter.NoticeRecyclerViewAdapter;
import com.howell.bean.NoticeItemBean;
import com.howell.ecam.R;

import java.util.ArrayList;
import java.util.List;

import pullrefreshview.layout.BaseFooterView;
import pullrefreshview.layout.BaseHeaderView;

/**
 * Created by howell on 2016/11/25.
 */

public class NoticeFragment extends Fragment implements BaseHeaderView.OnRefreshListener,BaseFooterView.OnLoadListener,NoticeRecyclerViewAdapter.OnItemClickListener {

    private final static int MSG_NOTICE_UPDATA = 0x20;
    View mView;
    BaseHeaderView mHv;
    BaseFooterView mFv;
    List<NoticeItemBean>mlist = new ArrayList<NoticeItemBean>();
    NoticeRecyclerViewAdapter mAdapter;
    RecyclerView mRv;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_NOTICE_UPDATA:
                    Log.i("123","updata notice");
                    mAdapter.setData(mlist);
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_notice,container,false);
        mHv = (BaseHeaderView) mView.findViewById(R.id.notice_header);
        mFv = (BaseFooterView) mView.findViewById(R.id.notice_footer);
        mHv.setOnRefreshListener(this);
        mFv.setOnLoadListener(this);
        mRv = (RecyclerView) mView.findViewById(R.id.notice_rv);
        mAdapter = new NoticeRecyclerViewAdapter(getContext(),this);
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.setAdapter(mAdapter);

        getData(5);
        return mView;
    }





    @Override
    public void onLoad(BaseFooterView baseFooterView) {
        mFv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFv.stopLoad();
            }
        },1500);
    }

    @Override
    public void onRefresh(BaseHeaderView baseHeaderView) {
        mHv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHv.stopRefresh();
            }
        },1500);
    }

    @Override
    public void onItemClickListener(int pos) {

    }

    private void getData(int num){
        mlist.clear();
        for (int i=0;i<num;i++){
            NoticeItemBean b = new NoticeItemBean("test title :"+i);
            b.setDescription("test description");
            b.setTime("time ");
            mlist.add(b);
        }
        mHandler.sendEmptyMessage(MSG_NOTICE_UPDATA);
    }

}
