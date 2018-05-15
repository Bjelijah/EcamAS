package com.howell.activity;

import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.howell.action.PTZControlAction;
import com.howell.bean.CameraItemBean;
import com.android.howell.webcam.R;
import com.howell.ehlib.MySeekBar;
import com.howell.modules.player.IPlayContract;
import com.howell.modules.player.bean.VODRecord;
import com.howell.modules.player.presenter.PlayApPresenter;
import com.howell.modules.player.presenter.PlayEcamPresenter;
import com.howell.modules.player.presenter.PlayTurnPresenter;
import com.howell.utils.AlerDialogUtils;
import com.howell.utils.MessageUtiles;
import com.howell.utils.PhoneConfig;
import com.howell.utils.UserConfigSp;
import com.howellsdk.api.player.GLESTextureView;
import com.howellsdk.utils.RxUtil;
import com.howellsdk.utils.ThreadUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2016/12/16.
 */

public abstract class BasePlayActivity extends FragmentActivity implements IPlayContract.IVew,SurfaceHolder.Callback,View.OnTouchListener {

    public static final int MSG_PTZ_SHAKE               = 0xff00;
    public final static int MSG_PLAY_SOUND_MUTE         = 0xff01;
    public final static int MSG_PLAY_SOUND_UNMUTE       = 0xff02;
    public final static int MSG_PLAY_SAVE_PICTURE       = 0xff03;
    public final static int MSG_PLAY_LOGIN_CAM_OK       = 0xff04;
    public final static int MSG_PLAY_LOGIN_CAM_ERROR    = 0xff05;
    public final static int MSG_PLAY_PLAY_CAM_OK        = 0xff06;
    public final static int MSG_PLAY_PLAY_CAM_ERROR     = 0xff07;
    public final static int MSG_PLAY_PLAY_WAIT          = 0xff08;
    public final static int MSG_PLAY_PLAY_UNWAIT        = 0xff09;
    public final static int MSG_PLAY_STOP_CAM_OK        = 0xff0a;
    public final static int MSG_PLAY_STOP_CAM_ERROR     = 0xff0b;
    public final static int MSG_PLAY_LOGOUT_CAM_OK      = 0xff0c;
    public final static int MSG_PLAY_LOGOUT_CAM_ERROR   = 0xff0d;
    public final static int MSG_PLAY_RELINK_OK          = 0xff0e;
    public final static int MSG_PLAY_PLAY_BACK_FUN      = 0xff0f;
    public final static int MSG_PLAY_RELINK_START       = 0xff10;

    //控件
//    protected GLSurfaceView mGlView;
    protected GLESTextureView mGlView;
    protected Button mBtTalk,mTeachBtn;
    protected ImageButton mVodList,mCatchPicture,mSound,mPause,mBack;
    protected FrameLayout mTitle;
    protected TextView mStreamChange;
    protected MySeekBar mReplaySeekBar;
    protected LinearLayout mSurfaceIcon,mHD,mSD;
    protected RelativeLayout mTeachll;
    protected ProgressBar mWaitProgressBar;

    protected PopupWindow mPopupWindow;
    protected TextView mStreamLen;
    protected boolean isShowSurfaceIcon = true;
//    protected ICam mPlayMgr;
    protected boolean mIsAudioOpen = false;
    protected boolean mIsTalk;
    protected boolean isDestory = false;

    protected IPlayContract.IPresent mPresent;
    protected CameraItemBean mCam;
    protected boolean mIsSub = true;
    protected int mSpeed;
    protected long mBegTimeStamp,mCurTimeStamp;



    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (isDestory)return;
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PTZ_SHAKE:
                    PTZControlAction.getInstance().ptzShake(BasePlayActivity.this, (View)msg.obj);
                    break;
                case MSG_PLAY_RELINK_START:
                    camReLink();
                    break;
                case MSG_PLAY_RELINK_OK:
                    Log.i("123","get play re link ok");
                    if (msg.arg1==1){
                        mStreamChange.setText("标清");
                    }else if(msg.arg1==2){
                        mStreamChange.setText("高清");
                    }
                    break;
                case MSG_PLAY_SOUND_MUTE:
                    mSound.setImageDrawable(getResources().getDrawable(R.mipmap.img_no_sound));
                    mIsAudioOpen = false;
                    break;
                case MSG_PLAY_SOUND_UNMUTE:
                    mSound.setImageDrawable(getResources().getDrawable(R.mipmap.img_sound));
                    mIsAudioOpen = true;
                    break;
                case MSG_PLAY_SAVE_PICTURE:
                    MessageUtiles.postToast(getApplicationContext(),getResources().getString(R.string.save_picture), Toast.LENGTH_SHORT);
                    break;
                case MSG_PLAY_LOGIN_CAM_OK:
                    Log.e("123","MSG_PLAY_LOGIN_CAM_OK");
                    camPlay();
                    break;
                case MSG_PLAY_PLAY_CAM_ERROR:
                    Log.e("123","play cam error");
                    playErrorFun();

