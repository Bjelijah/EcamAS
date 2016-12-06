package com.howell.activity.fragment;

import android.content.DialogInterface;
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

import com.howell.action.HomeAction;
import com.howell.action.LoginAction;
import com.howell.adapter.DeviceRecyclerViewAdapter;
import com.howell.bean.CameraItemBean;
import com.howell.bean.PlayType;
import com.howell.ecam.R;
import com.howell.entityclass.NodeDetails;
import com.howell.utils.AlerDialogUtils;

import java.util.ArrayList;
import java.util.List;

import pullrefreshview.layout.BaseFooterView;
import pullrefreshview.layout.BaseHeaderView;
import pullrefreshview.layout.PullRefreshLayout;

/**
 * Created by howell on 2016/11/11.
 */

public class DeviceFragment extends HomeBaseFragment implements BaseHeaderView.OnRefreshListener,BaseFooterView.OnLoadListener, DeviceRecyclerViewAdapter.OnItemClickListener,HomeAction.QueryDeviceCallback {
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
                    mbhv.stopRefresh();
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
        Log.i("123","on create get data");
        getData();
//        getData();
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

    @Override
    public void getData(){
        LoginAction.UserInfo info = LoginAction.getInstance().getmInfo();
        //get ap list
        HomeAction.getInstance().addApCam2List(getContext(),info.getAccount(),mList);

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
        getData();
        baseHeaderView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mbhv.stopRefresh();
            }
        },1500);
    }




    @Override
    public void onQueryDeviceSuccess(ArrayList<NodeDetails> l) {
//        mList.clear();
        if (l==null){
            mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);
            return;
        }

        for (NodeDetails n:l){
            CameraItemBean b = new CameraItemBean()
                    .setType(HomeAction.getInstance().isUseTurn()?PlayType.TURN:PlayType.HW5198)
                    .setCameraName(n.getName())
                    .setIndensity(n.getIntensity())
                    .setDeviceId(n.getDevID())
                    .setOnline(n.isOnLine())
                    .setPtz(n.isPtzFlag())
                    .setStore(n.iseStoreFlag())
                    .setUpnpIP(n.getUpnpIP())
                    .setUpnpPort(n.getUpnpPort())
                    .setPicturePath(n.getPicturePath());

            mList.add(b);
        }
        //TODO 重新排列： 1ecam OnLine 2ap 3ecam Offline
        HomeAction.getInstance().sort((ArrayList<CameraItemBean>) mList);//online 倒序添加
        mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);//updata ecam list and ap list
    }

    @Override
    public void onQueryDeviceError() {
        mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);//for updata ap list
    }

    @Override
    public void onItemVideoClickListener(View v, int pos) {
        CameraItemBean item = mList.get(pos);
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
    public void onItemDeleteClickListener(View v,final int pos) {
        //todo 关闭滑块  弹框
        adapter.closeAllSwipe();
        final CameraItemBean bean = mList.get(pos);
//        MyRemoveCam removeCam = new MyRemoveCam(getContext(),bean,pos);
        AlerDialogUtils.postDialogMsg(getContext(), getString(R.string.device_item_remove_title), getString(R.string.device_item_remove_msg), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(HomeAction.getInstance().removeCam(getContext(),bean)){
                    mList.remove(pos);
                    mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);
                }else{
                    Snackbar.make(mView,getString(R.string.device_item_remove_fail),Snackbar.LENGTH_LONG).show();
                }
            }
        }, null);
    }

}
