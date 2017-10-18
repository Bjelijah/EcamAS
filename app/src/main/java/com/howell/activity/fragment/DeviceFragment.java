package com.howell.activity.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.howell.action.ApAction;
import com.howell.action.ConfigAction;
import com.howell.action.HomeAction;
import com.howell.action.LoginAction;
import com.howell.action.PlayAction;
import com.howell.activity.DeviceSettingActivity;
import com.howell.activity.PlayViewActivity;
import com.howell.activity.VideoListActivity;
import com.howell.adapter.DeviceRecyclerViewAdapter;
import com.howell.bean.APDeviceDBBean;
import com.howell.bean.CameraItemBean;
import com.howell.bean.PlayType;
import com.android.howell.webcam.R;
import com.howell.entityclass.NodeDetails;
import com.howell.modules.device.IDeviceContract;
import com.howell.modules.device.presenter.DeviceHttpPresenter;
import com.howell.modules.device.presenter.DeviceSoapPresenter;
import com.howell.protocol.GetNATServerReq;
import com.howell.protocol.GetNATServerRes;
import com.howell.protocol.SoapManager;
import com.howell.rxbus.RxBus;
import com.howell.utils.AlerDialogUtils;
import com.howell.utils.IConst;
import com.zys.brokenview.BrokenCallback;
import com.zys.brokenview.BrokenTouchListener;
import com.zys.brokenview.BrokenView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pullrefreshview.layout.BaseFooterView;
import pullrefreshview.layout.BaseHeaderView;
import pullrefreshview.layout.PullRefreshLayout;

/**
 * Created by howell on 2016/11/11.
 */

public class DeviceFragment extends HomeBaseFragment implements IDeviceContract.IVew,BaseHeaderView.OnRefreshListener,BaseFooterView.OnLoadListener, DeviceRecyclerViewAdapter.OnItemClickListener,HomeAction.QueryDeviceCallback,IConst,ApAction.QueryApDevice {
    public static final int MSG_RECEIVE_SIP = 0x0000;
    public static final int MSG_DEVICE_LIST_UPDATA = 0x0001;
    public static final int MSG_NET_SERVER_OK = 0x0002;



    View mView;
    RecyclerView mRV;
    BaseHeaderView mbhv;
//    BaseFooterView mbfv;

    List<CameraItemBean> mList = new ArrayList<CameraItemBean>();
    int page = 1;
    DeviceRecyclerViewAdapter adapter;

    BrokenView mBrokenView;
    MyBrokenCallback mBrokenCallback = new MyBrokenCallback();
    private BrokenTouchListener mColorfulListener;
    private IDeviceContract.IPresenter mPresenter;

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
//                    mBrokenView.reset();
                    break;
                case MSG_NET_SERVER_OK:
                    doPlay(msg.arg1);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onDestroyView() {
        mHandler = null;
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bindPresenter();
        mView = inflater.inflate(R.layout.fragment_device,container,false);
        ((PullRefreshLayout)mView).setHasFooter(true);
        mRV = (RecyclerView) mView.findViewById(R.id.device_rv);
        mbhv = (BaseHeaderView) mView.findViewById(R.id.device_header);
//        mbfv = (BaseFooterView) mView.findViewById(R.id.device_footer);
        mbhv.setOnRefreshListener(this);
//        mbfv.setOnLoadListener(this);

        mBrokenView = BrokenView.add2Window(getActivity());
        mColorfulListener = new BrokenTouchListener
                .Builder(mBrokenView)
                .setComplexity(5)
                .setBreakDuration(1500)
                .setFallDuration(2000)
                .setCircleRiftsRadius(33)
                .build();
        mBrokenView.setCallback(mBrokenCallback);
        mBrokenView.setEnable(true);

        
//        adapter = new DeviceRecyclerViewAdapter(getContext(),this,getActivity());
        adapter = new DeviceRecyclerViewAdapter(getContext(),this,mColorfulListener);
//        adapter = new DeviceRecyclerViewAdapter(getContext(),this);
        mRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mRV.setAdapter(adapter);
        mRV.setItemAnimator(new DefaultItemAnimator());
        Log.i("123","on create get data");
        getData();
//        getData();
        return mView;
    }





