package com.howell.activity.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2017/7/14.
 */

public abstract class ShareBaseFragment extends Fragment {
    String mDevID;
    String mDevName;
    int mChannelNo;
    public ShareBaseFragment(){}

    public ShareBaseFragment(String devID,String devName,int channelNo){
      mDevID = devID;
      mDevName = devName;
      mChannelNo = channelNo;
    }
    public abstract void fun(int flag);

}
