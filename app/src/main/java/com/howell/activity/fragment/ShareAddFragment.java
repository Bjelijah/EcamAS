package com.howell.activity.fragment;

import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;


import com.android.howell.webcam.R;
import com.howell.activity.RecycleViewDivider;
import com.howell.adapter.ShareItem2RecyclerViewAdapter;
import com.howell.bean.ShareItem2Bean;
import com.howell.db.ShareDao;
import com.howell.entityclass.NodeDetails;

import com.howell.utils.ThreadUtil;
import com.howell.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/7/14.
 */

public class ShareAddFragment extends ShareBaseFragment {
    private static final int MSG_SHARE_ADD_SHARE_OK = 0x20;
    private static final int MSG_SHARE_ADD_SHARE_ERROR = 0x21;
    private static final int MSG_SHARE_ADD_HISTORY_UPDATE = 0x22;
    Spinner mSp;
    EditText mEt;
//    ImageButton mIb;
    Button mBtn;
    View mView;
    ShareDao mDao;
    RelativeLayout mShareDevView;
    RecyclerView mRv;
    ShareItem2RecyclerViewAdapter mAdapter;
    List<ShareItem2Bean> mList;
    Bundle mMyChoose = new Bundle();
    boolean mNeedChooseDev;
    ArrayList<NodeDetails> mL;
    String [] mSpinnerName;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SHARE_ADD_SHARE_OK:
                    Snackbar.make(mView,"分享成功",Snackbar.LENGTH_LONG).show();
                    queryShare();
                    break;
                case MSG_SHARE_ADD_SHARE_ERROR:
                    Snackbar.make(mView,"分享失败",Snackbar.LENGTH_LONG).show();
                    break;
                case MSG_SHARE_ADD_HISTORY_UPDATE:
                    Log.i("123","MSG_SHARE_ADD_HISTORY_UPDATE");
                    mAdapter.setData(mList);
                    break;
                default:
                    break;
            }

        }
    };

    public ShareAddFragment(@Nullable String devID, String devName,int channelNo) {
        super(devID, devName,channelNo);
        mNeedChooseDev = (devID==null);
    }

    @Override
    public void fun(int flag) {
        //del history
        mDao.delAllAddShare(mNeedChooseDev?null:mDevID);
        mList.clear();
        mHandler.sendEmptyMessage(MSG_SHARE_ADD_HISTORY_UPDATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_share2,container,false);
        mShareDevView = (RelativeLayout) mView.findViewById(R.id.share_item_share_dev_view);
        mSp = (Spinner) mView.findViewById(R.id.share_item2_sp);
        mEt = (EditText) mView.findViewById(R.id.share_item2_et);
        mBtn = (Button) mView.findViewById(R.id.share_item2_btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFun();
            }
        });
        mRv = (RecyclerView) mView.findViewById(R.id.share_item2_lv);
        mAdapter = new ShareItem2RecyclerViewAdapter(getContext());
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL));
        initFun();
        return mView;
    }


    @Override
    public void onDestroyView() {
        mDao.close();
        super.onDestroyView();
    }

    private void queryShare(){
        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
            public void run() {
                mList = mDao.queryAllAddShare(mNeedChooseDev?null:mDevID);
                Log.e("123","mList="+mList.toString());
                mHandler.sendEmptyMessage(MSG_SHARE_ADD_HISTORY_UPDATE);
            }
        });
    }

    private void initFun(){
        mDao = new ShareDao(getContext());
        queryShare();
        if (mNeedChooseDev){
            //init spinner
            mView.findViewById(R.id.share_item_share_dev_view).setVisibility(View.VISIBLE);
//            mL = SoapManager.getInstance().getNodeDetails();
            mSpinnerName = new String[mL.size()];
            for (int i=0;i<mL.size();i++){
                mSpinnerName[i] = mL.get(i).getName();
            }
            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,mSpinnerName);
            myAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mSp.setAdapter(myAdapter);
            mSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("123","pos="+position);
                    mMyChoose.putString("name",mL.get(position).getName());
                    mMyChoose.putString("id",mL.get(position).getDevID());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.e("123","onNothingSelected");
                }
            });
        }else{
            mView.findViewById(R.id.share_item_share_dev_view).setVisibility(View.GONE);
        }
    }



    private void shareFun(){
        final String shareAccount = mEt.getText().toString();
        if (mNeedChooseDev){
            mDevID=mMyChoose.getString("id",null);
            mDevName=mMyChoose.getString("name",null);
        }
        if (mDevID==null||mDevName==null||shareAccount==null||shareAccount.equals("")||mChannelNo==-1){
            Snackbar.make(mView,getString(R.string.share_error_no_choose_device),Snackbar.LENGTH_LONG).show();
            return;
        }

//        new AsyncTask<Void,Void,Boolean>(){
//            @Override
//            protected Boolean doInBackground(Void... params) {
//                boolean foo =  true;
//                if (foo)return true;
//
//                SoapManager s = SoapManager.getInstance();
//                AddDeviceSharerRes res = s.getAddDeviceSharerRes(new AddDeviceSharerReq(
//                        LoginAction.getInstance().getmInfo().getAccount(),
//                        LoginAction.getInstance().getmInfo().getLr().getLoginSession(),
//                        mDevID,
//                        mChannelNo,
//                        shareAccount));
//                Log.e("123","res="+res.toString());
//                if (res.getResult().equalsIgnoreCase("ok"))return true;
//
//                return false;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean aBoolean) {
//                super.onPostExecute(aBoolean);
//
//                if (aBoolean){
//                    //TODO add db,
//                    String time = Util.Date2ISODate(new Date());
//                    mDao.insertAddShare(shareAccount,time,mDevID,mDevName);
////                    ArrayList<ShareItem2Bean> l = mDao.queryAllAddShare(mNeedChooseDev?null:mDevID);
////                    Log.i("123","l="+l.toString());
//                }
//                mHandler.sendEmptyMessage(aBoolean?MSG_SHARE_ADD_SHARE_OK:MSG_SHARE_ADD_SHARE_ERROR);
//            }
//        }.execute();

    }
}
