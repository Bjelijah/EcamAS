package com.howell.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.howell.action.AudioAction;
import com.howell.action.LoginAction;
import com.howell.action.PTZControlAction;
import com.howell.action.PlayAction;
import com.howell.adapter.MyPagerAdapter;
import com.android.howell.webcam.R;
import com.howell.modules.player.IPlayContract;
import com.howell.modules.player.bean.VODRecord;
import com.howell.transformer.CubeInTransformer;
import com.howell.utils.AlerDialogUtils;
import com.howell.utils.PhoneConfig;
import com.howell.utils.UserConfigSp;

import java.util.List;

/**
 * Created by Administrator on 2016/12/16.
 */

public class PlayViewActivity extends BasePlayActivity implements GestureDetector.OnGestureListener,View.OnTouchListener,View.OnClickListener,IPlayFun {

    private GestureDetector mGestureDetector;
    private RelativeLayout mPlayPtzMove;
    private LinearLayout mPtzLeft,mPtzRight,mPtzUp,mPtzDown;
    private PlayFunViewPage mPlayFun;
    private boolean mIsShowPtz;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPlayView();
        initFun();
        start();
    }

    @Override
    protected void onDestroy() {
        AudioAction.getInstance().deInitAudioRecord();
        super.onDestroy();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

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
        Log.i("123","play view cam play");
        super.camPlay();

    }


    @Override
    protected void camStop() {
        super.camStop();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (PhoneConfig.getPhoneHeight(this)>PhoneConfig.getPhoneWidth(this))return false;
        if (isShowSurfaceIcon){
            showSurfaceIcon(false);
            mPlayFun.setBottomView(null);
        }else{
            showSurfaceIcon(true);
            mPlayFun.setBottomView(mSurfaceIcon);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (!PlayAction.getInstance().getPlayBean().isPtz()){
           // return false;
        }

        final int hMax = PhoneConfig.getPhoneHeight(this);
        final int wMax = PhoneConfig.getPhoneWidth(this);
        if (hMax > wMax) {
            Log.e("123", "竖屏");
            return false;
        }

        if (!PTZControlAction.getInstance().bAnimationFinish()) {
            Log.e("123", "ptz animation not finish");
            return false;
        }
        final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;

        if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_MIN_VELOCITY && e1.getX() < (wMax/2)) {
            Log.e("123", "fling up");
            mPlayPtzMove.setVisibility(View.VISIBLE);
            mPlayFun.setVisibility(View.VISIBLE);
            if (mIsShowPtz) {//当前显示  从当中到最上
                //要不显示显示
                PTZControlAction.getInstance().ptzAnimationStart(this,mPlayPtzMove,0,0,0,-hMax,false,true);
                PTZControlAction.getInstance().ptzAnimationStart(this,mPlayFun,0,0,0,hMax,false,false);
                mIsShowPtz = false;
            }else{//当前不显示 从最下到当中
                //要显示
                PTZControlAction.getInstance().ptzAnimationStart(this,mPlayPtzMove,0,0,hMax,0,true,true);
                PTZControlAction.getInstance().ptzAnimationStart(this,mPlayFun,0,0,-hMax,0,true,false);
                mIsShowPtz = true;
            }

        }else if(e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_MIN_VELOCITY && e1.getX() < (wMax/2)){
            Log.e("123", "fling down");
            mPlayPtzMove.setVisibility(View.VISIBLE);
            mPlayFun.setVisibility(View.VISIBLE);
            if(mIsShowPtz){//当前显示  从当中到最下
                //要不显示
                PTZControlAction.getInstance().ptzAnimationStart(this,mPlayPtzMove,0,0,0,hMax,false,true);
                PTZControlAction.getInstance().ptzAnimationStart(this,mPlayFun,0,0,0,-hMax,false,false);
                mIsShowPtz = false;
            }else{//从最上到当中
                PTZControlAction.getInstance().ptzAnimationStart(this, mPlayPtzMove, 0, 0, -hMax, 0, true,true);
                PTZControlAction.getInstance().ptzAnimationStart(this,mPlayFun,0,0,hMax,0,true,false);
                mIsShowPtz = true;
            }

        }
        return true;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouch(v,event);

        switch (v.getId()){
            case R.id.play_talk:
                talkBtnFun(event);
                return false;
            case R.id.play_ptz_left:
            case R.id.play_ptz_right:
            case R.id.play_ptz_top:
            case R.id.play_ptz_bottom:
                ptzFun(v.getId(),event);
                return false;

            default:
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.pop_layout_sd:
                mPopupWindow.dismiss();
                PlayAction.getInstance().rePlay(1);
                break;
            case R.id.pop_layout_hd:
                mPopupWindow.dismiss();
                PlayAction.getInstance().rePlay(0);
                break;
            case R.id.sound:
                this.soundFun();
                break;
            case R.id.vedio_list:
                showVodFun();
                break;
            case R.id.catch_picture:
                PlayAction.getInstance().catchPic();
                break;
            case R.id.player_change_stream:
                mPopupWindow.showAsDropDown(v);
                break;
            case R.id.player_imagebutton_back:
                PlayAction.getInstance().catchPic("/sdcard/eCamera/cache");
                //TODO: stop play

                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            PlayAction.getInstance().catchPic("/sdcard/eCamera/cache");
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showVodFun(){
        //if
        if (!PlayAction.getInstance().getPlayBean().isStore()){
            AlerDialogUtils.postDialogMsg(this,
                    getResources().getString(R.string.no_estore),
                    getResources().getString(R.string.no_sdcard),null);
            return;
        }
        //stop
        finish();
        //goto activity
        Intent intent = new Intent(this, VideoListActivity.class);
        intent.putExtra("bean",PlayAction.getInstance().getPlayBean());
        startActivity(intent);
    }


    private void initPlayView(){
        mReplaySeekBar.setVisibility(View.GONE);
        fragmentInit();
    }

    private void initFun(){
        mGestureDetector = new GestureDetector(this,this);
        AudioAction.getInstance().initAudioRecord();
        AudioAction.getInstance().initSound(this);
        mBtTalk.setOnTouchListener(this);
        mSound.setOnClickListener(this);
        mVodList.setOnClickListener(this);
        mCatchPicture.setOnClickListener(this);
        mHD.setOnClickListener(this);
        mSD.setOnClickListener(this);
        mStreamChange.setOnClickListener(this);
        mBack.setOnClickListener(this);
        PlayAction.getInstance().setPlayBack(false);
    }

    private void fragmentPtzInit(){
        mPlayPtzMove = (RelativeLayout) findViewById(R.id.play_rl_ptz);
        mPtzLeft = (LinearLayout) findViewById(R.id.play_ptz_left);
        mPtzRight = (LinearLayout) findViewById(R.id.play_ptz_right);
        mPtzUp = (LinearLayout) findViewById(R.id.play_ptz_top);
        mPtzDown = (LinearLayout) findViewById(R.id.play_ptz_bottom);
        mPtzLeft.setOnTouchListener(this);
        mPtzRight.setOnTouchListener(this);
        mPtzUp.setOnTouchListener(this);
        mPtzDown.setOnTouchListener(this);
        Log.i("123","mPlayMgr="+mPlayMgr);
        PTZControlAction.getInstance().setCam(mPlayMgr).setHandle(mHandler).setPtzInfo(
                LoginAction.getInstance().getmInfo().getAccount(),
                LoginAction.getInstance().getmInfo().getLr().getLoginSession(),
                PlayAction.getInstance().getPlayBean().getDeviceId(),
                PlayAction.getInstance().getPlayBean().getChannelNo());
        mIsShowPtz = false;
    }

    private void fragmentFunInit(){
        mPlayFun = (PlayFunViewPage) findViewById(R.id.play_fun);
        mPlayFun.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mPlayFun.setCurrentItem(200);
        mPlayFun.setPageTransformer(true,new CubeInTransformer());
        mPlayFun.setBottomView(mSurfaceIcon);
    }


    private void fragmentInit(){
        fragmentPtzInit();
        fragmentFunInit();
    }

    private void talkBtnFun(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("123", "按下了   开始对讲");
                AudioAction.getInstance().pauseAudio();
                AudioAction.getInstance().startAudioRecord();
//                mIsTalk = true;
                setOrientation(true);
                break;
            case MotionEvent.ACTION_UP:
                Log.i("123", "ACTION_UP   停止对讲");
                AudioAction.getInstance().stopAudioRecord();
                AudioAction.getInstance().playAudio();
                setOrientation(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i("123", "ACTION_CANCEL   停止对讲");
                AudioAction.getInstance().stopAudioRecord();
                AudioAction.getInstance().playAudio();
                setOrientation(false);
                break;
            default:
                break;
        }
    }

    private void setOrientation(boolean isTalking){
        int ro =getRequestedOrientation();
        if (isTalking) {
//            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){//2   shu
//
//                showSurfaceIcon(false);
////            if (mIsTalk)
//            } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){//1  heng
//                showSurfaceIcon(true);
////            if (mIsTalk)
//            }
            Log.i("123","orientation    = "+this.getResources().getConfiguration().orientation);
            setRequestedOrientation(this.getResources().getConfiguration().orientation);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        Log.i("123","ro="+ro);
    }


    private void ptzFun(int viewID,MotionEvent event){
        switch (viewID){
            case R.id.play_ptz_left:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    PTZControlAction.getInstance().ptzMoveStart("Left");
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    PTZControlAction.getInstance().ptzMoveStop();
                }
                break;
            case R.id.play_ptz_right:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    PTZControlAction.getInstance().ptzMoveStart("Right");
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    PTZControlAction.getInstance().ptzMoveStop();
                }
                break;
            case R.id.play_ptz_top:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    PTZControlAction.getInstance().ptzMoveStart("Up");
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    PTZControlAction.getInstance().ptzMoveStop();
                }
                break;
            case R.id.play_ptz_bottom:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    PTZControlAction.getInstance().ptzMoveStart("Down");
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    PTZControlAction.getInstance().ptzMoveStop();
                }
                break;
            default:
                break;
        }
    }

    private void start(){
        this.camConnect();
    }

    private void end(){

    }

    @Override
    protected void soundFun() {
        super.soundFun();
        UserConfigSp.saveSoundState(PlayViewActivity.this,mIsAudioOpen);
        mPlayFun.updataAllView();
    }

    @Override
    public void clickSound() {
        if (mIsAudioOpen){
            mIsAudioOpen = false;
            PlayAction.getInstance().mute();
        }else{
            mIsAudioOpen = true;
            PlayAction.getInstance().unmute();
        }
        UserConfigSp.saveSoundState(PlayViewActivity.this,mIsAudioOpen);
    }

    @Override
    public boolean getSoundState() {
        return mIsAudioOpen;
    }


}
