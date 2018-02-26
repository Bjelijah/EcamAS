package com.howell.activity.fragment;

import android.support.v4.app.Fragment;

import com.howell.modules.device.IDeviceContract;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * Created by howell on 2016/11/30.
 */

public abstract class HomeBaseFragment extends DaggerFragment {


    public abstract void getData();

}
