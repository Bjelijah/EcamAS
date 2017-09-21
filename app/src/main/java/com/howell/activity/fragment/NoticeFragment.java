package com.howell.activity.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.howell.activity.BigImagesActivity;
import com.howell.adapter.NoticeRecyclerViewAdapter;
import com.howell.bean.NoticeItemBean;
import com.howell.datetime.JudgeDate;
import com.howell.datetime.ScreenInfo;
import com.howell.datetime.WheelMain;
import com.android.howell.webcam.R;
import com.howell.modules.device.presenter.DeviceSoapPresenter;
import com.howell.modules.notice.INoticeContract;
import com.howell.modules.notice.presenter.NoticeSoapPresenter;
import com.howell.protocol.NoticeList;
import com.howell.protocol.QueryNoticesRes;
import com.howell.utils.SDCardUtils;
import com.howell.utils.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pullrefreshview.layout.BaseFooterView;
import pullrefreshview.layout.BaseHeaderView;

/**
 * Created by howell on 2016/11/25.
 */

public class NoticeFragment extends HomeBaseFragment implements INoticeContract.IVew,BaseHeaderView.OnRefreshListener,BaseFooterView.OnLoadListener,NoticeRecyclerViewAdapter.OnItemClickListener {
    private final static int MSG_NOTICE_ERROR = 0x20;
    private final static int MSG_NOTICE_UPDATA = 0x21;
    private final static int MSG_NOTICE_END    = 0x22;
    View mView;
    BaseHeaderView mHv;
    BaseFooterView mFv;
    List<NoticeItemBean>mlist = new ArrayList<NoticeItemBean>();
    NoticeRecyclerViewAdapter mAdapter;
    RecyclerView mRv;

    INoticeContract.IPresenter mPresenter;

    Boolean mIsRead = null;
    String mTime = null;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_NOTICE_ERROR:
//                    Snackbar.make(mView,"error",Snackbar.LENGTH_LONG).show();
                    break;
                case MSG_NOTICE_UPDATA:
                    Log.i("123","updata notice");
                    mFv.stopLoad();
                    mHv.stopRefresh();
                    Log.i("123","mlist size="+mlist.size());
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
        bindPresenter();
        mView = inflater.inflate(R.layout.fragment_notice,container,false);
        mHv = (BaseHeaderView) mView.findViewById(R.id.notice_header);
        mFv = (BaseFooterView) mView.findViewById(R.id.notice_footer);
        mHv.setOnRefreshListener(this);
        mFv.setOnLoadListener(this);
        mRv = (RecyclerView) mView.findViewById(R.id.notice_rv);
        mAdapter = new NoticeRecyclerViewAdapter(getContext(),this);
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.setAdapter(mAdapter);

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
//        mNoticeAction.getNoticesTask();
        mPresenter.queryNotice(null,mIsRead,mTime,null);
    }

    @Override
    public void onRefresh(BaseHeaderView baseHeaderView) {

        mHv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHv.stopRefresh();
            }
        },1000);
