package com.howell.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.howell.action.PlayAction;
import com.android.howell.webcam.R;
import com.howell.jni.JniUtil;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/1/10.
 */

public class PlayBackActivity extends BasePlayActivity implements View.OnClickListener,PlayAction.IPlayBackFun,SeekBar.OnSeekBarChangeListener{


    private long mCurBeg = 0;
    private boolean mIsPause = false;
    private long mLastProgressOffset = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPlayBackView();
        initFun();
        start();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(MSG_PLAY_PLAY_BACK_FUN);
        PlayAction.getInstance().unregistPlayBackCallback();
        super.onDestroy();
    }

    private void initPlayBackView(){
        boolean isAllow = PlayAction.getInstance().isSeekBarAllow();
        mReplaySeekBar.setVisibility(isAllow?View.VISIBLE:View.GONE);
        mPause.setVisibility(isAllow?View.VISIBLE:View.GONE);
        mStreamChange.setVisibility(View.GONE);
        mBtTalk.setVisibility(View.GONE);
        mVodList.setVisibility(View.GONE);
    }


    private void initFun(){
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");
        mBack.setOnClickListener(this);
        mCatchPicture.setOnClickListener(this);
        mSound.setOnClickListener(this);
        mReplaySeekBar.setOnSeekBarChangeListener(this);
        mPause.setOnClickListener(this);
        PlayAction.getInstance().setPlayBack(true).setPlayBackTime(startTime,endTime).registPlayBackCallback(this);
    }

    private String translateTime(int progress){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        String text = sdf.format(mCurBeg*1000 + progress);
//        Log.i("123"," cur progress+offset= "+mCurBeg*1000 + progress+"                 progress="+progress);
        return text;
    }




    private void start(){
        mIsPause = false;
        PlayAction.getInstance().setPlayBackProgressByUser(false);
        this.camConnect();
    }

    private void pause(){
        if (mIsPause){//现在播放
            mIsPause = false;
            mPause.setImageDrawable(getResources().getDrawable(R.mipmap.img_pause));
            mHandler.sendEmptyMessageDelayed(MSG_PLAY_PLAY_BACK_FUN,200);
        }else{//现在暂停
            mIsPause = true;
            mPause.setImageDrawable(getResources().getDrawable(R.mipmap.img_play));
            mHandler.removeMessages(MSG_PLAY_PLAY_BACK_FUN);
        }
        PlayAction.getInstance().camPlayBackPause(mIsPause,mCurBeg,mReplaySeekBar.getProgress());
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
        mHandler.sendEmptyMessageDelayed(MSG_PLAY_PLAY_BACK_FUN,200);
    }

    @Override
    protected void camStop() {
        super.camStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.player_imagebutton_back:
                finish();
                break;
            case R.id.sound:
                soundFun();
                break;
            case R.id.catch_picture:
                PlayAction.getInstance().catchPic();
                break;
            case R.id.ib_pause:
                pause();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayBackBegEnd(long beg, long end) {//秒数
        int max = (int)((end-beg)*1000);//开始到结束时间的毫秒
        mCurBeg = beg;
        Log.i("123","!!!!!!!!!!onPlayback beg="+beg+" end="+end+"    max="+max);

        mReplaySeekBar.setMax((int)((end-beg)*1000));
    }

    @Override
    protected void playBackFun() {
        mHandler.sendEmptyMessageDelayed(MSG_PLAY_PLAY_BACK_FUN,200);
        if (PlayAction.getInstance().getPlayBackKeepProgress())return;
        long begTimestamp = JniUtil.getBegPlayTimestamp();
        if (begTimestamp == 0) {
            return;
        }
        long curTimestamp = JniUtil.getCurPlayTimestamp();
        long offset = curTimestamp - begTimestamp;

        Log.i("123","offset=            "+offset);
        if (mLastProgressOffset!=offset) {
            mReplaySeekBar.setProgress((int) offset);
            mLastProgressOffset = offset;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mReplaySeekBar.setSeekBarText(translateTime(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        PlayAction.getInstance().setPlayBackProgressByUser(true);
        PlayAction.getInstance().setPlayBackKeepProgress(true);
        int progress = seekBar.getProgress();
        mReplaySeekBar.setSeekBarText(translateTime(progress));
        mHandler.removeMessages(MSG_PLAY_PLAY_BACK_FUN);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        PlayAction.getInstance().setPlayBackProgressByUser(false);
        int progress = seekBar.getProgress();
        mLastProgressOffset = progress;
        PlayAction.getInstance().playBackRePlay(mCurBeg,progress);
        mHandler.sendEmptyMessageDelayed(MSG_PLAY_PLAY_BACK_FUN,1000);
    }
}
