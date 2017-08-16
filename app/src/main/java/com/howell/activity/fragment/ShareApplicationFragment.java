package com.howell.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.howell.webcam.R;

/**
 * Created by Administrator on 2017/7/13.
 */

public class ShareApplicationFragment extends ShareBaseFragment {

    View mView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_share1,container,false);
        return mView;
    }

    @Override
    public void fun(int flag) {

    }
}
