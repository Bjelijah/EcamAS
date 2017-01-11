package com.howell.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.howell.action.PlayAction;

/**
 * Created by Administrator on 2017/1/10.
 */

public class PlayBackActivity extends BasePlayActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initFun();
        start();
    }

    private void initFun(){
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");
        PlayAction.getInstance().setPlayBack(true).setPlayBackTime(startTime,endTime);
    }


    private void start(){
        this.camConnect();
    }

    @Override
    protected void camConnect() {
        super.camConnect();
    }

    @Override
    protected void camDisconnect() {
        super.camDisconnect();
    }

    @Override
    protected void camPlay() {
        super.camPlay();
    }

    @Override
    protected void camStop() {
        super.camStop();
    }
}