                    break;
                case MSG_PLAY_LOGIN_CAM_ERROR:
                    Log.e("123","MSG_PLAY_LOGIN_CAM_ERROR");
                    playErrorFun();
                    break;
                case MSG_PLAY_PLAY_WAIT:
                    mWaitProgressBar.setVisibility(View.VISIBLE);
                    break;
                case MSG_PLAY_PLAY_UNWAIT:
                    mWaitProgressBar.setVisibility(View.GONE);
                    break;
                case MSG_PLAY_PLAY_BACK_FUN:
                    playBackFun();
                    break;
                default:
                    break;
            }
        }
    };


    protected void playErrorFun(){
        RxUtil.doInUIThread(new RxUtil.RxSimpleTask<Object>() {
            @Override
            public void doTask() {
                mWaitProgressBar.setVisibility(View.GONE);
                AlerDialogUtils.postDialogMsg(BasePlayActivity.this,
                        getResources().getString(R.string.play_play_error_msg_title),
                        getResources().getString(R.string.play_play_error_msg_msg),null);
            }
        });

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glsurface);
        initView();
        initViewFun();
        initPlayer();
        bindPresenter();
        isDestory = false;
    }




    protected void initView(){
        mGlView = (GLESTextureView) findViewById(R.id.gl_texture_view);
        mBtTalk = (Button) findViewById(R.id.play_talk);
        mCatchPicture = (ImageButton)findViewById(R.id.catch_picture);
        mVodList = (ImageButton) findViewById(R.id.vedio_list);
        mSound = (ImageButton)findViewById(R.id.sound);
        mTitle = (FrameLayout)findViewById(R.id.player_title_bar);
        mStreamChange = (TextView)findViewById(R.id.player_change_stream);
        mPause = (ImageButton) findViewById(R.id.ib_pause);
        mBack = (ImageButton) findViewById(R.id.player_imagebutton_back);
        mTeachBtn = (Button)findViewById(R.id.play_teacher_btn);
        mTeachll = findViewById(R.id.play_teacher);
        mReplaySeekBar = (MySeekBar) findViewById(R.id.replaySeekBar);
        mSurfaceIcon = (LinearLayout) findViewById(R.id.surface_icons);
        mWaitProgressBar = (ProgressBar) findViewById(R.id.waitProgressBar);

        mStreamLen = (TextView) findViewById(R.id.tv_stream_len);
        findViewById(R.id.player_talk).setVisibility(View.GONE);
        initPopupWindow();
    }


    protected void initViewFun(){
//        mGlView.setEGLContextClientVersion(2);
//        mGlView.setRenderer(new YV12Renderer(this,mGlView,mHandler));
//        mGlView.getHolder().addCallback(this);
//        mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mTeachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable.timer(500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                mTeachll.setVisibility(View.GONE);
                                UserConfigSp.saveUserPtzTeach(BasePlayActivity.this,true);
                            }
                        });

            }
        });
        mTeachll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });



        mGlView.setOnTouchListener(this);
        mGlView.setFocusable(true);
        mGlView.setClickable(true);
        mGlView.setLongClickable(true);

        if (PhoneConfig.getPhoneHeight(this)<PhoneConfig.getPhoneWidth(this)){
            showSurfaceIcon(false);
        }
        mIsAudioOpen = UserConfigSp.loadSoundState(this);
        updateSoundView(!mIsAudioOpen);



    }

    protected void updateSoundView(boolean isMute){
        mSound.setImageDrawable(getResources().getDrawable(!isMute?R.mipmap.img_sound:R.mipmap.img_no_sound));
        mIsAudioOpen = !isMute;
    }

    @Override
    public void onSoundMute(boolean isMute) {
        updateSoundView(isMute);
    }

    protected void initPopupWindow(){
        View v = LayoutInflater.from(this).inflate(R.layout.popup_window,null);
        mHD = (LinearLayout) v.findViewById(R.id.pop_layout_hd);
        mSD = (LinearLayout) v.findViewById(R.id.pop_layout_sd);
        int width = PhoneConfig.getPhoneWidth(this);
        int height = width * 5 / 3;
        mPopupWindow = new PopupWindow(v,width/4,height);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
    }

    protected void initPlayer(){

//        mPlayMgr = CamFactory.buildCam(PlayAction.getInstance().getPlayBean().getType());
//        mPlayMgr.init(this,PlayAction.getInstance().getPlayBean());
//        mPlayMgr.setHandler(mHandler);
//        mPlayMgr.registStreamLenCallback(this);
//        PlayAction.getInstance().setHandler(mHandler).setCam(mPlayMgr);

        mCam = (CameraItemBean) getIntent().getSerializableExtra("CameraItem");
        Log.i("123","base play activity mCam = "+mCam.toString());



        if (!UserConfigSp.loadUserPtzTeach(this) && mCam.isPtz() ) {
            mTeachll.setVisibility(View.VISIBLE);
        }
    }


    protected void showSurfaceIcon(boolean bShow){
        if (!bShow){
            mTitle.setVisibility(View.GONE);
            mSurfaceIcon.setVisibility(View.GONE);
            mStreamLen.setVisibility(View.VISIBLE);
            isShowSurfaceIcon = false;
        }else{
            mTitle.setVisibility(View.VISIBLE);
            mSurfaceIcon.setVisibility(View.VISIBLE);
            isShowSurfaceIcon = true;
        }


    }


    protected void camConnect(){
//        PlayAction.getInstance().camLogin();
        Log.i("123","mCam="+mCam.toString());
        mPresent.init(this,mCam);
    }




    protected void soundFun(){
//        if (PlayAction.getInstance().isMute()){
//            PlayAction.getInstance().unmute();
//            mIsAudioOpen = true;
//        }else{
//            PlayAction.getInstance().mute();
//            mIsAudioOpen = false;
//        }
        mPresent.setSoundMute(mIsAudioOpen);
        mIsAudioOpen = !mIsAudioOpen;
    }

    protected void camReLink(){
        Log.i("123","relink");
//        PlayAction.getInstance().reLink();

    }

    protected void camPlay(){
        Log.i("123","base play cam play maybe playback");
//        PlayAction.getInstance().camViewPlay();
//        mPresent.play(mIsSub);
    }

    protected void camStop(){
//        PlayAction.getInstance().camViewStop();
        mPresent.stop();
    }

    protected void camDisconnect(){
//        PlayAction.getInstance().camLogout();
        mPresent.deInit();
    }



    @Override
    protected void onPause() {
        mGlView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        isDestory = false;
        mGlView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {

//        mPlayMgr.unregistStreamLenCallback();

        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
            public void run() {
        Log.i("123","cam stop");
                camStop();//ecam stop play stop
                camDisconnect(); // release play
            }
        });
        Log.i("123","cam stop finish");
        mGlView.onDestroy();
        isDestory = true;
        unbindPresenter();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {


        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){//2   heng

            showSurfaceIcon(false);
//            if (mIsTalk)
            mBtTalk.setVisibility(View.INVISIBLE);
        } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){//1  shu
            showSurfaceIcon(true);
//            if (mIsTalk)
            mBtTalk.setVisibility(View.VISIBLE);
        }

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){

            default:
                break;
        }

        return false;
    }

    protected void playBackFun(){}



    @Override
    public void bindPresenter() {
        if (mPresent==null){
            switch (mCam.getType()){
                case ECAM:
                    Log.i("123","new ecam presenter");
                    mPresent = new PlayEcamPresenter();//// FIXME: 2017/9/20
                    break;
                case HW5198:
                    mPresent = new PlayApPresenter();
                    break;
                case TURN:
                    mPresent = new PlayTurnPresenter();
                    break;
            }
        }
        mPresent.bindView(this);
    }

    @Override
    public void unbindPresenter() {
        if (mPresent!=null){
            mPresent.unbindView();
        }
    }


    @Override
    public void onConnect(boolean isSuccess) {
        if (isSuccess){
            camPlay();
        }else{
            playErrorFun();
        }
    }

    @Override
    public void onRecord(List<VODRecord> vodRecords) {

    }

    @Override
    public void onError(int flag) {
        if (flag==0) {
            playErrorFun();
        }else if(flag == 1){
            camReLink();
        }
    }

    @Override
    public void onTime(final int speed, long timestamp, long firstTimestamp, final boolean bWait) {
        mSpeed = speed;
        mBegTimeStamp = firstTimestamp;
        mCurTimeStamp = timestamp;

    }

    @Override
    public void onPlaybackStartEndTime(long beg, long end) {
        Log.i("123","base play  onPlaybackStartEndTime");
    }


}
