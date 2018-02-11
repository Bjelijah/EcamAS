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

import com.howell.activity.PlayBackActivity;
import com.howell.activity.RecycleViewDivider;
import com.howell.adapter.VideoListRecyclerAdapter;
import com.howell.bean.CameraItemBean;
import com.android.howell.webcam.R;

import com.howell.modules.player.IPlayContract;
import com.howell.modules.player.bean.VODRecord;
import com.howell.modules.player.presenter.PlayApPresenter;
import com.howell.modules.player.presenter.PlayEcamPresenter;
import com.howell.modules.player.presenter.PlayTurnPresenter;
import com.howell.utils.AlerDialogUtils;
import com.howellsdk.utils.RxUtil;
import com.howellsdk.utils.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pullrefreshview.layout.BaseFooterView;
import pullrefreshview.layout.BaseHeaderView;
import pullrefreshview.support.view.LockFooterView;
import pullrefreshview.support.view.LockHeaderView;

/**
 * Created by Administrator on 2017/1/6.
 */

public class VodFragment extends Fragment implements IPlayContract.IVew,VideoListRecyclerAdapter.OnItemClick,LockHeaderView.OnRefreshListener,LockFooterView.OnLoadListener{

    public static final int MSG_VIDEO_LIST_DATA_UPDATE          = 0xfe00;
    public static final int MSG_VIDEO_LIST_DATA_UPDATE_ERROR    = 0xfe01;
    public static final int MSG_VIDEO_LIST_DATA_REFREASH        = 0xfe02;
    public static final int MSG_VIDEO_LIST_DATA_LAST            = 0xfe03;
    private static final boolean IS_SUB = true;

    RecyclerView mRv;
    VideoListRecyclerAdapter mAdapter;
    View mView;
    LockHeaderView mlhv;
    LockFooterView mlfv;
    ArrayList<VODRecord> mList = new ArrayList<>();
    CameraItemBean mBean;
    IPlayContract.IPresent mPresent;
    String mBeg,mEnd;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_VIDEO_LIST_DATA_UPDATE:
                    Log.i("123","MSG_VIDEO_LIST_DATA_UPDATE");
//                    mList.addAll(PlayBackVideoListAction.getInstance().getVodList());//fixme
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

    @Override
    public void onResume() {
        super.onResume();
        mPresent.resumeServer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("123","on VodFragment  destoryView");
        unbindPresenter();
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
//        PlayBackVideoListAction.getInstance().setHandler(mHandler);
//        if (PlayBackVideoListAction.getInstance().hasVod()) {
//            getData(null,null);
//        }else{
//            AlerDialogUtils.postDialogMsg(getContext(),
//                    getResources().getString(R.string.no_estore),
//                    getResources().getString(R.string.no_sdcard),null);
//        }
//         getData(null,null);
    }



    public void searchList(String startTime,String endTime){
        mList.clear();
//        PlayBackVideoListAction.getInstance().reset();
//        PlayBackVideoListAction.getInstance().setSearchTime(startTime,endTime);
//        if (PlayBackVideoListAction.getInstance().hasVod()) {
//            getData(startTime,endTime);
//        }else{
//            AlerDialogUtils.postDialogMsg(getContext(),
//                    getResources().getString(R.string.no_estore),
//                    getResources().getString(R.string.no_sdcard),null);
//        }
        mBeg = startTime;
        mEnd = endTime;
        mPresent.vodReset();
        if (mBean.isStore()) {
            mPresent.vodReset();
            mPresent.getVODRecord(IS_SUB, startTime, endTime);
        }else{
            AlerDialogUtils.postDialogMsg(getContext(),
                    getResources().getString(R.string.no_estore),
                    getResources().getString(R.string.no_sdcard),null);
        }


    }

    private void initNowTime(){
        Date endDate = new Date();
        Date begDate = new Date(1970 - 1900, 1 - 1, 1, 0, 0, 0);

        Date dateNow = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_MONTH,-1);
//        calendar.add(Calendar.DAY_OF_MONTH,-1);
        Date dateBefore = calendar.getTime();


        mBeg = Util.Date2String(begDate);
        mEnd = Util.Date2String(endDate);
    }



    public void setBean(CameraItemBean b){
        mBean = b;
        bindPresenter();
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
//        Log.i("123","on item click mpresent deinit");
//        mPresent.deInit();
        mPresent.holdServer();

        VODRecord record = mList.get(pos);
        String startTime = record.getStartTime();
        String endTime = record.getEndTime();
        Log.i("123","startTime="+startTime+"  zoneTime="+record.getTimeZoneStartTime());
//        PlayAction.getInstance().setPlayBean(mBean);

        Intent intent = new Intent(getContext(), PlayBackActivity.class);
        Log.e("123","mBean="+mBean.toString());
        intent.putExtra("CameraItem",mBean);
        intent.putExtra("startTime",startTime);
        intent.putExtra("endTime",endTime);

        getContext().startActivity(intent);

    }



    @Override
    public void onLoad(BaseFooterView baseFooterView) {
//        PlayBackVideoListAction.getInstance().loadVODList();
        mPresent.getVODRecord(IS_SUB,mBeg,mEnd);
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
//         PlayBackVideoListAction.getInstance().refreashVODList();


        initNowTime();

        mPresent.vodReset();
        mPresent.getVODRecord(IS_SUB,mBeg,mEnd);
        baseHeaderView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mlhv.stopRefresh();
            }
        },1000);

    }

    @Override
    public void bindPresenter() {
        if(mBean==null)return;
        if (mPresent==null){
            switch (mBean.getType()){
                case ECAM:
                    mPresent = new PlayEcamPresenter();
                    break;
                case HW5198:
                    mPresent = new PlayApPresenter();
                    break;
                case TURN:
                    mPresent = new PlayTurnPresenter();
                    break;
            }
        }
        Log.i("123","vod bindPresent  type="+mBean.getType());
        mPresent.bindView(this);
        mPresent.init(getContext(),mBean);

    }

    @Override
    public void unbindPresenter() {
        if (mPresent!=null){
            mPresent.clearServer();
            mPresent.unbindView();
            mPresent.deInit();
            mPresent = null;
        }
    }

    @Override
    public void onConnect(boolean isSuccess) {
        Log.i("123","onConnect ="+isSuccess);
        if (isSuccess){
            if (mBean.isStore()){
                initNowTime();
                mPresent.vodReset().getVODRecord(IS_SUB,mBeg,mEnd);
            }else{
                AlerDialogUtils.postDialogMsg(getContext(),
                        getResources().getString(R.string.no_estore),
                        getResources().getString(R.string.no_sdcard),null);
            }
        }else{
            Log.e("123","MSG_VIDEO_LIST_DATA_UPDATE_ERROR");
        }
    }

    @Override
    public void onSoundMute(boolean isMute) {

    }

    @Override
    public void onRecord(final List<com.howell.modules.player.bean.VODRecord> vodRecords) {
        RxUtil.doInUIThread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                mList.addAll(vodRecords);
                mAdapter.setData(mList);
                mlfv.stopLoad();
                mlhv.stopRefresh();
            }
        });

    }

    @Override
    public void onError(int flag) {

    }

    @Override
    public void onTime(int speed, long timestamp, long firstTimestamp, boolean bWait) {

    }

    @Override
    public void onPlaybackStartEndTime(long beg, long end) {
        Log.i("123","vod fragment on onPlaybackStartEndTime ");
    }
}