    @Override
    public void getData(){
  
        if (mList==null)return;
        mList.clear();
        mPresenter.queryDevices();
//        LoginAction.UserInfo info = LoginAction.getInstance().getmInfo();
        //get ap list
//        HomeAction.getInstance().addApCam2List(getContext(),info.getAccount(),mList);
//        HomeAction.getInstance().addApCam2List(getContext(),info.getAccount(),this);
//        HomeAction.getInstance().setContext(getContext()).registQueryDeviceCallback(this).queryDevice(info.getAccount(),info.getLr().getLoginSession());
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
            Log.i("123","n="+n.toString());
            CameraItemBean b = new CameraItemBean()
                    .setType(HomeAction.getInstance().isUseTurn()?PlayType.TURN:PlayType.ECAM)//FIXME ME  should be ecam when test ,is 5198
                    .setCameraName(n.getName())
                    .setCameraDescription(null)
                    .setIndensity(n.getIntensity())
                    .setDeviceId(n.getDevID())
                    .setOnline(n.isOnLine())
                    .setPtz(n.isPtzFlag())
                    .setStore(n.iseStoreFlag())
                    .setUpnpIP(n.getUpnpIP())
                    .setUpnpPort(n.getUpnpPort())
                    .setMethodType(n.getMethodType())
                    .setPicturePath(n.getPicturePath());
//            Log.e("123","~~~~~~~~~~~~~~~~~~~~~~~~~~~n.getMethod type="+n.getMethodType()+"  name="+n.getName()+"  upnpIP="+n.getUpnpIP());
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
    public void onItemVideoTouchListener(View v, View itemView, int pos) {

    }

    @Override
    public void onItemVideoClickListener(View v, View itemView, int pos) {
        //long click || click
        Log.i("123","on item vied long click || click");
      //  itemView.setOnTouchListener(mColorfulListener);
        //TODO get net server res
        CameraItemBean bean = mList.get(pos);
        Log.i("123","bean type="+bean.getType());
        if (!bean.isOnline()){
            AlerDialogUtils.postDialogMsg(this.getContext(),
                    getResources().getString(R.string.not_online),
                    getResources().getString(R.string.not_online_message),null);
            return;
        }
        getContext().startActivity(new Intent(getContext(),PlayViewActivity.class).putExtra("CameraItem",bean));
    }


    @Override
    public void onItemReplayClickListener(View v, int pos) {
        Log.i("123","onItemClickListener   pos="+pos);
        CameraItemBean bean = mList.get(pos);
//        if (!bean.isStore()){
//            AlerDialogUtils.postDialogMsg(getContext(),
//                    getResources().getString(R.string.no_estore),
//                    getResources().getString(R.string.no_sdcard),null);
//            return;
//        }
        this.getContext().startActivity(new Intent(this.getContext(), VideoListActivity.class).putExtra("bean",bean));
    }

    @Override
    public void onItemSettingClickListener(View v, int pos) {
        //TODO: camera setting
        CameraItemBean bean = mList.get(pos);
        startActivity(new Intent(getContext(),DeviceSettingActivity.class).putExtra("bean",bean));
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
//                if(HomeAction.getInstance().removeCam(getContext(),bean)){
//                    mList.remove(pos);
//                    mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);
//                }else{
//                    Snackbar.make(mView,getString(R.string.device_item_remove_fail),Snackbar.LENGTH_LONG).show();
//                }
                mPresenter.removeDevice(bean,pos);
            }
        }, null);
    }

    @Override
    public void onQueryApDevice(List<APDeviceDBBean> list) {
        if (mList==null)return;

        for (APDeviceDBBean apBean:list){
            CameraItemBean camBean = new CameraItemBean()
                    .setType(PlayType.HW5198)
                    .setCameraName(apBean.getDeviceName())
                    .setCameraDescription("AP:"+apBean.getDeviceIP())
                    .setOnline(apBean.isOnLine())
                    .setIndensity(0)
                    .setStore(true)
                    .setPtz(true)
                    .setUpnpIP(apBean.getDeviceIP())
                    .setUpnpPort(apBean.getDevicePort())
                    .setDeviceId(apBean.getDeviceIP())
                    .setPicturePath("/sdcard/eCamera/cache/"+apBean.getDeviceIP()+".jpg");
            mList.add(camBean);
        }
        HomeAction.getInstance().sort((ArrayList<CameraItemBean>) mList);//online 倒序添加
        mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);//updata ecam list and ap list
    }

    @Override
    public void bindPresenter() {
        if (mPresenter==null){
            switch (ConfigAction.getInstance(getContext()).getMode()){
                case 0:
                    mPresenter = new DeviceSoapPresenter();
                    break;
                case 1:
                    mPresenter = new DeviceHttpPresenter();
                    break;
            }

        }
        mPresenter.bindView(this);
        mPresenter.init(getContext());
    }

    @Override
    public void unbindPresenter() {
        if (mPresenter!=null) {
            mPresenter.unbindView();
            mPresenter = null;
        }
    }

    @Override
    public void onQueryResult(List<CameraItemBean> beanList) {
        mList = beanList;
        mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);//updata ecam list and ap list
    }

    @Override
    public void onAddResult(boolean isSuccess, PlayType type) {
        Log.i("123","!!!!!!!!DeviceFragment onAddresult");
    }

    @Override
    public void onRemoveResult(boolean isSuccess, int pos) {
        if (isSuccess){
            mList.remove(pos);
            mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);
        }else{
            Snackbar.make(mView,getString(R.string.device_item_remove_fail),Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onError() {
        Log.e("123","Device Fragment on error");
        //// TODO: 2017/9/18
        
    }

    @Override
    public void onUpdateCamBean(@Nullable Boolean isTurn, @Nullable Boolean isCrypto) {
        //TODO
        Log.e("123","on update cam bean isturn="+isTurn+" iscrypto="+isCrypto);
        synchronized (this) {
            for (CameraItemBean b:mList){
                if (b.getType()!=PlayType.HW5198){
                    if (isTurn!=null){
                        b.setType(isTurn ? PlayType.TURN : PlayType.ECAM);
                    }
                    if (isCrypto!=null){
                        b.setMethodType(isCrypto ? 1 : 0);
                    }

                }else{//ap   isTurn ->  h265
                    if (isTurn!=null && isCrypto!=null){
                        b.setMethodType(isTurn?(isCrypto? 3:2):(isCrypto?1:0));
                    }
                }
            }
        }

    }

    class MyBrokenCallback extends BrokenCallback {
        @Override
        public void onStart(View v) {
            super.onStart(v);
            Log.e("123","BrokenCallback onStart  v="+v.getId());
        }

        @Override
        public void onCancel(View v) {
            super.onCancel(v);
            Log.e("123","BrokenCallback onCancel");
        }

        @Override
        public void onRestart(View v) {
            super.onRestart(v);
        }

        @Override
        public void onFalling(View v) {
            super.onFalling(v);
            Log.e("123","BrokenCallback onFalling  v="+v.getId());
            //开始删除
            int pos = (int) v.getTag();
            Log.i("123","pos="+pos);
//            final CameraItemBean bean = mList.get(pos);
//            if(HomeAction.getInstance().removeCam(getContext(),bean)){
//              //  mList.remove(pos);
//            }else{
//                Snackbar.make(mView,getString(R.string.device_item_remove_fail),Snackbar.LENGTH_LONG).show();
//            }
//            Snackbar.make(mView,getString(R.string.device_item_remove_fail),Snackbar.LENGTH_LONG).show();
        }

        @Override
        public void onFallingEnd(View v) {
            Log.e("123","BrokenCallback onFallingEnd");
            //更新
            int pos = (int) v.getTag();
            final CameraItemBean bean = mList.get(pos);
         //   mPresenter.removeDevice(bean,pos);
//            mList.remove(pos);
//            adapter.removeSllData(pos);
//            adapter.setData(mList);

//            mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);


//            super.onFallingEnd(v);

        }

        @Override
        public void onCancelEnd(View v) {
            super.onCancelEnd(v);
            Log.e("123","BrokenCallback onCancelEnd");
        }
    }


    private void doPlay(int pos){
        CameraItemBean bean = getUpdataBean(pos);
        Log.i("123","do play Type="+bean.getType());
        PlayAction.getInstance().setPlayBean(bean);
        Intent intent = new Intent(getContext(), PlayViewActivity.class);
        getContext().startActivity(intent);
    }





    private CameraItemBean getUpdataBean(int pos){
        CameraItemBean b = mList.get(pos);
        if (b.getType()==PlayType.ECAM || b.getType() == PlayType.TURN){
            b.setType(HomeAction.getInstance().isUseTurn()?PlayType.TURN:PlayType.ECAM);
            mList.set(pos,b);
        }
        return b;
    }




    public void updataAllBeanType(){
        Log.i("123","updata All bean type");
        for (CameraItemBean b:mList){
            if (b.getType()==PlayType.ECAM||b.getType()==PlayType.TURN){
                b.setType(HomeAction.getInstance().isUseTurn()?PlayType.TURN:PlayType.ECAM);
            }
        }
    }


}
