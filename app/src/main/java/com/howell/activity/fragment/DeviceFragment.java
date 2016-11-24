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

import com.howell.action.HomeAction;
import com.howell.action.LoginAction;
import com.howell.adapter.DeviceRecyclerViewAdapter;
import com.howell.bean.CameraItemBean;
import com.howell.ecam.R;
import com.howell.entityclass.NodeDetails;

import java.util.ArrayList;
import java.util.List;

import pullrefreshview.layout.BaseFooterView;
import pullrefreshview.layout.BaseHeaderView;
import pullrefreshview.layout.PullRefreshLayout;

/**
 * Created by howell on 2016/11/11.
 */

public class DeviceFragment extends Fragment implements BaseHeaderView.OnRefreshListener,BaseFooterView.OnLoadListener, DeviceRecyclerViewAdapter.OnItemClickListener,HomeAction.QueryDeviceCallback {
    public static final int MSG_RECEIVE_SIP = 0x0000;
    public static final int MSG_DEVICE_LIST_UPDATA = 0x0001;
    View mView;
    RecyclerView mRV;
    BaseHeaderView mbhv;
//    BaseFooterView mbfv;

    List<CameraItemBean> mList = new ArrayList<CameraItemBean>();
    int page = 1;
    DeviceRecyclerViewAdapter adapter;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_RECEIVE_SIP:
                    break;
                case MSG_DEVICE_LIST_UPDATA:
                    adapter.setData(mList);
                    break;
                default:
                    break;
            }
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_device,container,false);
        ((PullRefreshLayout)mView).setHasFooter(true);
        mRV = (RecyclerView) mView.findViewById(R.id.device_rv);
        mbhv = (BaseHeaderView) mView.findViewById(R.id.device_header);
//        mbfv = (BaseFooterView) mView.findViewById(R.id.device_footer);
        mbhv.setOnRefreshListener(this);
//        mbfv.setOnLoadListener(this);



        adapter = new DeviceRecyclerViewAdapter(getContext(),this);
        mRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mRV.setAdapter(adapter);
        getData(5);
        return mView;
    }


    private void getData(int n) {
        //// FIXME: 2016/11/18 for test
        List<String> datas = new ArrayList<>(n);
        mList.clear();

        for (int i=0;i<n;i++){
            CameraItemBean b = new CameraItemBean()
                    .setCameraName("test   "+i)
                    .setIndensity(100)
                    .setOnline(true)
                    .setPtz(true)
                    .setStore(true)
                    .setPicturePath(null);

            mList.add(b);
        }
        mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);
    }

    private void getData(){
        LoginAction.UserInfo info = LoginAction.getInstance().getmInfo();
        HomeAction.getInstance().setContext(getContext()).registQueryDeviceCallback(this).queryDevice(info.getAccount(),info.getLr().getLoginSession());
    }


    @Override
    public void onLoad(BaseFooterView baseFooterView) {
        Log.i("123","onLoad");
        baseFooterView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mbfv.stopLoad();
            }
        },3000);
    }

    @Override
    public void onRefresh(BaseHeaderView baseHeaderView) {
        Log.i("123","onRefresh");
        baseHeaderView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mbhv.stopRefresh();
            }
        },3000);
    }




    @Override
    public void onQueryDeviceSuccess(ArrayList<NodeDetails> l) {
        mList.clear();
        for (NodeDetails n:l){
            CameraItemBean b = new CameraItemBean()
                    .setCameraName(n.getName())
                    .setIndensity(n.getIntensity())
                    .setOnline(n.isOnLine())
                    .setPtz(n.isPtzFlag())
                    .setStore(n.iseStoreFlag())
                    .setPicturePath(n.getPicturePath());
            mList.add(b);
        }
        mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);
    }

    @Override
    public void onQueryDeviceError() {

    }

    @Override
    public void onItemVideoClickListener(View v, int pos) {

    }

    @Override
    public void onItemReplayClickListener(View v, int pos) {
        Log.i("123","onItemClickListener   pos="+pos);
    }

    @Override
    public void onItemSettingClickListener(View v, int pos) {

    }

    @Override
    public void onItemInfoClickListener(View v, int pos) {

    }

    @Override
    public void onItemDeleteClickListener(View v, int pos) {

    }
}
