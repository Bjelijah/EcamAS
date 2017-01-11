package com.howell.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.howell.action.PlayAction;
import com.howell.action.PlayBackVideoListAction;
import com.howell.activity.PlayBackActivity;
import com.howell.activity.RecycleViewDivider;
import com.howell.adapter.VideoListRecyclerAdapter;
import com.howell.bean.CameraItemBean;
import com.howell.ecam.R;
import com.howell.entityclass.VODRecord;

import java.util.ArrayList;

import pullrefreshview.layout.BaseFooterView;
import pullrefreshview.layout.BaseHeaderView;
import pullrefreshview.support.view.LockFooterView;
import pullrefreshview.support.view.LockHeaderView;

/**
 * Created by Administrator on 2017/1/6.
 */

public class VodFragment extends Fragment implements VideoListRecyclerAdapter.OnItemClick,LockHeaderView.OnRefreshListener,LockFooterView.OnLoadListener{

    public static final int MSG_VIDEO_LIST_DATA_UPDATE          = 0xff00;
    public static final int MSG_VIDEO_LIST_DATA_UPDATE_ERROR    = 0xff01;
    public static final int MSG_VIDEO_LIST_DATA_REFREASH        = 0xff02;
    public static final int MSG_VIDEO_LIST_DATA_LAST            = 0xff03;


    RecyclerView mRv;
    VideoListRecyclerAdapter mAdapter;
    View mView;
    LockHeaderView mlhv;
    LockFooterView mlfv;
    ArrayList<VODRecord> mList = new ArrayList<>();
    CameraItemBean mBean;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_VIDEO_LIST_DATA_UPDATE:
                    Log.i("123","MSG_VIDEO_LIST_DATA_UPDATE");
                    mList.addAll(PlayBackVideoListAction.getInstance().getVodList());
                    mAdapter.setData(mList);
                    mlfv.stopLoad();
                    break;
                case MSG_VIDEO_LIST_DATA_UPDATE_ERROR:
                    Log.e("123","MSG_VIDEO_LIST_DATA_UPDATE_ERROR");
                    break;
                case MSG_VIDEO_LIST_DATA_REFREASH:
                    mAdapter.setData(mList);
                    mlhv.stopRefresh();
                    break;
                case MSG_VIDEO_LIST_DATA_LAST:
                    try {//maybe fragment not attach to activity
                        Snackbar.make(mRv,getResources().getString(R.string.vod_last_page),Snackbar.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
                default:
                    break;
            }
        }
    };



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vod_list,container,false);

        initView();
        initFun();
        return mView;
    }


    private void initView(){
        mRv = (RecyclerView) mView.findViewById(R.id.fragment_vod_rv);
        mlhv = (LockHeaderView) mView.findViewById(R.id.fragment_vod_header);
        mlhv.setOnRefreshListener(this);
        mlfv = (LockFooterView) mView.findViewById(R.id.fragment_vod_footer);
        mlfv.setOnLoadListener(this);
    }

    private void initFun(){
        mAdapter = new VideoListRecyclerAdapter(this);
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(new RecycleViewDivider(getContext(),LinearLayoutManager.HORIZONTAL,2,getResources().getColor(R.color.black)));
        PlayBackVideoListAction.getInstance().setHandler(mHandler);
        getData();
    }

    private void getData(){
        PlayBackVideoListAction.getInstance().searchVODList();
    }

    public void searchList(String startTime,String endTime){
        mList.clear();
        PlayBackVideoListAction.getInstance().reset();
        PlayBackVideoListAction.getInstance().setSearchTime(startTime,endTime);
        getData();
    }

    public void setBean(CameraItemBean b){
        mBean = b;
    }


    private void getData(int n){
        for (int i=0;i<n;i++){
            VODRecord record = new VODRecord();
            record.setStartTime("2017-01-04 13:57:00  00:00:00");
            record.setEndTime("2017-01-04 13:57:00   11:11:11");
            record.setTimeZoneStartTime("2017-01-04 13:57:00  00:00:00  ");
            record.setTimeZoneEndTime("2017-01-04 13:57:00   11:11:11");
            if (mList==null){
                mList = new ArrayList<>();
            }
            mList.add(record);
        }
        mHandler.sendEmptyMessage(MSG_VIDEO_LIST_DATA_UPDATE);
    }


    @Override
    public void onItemClickListener(View v, int pos) {
        VODRecord record = mList.get(pos);
        String startTime = record.getStartTime();
        String endTime = record.getEndTime();
        Log.i("123","startTime="+startTime+"  zoneTime="+record.getTimeZoneStartTime());
        PlayAction.getInstance().setPlayBean(mBean);

        Intent intent = new Intent(getContext(), PlayBackActivity.class);
        intent.putExtra("startTime",startTime);
        intent.putExtra("endTime",endTime);

        getContext().startActivity(intent);
    }

    @Override
    public void onLoad(BaseFooterView baseFooterView) {
        PlayBackVideoListAction.getInstance().loadVODList();
        baseFooterView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mlfv.stopLoad();
            }
        },1000);
    }

    @Override
    public void onRefresh(BaseHeaderView baseHeaderView) {
         mList.clear();
         PlayBackVideoListAction.getInstance().refreashVODList();
         baseHeaderView.postDelayed(new Runnable() {
             @Override
             public void run() {
                 mlhv.stopRefresh();
             }
         },1000);
    }
}