//        mNoticeAction.reset().getNoticesTask();
        mTime = null;
        mIsRead = null;
        getData();
    }

    @Override
    public void onItemClickListener(int pos) {

    }

    @Override
    public void onPicClickListener(int pos, int index,ArrayList<String>picPath,String str) {
       if (picPath==null||picPath.size()==0)return;
        Log.i("123","on pic click pos="+pos+"   str="+str);

        Log.i("123","pic path="+picPath.get(index)+"   id="+mlist.get(pos).getPicID());//FIXME picPath no use

        ArrayList<String> _picPath = new ArrayList<>();
        for (int i=0;i<mlist.get(pos).getPicID().size();i++){
            _picPath.add(SDCardUtils.getBitmapCachePath() + mlist.get(pos).getPicID().get(i) + "HD");
            Log.i("123","_picPath="+_picPath.get(i));
        }

        Intent intent = new Intent(getContext(), BigImagesActivity.class);
        intent.putExtra("position", index);
        intent.putStringArrayListExtra("arrayList", _picPath);

        getActivity().overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

        startActivity(intent);

//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(),view,"myImage").toBundle());
    }

    @Override
    public void onNoticeReadClickListener(int pos) {

        NoticeItemBean b = mlist.get(pos);
        String id =b.getId();
        boolean isRead = b.isHasRead();
        b.setHasRead(!isRead);
        isRead = !isRead;
        Log.i("123","pos="+pos+"   set read ="+isRead  +"  time="+b.getTime());
//        mNoticeAction.setNoticesStatus(id,isRead);

        mPresenter.setNoticeStatus(id,isRead);

    }



    public void doSearchByTime(){
        mView.post(new Runnable() {
            @Override
            public void run() {
                wheelTimeFun();
            }
        });

    }

    public void doSearchByState(final int status){

        switch (status){
            case 0:
                mIsRead = false;
                break;
            case 1:
                mIsRead = true;
                break;
            case 2:
                mIsRead = null;
                break;
            default:
                mIsRead = null;
                break;
        }


        mView.post(new Runnable() {
            @Override
            public void run() {
//                Log.i("123","do search by state action="+mNoticeAction);
                mlist.clear();
                Log.i("123","mlist size="+mlist.size());
//                if (mNoticeAction==null){
//                    mNoticeAction = NoticeAction.getInstance();
//                }
//                mNoticeAction.searchNotices(status);

                mPresenter.reset();
                mPresenter.queryNotice(null,mIsRead,mTime,null);

            }
        });

    }

    @Override
    public void getData(){
        mlist.clear();
//        mNoticeAction.reset();
//        mNoticeAction.getNoticesTask();
        mPresenter.reset().queryNotice(null,mIsRead,mTime,null);
    }







    private void wheelTimeFun(){
        final View timepickerview= LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(getActivity());
        String country = getResources().getConfiguration().locale.getCountry();
        final WheelMain wheelMain = new WheelMain(timepickerview,country);
        wheelMain.screenheight = screenInfo.getHeight();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String time = (calendar.get(Calendar.YEAR) + "-" +
                (calendar.get(Calendar.MONTH) + 1 )+ "-" +
                calendar.get(Calendar.DAY_OF_MONTH) + "");
        if(JudgeDate.isDate(time, "yyyy-MM-dd")){
            try {
                calendar.setTime(dateFormat.parse(time));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int  month = calendar.get(Calendar.MONTH) ;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        wheelMain.initDateTimePicker(year,month,day);

        new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.select_date))
                .setView(timepickerview)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//						now.setText(wheelMain.getTime());
                        //"yyyy-MM-dd'T'HH:mm:ss"
                        Date date = wheelMain.getTime();
                        mTime = Util.Date2ISODate(date);
                        Log.i("123","time ="+mTime);
                        mlist.clear();
//                        mNoticeAction.searchNotices(mTime);
                        mPresenter.reset().queryNotice(null,mIsRead,mTime,null);

                    }
                })
                .setNeutralButton(getString(R.string.all), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mlist.clear();
                        mTime = null;
//                        mNoticeAction.searchNotices(null);
                        mPresenter.reset().queryNotice(null,mIsRead,mTime,null);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .show();
    }

    @Override
    public void bindPresenter() {
        if (mPresenter==null){
            mPresenter = new NoticeSoapPresenter();
        }
        mPresenter.bindView(this);
        mPresenter.init(getContext());
    }

    @Override
    public void unbindPresenter() {
        if (mPresenter!=null) {
            mPresenter.unbindView();
        }
        mPresenter = null;
    }

    @Override
    public void onQueryResult(List<NoticeItemBean> lists) {
        Log.i("123","on query result lists="+lists+"    mlist="+mlist);
        if (lists!=null) {
            mlist.addAll(lists);
        }
        mHandler.sendEmptyMessage(MSG_NOTICE_UPDATA);
    }

    @Override
    public void onError(int flag) {
        if (flag==0){
            mHandler.sendEmptyMessage(MSG_NOTICE_END);
        }else {
            mHandler.sendEmptyMessage(MSG_NOTICE_ERROR);
        }
    }

    @Override
    public void onStatusError() {

    }

    @Override
    public void onPicture(NoticeRecyclerViewAdapter.ViewHoder hoder, Bitmap bit, String path, int index) {

    }


}
