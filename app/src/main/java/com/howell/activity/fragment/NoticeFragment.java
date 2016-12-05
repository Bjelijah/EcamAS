package com.howell.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.howell.action.NoticeAction;
import com.howell.activity.BigImages;
import com.howell.adapter.NoticeRecyclerViewAdapter;
import com.howell.bean.NoticeItemBean;
import com.howell.ecam.R;
import com.howell.protocol.NoticeList;
import com.howell.protocol.QueryNoticesRes;

import java.util.ArrayList;
import java.util.List;

import pullrefreshview.layout.BaseFooterView;
import pullrefreshview.layout.BaseHeaderView;

/**
 * Created by howell on 2016/11/25.
 */

public class NoticeFragment extends HomeBaseFragment implements BaseHeaderView.OnRefreshListener,BaseFooterView.OnLoadListener,NoticeRecyclerViewAdapter.OnItemClickListener,NoticeAction.OnNoticeRes {
    private final static int MSG_NOTICE_ERROR = 0x20;
    private final static int MSG_NOTICE_UPDATA = 0x21;
    private final static int MSG_NOTICE_END    = 0x22;
    View mView;
    BaseHeaderView mHv;
    BaseFooterView mFv;
    List<NoticeItemBean>mlist = new ArrayList<NoticeItemBean>();
    NoticeRecyclerViewAdapter mAdapter;
    RecyclerView mRv;
    NoticeAction mNoticeAction;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_NOTICE_ERROR:
                    break;
                case MSG_NOTICE_UPDATA:
                    Log.i("123","updata notice");
                    mFv.stopLoad();
                    mHv.stopRefresh();
                    mAdapter.setData(mlist);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_NOTICE_END:
                    Snackbar.make(mView,getString(R.string.notice_no_more),Snackbar.LENGTH_LONG).show();
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
        mNoticeAction = NoticeAction.getInstance().setListener(this).init();
        getData();
        return mView;
    }





    @Override
    public void onLoad(BaseFooterView baseFooterView) {
        mFv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFv.stopLoad();
            }
        },1000);
        mNoticeAction.getNoticesTask();
    }

    @Override
    public void onRefresh(BaseHeaderView baseHeaderView) {
        mlist.clear();
        mHv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHv.stopRefresh();
            }
        },1000);
        mNoticeAction.reset().getNoticesTask();
    }

    @Override
    public void onItemClickListener(int pos) {

    }

    @Override
    public void onPicClickListener(int pos, int index,ArrayList<String>picPath) {
       if (picPath==null||picPath.size()==0)return;
        Intent intent = new Intent(getContext(), BigImages.class);
        intent.putExtra("position", index);
        intent.putStringArrayListExtra("arrayList", picPath);
        getActivity().overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

        //startActivity(intent);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(),view,"myImage").toBundle());
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

    @Override
    public void getData(){
        mlist.clear();
        mNoticeAction.reset();
        mNoticeAction.getNoticesTask();
    }



    @Override
    public void OnNoticeError() {
        Log.e("123","on notice error");
        mHandler.sendEmptyMessage(MSG_NOTICE_ERROR);
    }

    @Override
    public void OnNoticeRes(QueryNoticesRes res) {
        Log.e("123","on notice res");
        if (res == null){
            mHandler.sendEmptyMessage(MSG_NOTICE_END);
        }
        List<NoticeList>list = res.getNodeList();
        if (list==null){
            mHandler.sendEmptyMessage(MSG_NOTICE_UPDATA);
            return;
        }
        for (NoticeList o:list){
            NoticeItemBean bean = new NoticeItemBean();
            bean.setTitle(o.getName())
                    .setDescription(o.getMessage()).setTime(o.getTime().substring(0, 10)+" "+o.getTime().substring(11,19))
                    .setPicID(o.getPictureID());
            mlist.add(bean);



//            holder.title.setText(notice.getName());
//            holder.message.setText(notice.getMessage());
//            holder.time.setText(notice.getTime().substring(0, 10)+" "+notice.getTime().substring(11,19));

        }
        mHandler.sendEmptyMessage(MSG_NOTICE_UPDATA);


    }
}
