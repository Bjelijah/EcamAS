package com.howell.activity;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.howell.action.AudioAction;
import com.howell.action.PlayAction;
import com.howell.action.YV12Renderer;
import com.howell.bean.CamFactory;
import com.howell.bean.ICam;
import com.howell.ecam.R;
import com.howell.ehlib.MySeekBar;
import com.howell.utils.FileUtils;
import com.howell.utils.MessageUtiles;
import com.howell.utils.PhoneConfig;

import java.io.File;

/**
 * Created by Administrator on 2016/12/16.
 */

public class BasePlayActivity extends FragmentActivity implements SurfaceHolder.Callback,View.OnTouchListener{

    public final static int MSG_PLAY_RELINK_OK          = 0xff00;
    public final static int MSG_PLAY_SOUND_MUTE         = 0xff01;
    public final static int MSG_PLAY_SOUND_UNMUTE       = 0xff02;
    public final static int MSG_PLAY_SAVE_PICTURE       = 0xff03;
    public final static int MSG_PLAY_LOGIN_CAM_OK       = 0xff04;
    public final static int MSG_PLAY_LOGIN_CAM_ERROR    = 0xff05;
    public final static int MSG_PLAY_PLAY_CAM_OK        = 0xff06;
    public final static int MSG_PLAY_PLAY_CAM_ERROR     = 0xff07;



    //控件
    protected GLSurfaceView mGlView;
    protected Button mBtTalk;
    protected ImageButton mCatchPicture,mSound,mPause,mBack;
    protected FrameLayout mTitle;
    protected TextView mStreamChange;
    protected MySeekBar mReplaySeekBar;
    protected LinearLayout mSurfaceIcon,mHD,mSD;
    protected ProgressBar mWaitProgressBar;

    protected PopupWindow mPopupWindow;
    protected TextView mStreamLen;
    protected boolean isShowSurfaceIcon = true;

    protected ICam mPlayMgr;

    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PLAY_RELINK_OK:
                    if (msg.arg1==1){
                        mStreamChange.setText("标清");
                    }else if(msg.arg1==0){
                        mStreamChange.setText("高清");
                    }
                    break;
                case MSG_PLAY_SOUND_MUTE:
                    mSound.setImageDrawable(getResources().getDrawable(R.mipmap.img_no_sound));
                    break;
                case MSG_PLAY_SOUND_UNMUTE:
                    mSound.setImageDrawable(getResources().getDrawable(R.mipmap.img_sound));
                    break;
                case MSG_PLAY_SAVE_PICTURE:
                    MessageUtiles.postToast(getApplicationContext(),getResources().getString(R.string.save_picture), Toast.LENGTH_SHORT);
                    break;
                case MSG_PLAY_LOGIN_CAM_OK:
                    break;
                case MSG_PLAY_LOGIN_CAM_ERROR:
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glsurface);
        initView();

        initViewFun();
        initPlayer();
    }




    protected void initView(){
        mGlView = (GLSurfaceView)findViewById(R.id.glsurface_view);
        mBtTalk = (Button) findViewById(R.id.play_talk);
        mCatchPicture = (ImageButton)findViewById(R.id.catch_picture);
        mSound = (ImageButton)findViewById(R.id.sound);
        mTitle = (FrameLayout)findViewById(R.id.player_title_bar);
        mStreamChange = (TextView)findViewById(R.id.player_change_stream);
        mPause = (ImageButton) findViewById(R.id.ib_pause);
        mBack = (ImageButton) findViewById(R.id.player_imagebutton_back);
        mReplaySeekBar = (MySeekBar) findViewById(R.id.replaySeekBar);
        mSurfaceIcon = (LinearLayout) findViewById(R.id.surface_icons);
        mWaitProgressBar = (ProgressBar) findViewById(R.id.waitProgressBar);

        mStreamLen = (TextView) findViewById(R.id.tv_stream_len);
        initPopupWindow();
    }


    protected void initViewFun(){
        mGlView.setEGLContextClientVersion(2);
        mGlView.setRenderer(new YV12Renderer(this,mGlView,mHandler));
        mGlView.getHolder().addCallback(this);
        mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGlView.setOnTouchListener(this);
        mGlView.setFocusable(true);
        mGlView.setClickable(true);
        mGlView.setLongClickable(true);

        if (PhoneConfig.getPhoneHeight(this)<PhoneConfig.getPhoneWidth(this)){
            showSurfaceIcon(false);
        }



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

        mPlayMgr = CamFactory.buildCam(PlayAction.getInstance().getPlayBean().getType());
        PlayAction.getInstance().setHandler(mHandler);

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


    protected void soundFun(){
        if (PlayAction.getInstance().isMute()){
            PlayAction.getInstance().unmute();
        }else{
            PlayAction.getInstance().mute();
        }
    }





    @Override
    protected void onPause() {
        mGlView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mGlView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            showSurfaceIcon(false);

        } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            showSurfaceIcon(true);

        }


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


}
