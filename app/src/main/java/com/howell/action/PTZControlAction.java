package com.howell.action;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.howell.activity.BasePlayActivity;
import com.howell.activity.PlayerActivity;
import com.howell.bean.ICam;
import com.howell.bean.PlayType;
import com.howell.ecam.R;
import com.howell.protocol.LensControlReq;
import com.howell.protocol.LensControlRes;
import com.howell.protocol.PtzControlReq;
import com.howell.protocol.SoapManager;
import com.howell.utils.PhoneConfig;

/**
 * 
 * @author cbj
 * @category
 *	ptz control class task
 *  
 */
public class PTZControlAction {
	private static PTZControlAction mInstance = null;
	public static PTZControlAction getInstance(){
		if(mInstance == null){
			mInstance = new PTZControlAction();
		}
		return mInstance;
	}
	Handler handler = null;
	

	private int animationNum = 0;
	private boolean bAnimatingFinish = true;
	private void resetAnimationNum(){
		animationNum = 0;
	}
	private ICam mCam;

	public boolean bAnimationFinish(){
		return bAnimatingFinish;
	}
	
	public PTZControlAction setHandle(Handler handler){
		this.handler = handler;
		return this;
	}

	public PTZControlAction setCam(ICam cam){
		mCam = cam;
		return this;
	}


	
	public boolean bAnimating(){
		return animationNum<2?false:true;
	}
	
	
	public PTZControlAction setPtzInfo(String account,String session,String devId,int channel){
		if (mCam == null) {
			return this;
		}
		mCam.ptzSetInfo(account,session,devId,channel);
		return this;
	}
	

	public  void zoomTeleStart(){
		new AsyncTask<Void, Integer, Void>(){

			@Override
			protected Void doInBackground(Void... arg0) {
				if (null==mCam) {
					throw new NullPointerException();
				}
				mCam.zoomTeleStart();
				return null;
			}
		}.execute();
	}
	
	public void zoomTeleStop(){
		new AsyncTask<Void, Integer, Void>(){

			@Override
			protected Void doInBackground(Void... arg0) {
				if (null==mCam) {
					throw new NullPointerException();
				}
				mCam.zoomTeleStop();
				
				return null;
			}
			
		}.execute();
	}
	
	
	public void zoomWideStart(){
		new AsyncTask<Void, Integer, Void>(){

			@Override
			protected Void doInBackground(Void... arg0) {
				if ( null==mCam) {
					throw new NullPointerException();
				}
				mCam.zoomWideStart();
				return null;
			}
			
		}.execute();
	}

	public void zoomWideStop(){
		new AsyncTask<Void, Integer, Void>(){
			@Override
			protected Void doInBackground(Void... arg0) {
				if(null == mCam){
					throw new NullPointerException();
				}
				mCam.zoomWideStop();
				return null;
			}
		}.execute();
	}
	
	public void ptzMoveStart(final String direction){
		Log.e("123", "ptz move start");
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				if (null==mCam) {
					throw new NullPointerException();
				}
				mCam.ptzMoveStart(direction);
				return null;
			}
			
		}.execute();
	}
	
	public void ptzMoveStop(){
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				if (null==mCam) {
					throw new NullPointerException();
				}
				mCam.ptzMoveStop();
				return null;
			}
		}.execute();
	}
	

	public void ptzAnimationStart(Context context,final View view,final float fromXDelta,final float toXDelta,final float fromYDelta,final float toYDelta,final boolean bshow,final boolean bLeft){
		if (bAnimating()) {
			return;
		}
	
		final int hMax = PhoneConfig.getPhoneHeight(context);
		
		AnimationSet animationSet = new AnimationSet(true);
		Animation scaleAnimation,alphaAnimation ;
		if (bshow) {
			if (bLeft) {
//				scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f);
				scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f);
			}else{
//			
				scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
			}
			
			alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
			
		}else{
			
			if (bLeft) {
//				scaleAnimation = new ScaleAnimation(1.0f, 0.5f,1.0f, 0.5f);
				scaleAnimation = new ScaleAnimation(1.0f, 0.1f, 1.0f, 0.1f, Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f);
			}else{
				scaleAnimation = new ScaleAnimation(1.0f, 0.1f, 1.0f, 0.1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
			}
			alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
	
		}
		scaleAnimation.setDuration(1000);
		alphaAnimation.setDuration(500);
		Animation translateAnimation = new TranslateAnimation(fromXDelta, toXDelta,fromYDelta,toYDelta);
		translateAnimation.setDuration(1000);  
		translateAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
//				view.setVisibility(View.VISIBLE);
				animationNum++;
				bAnimatingFinish = false;
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				int left = view.getLeft() + (int)(toXDelta-fromXDelta);
				int right = view.getRight() + (int)(toXDelta-fromXDelta);
				int top = view.getTop() + (int)(toYDelta - fromYDelta);
				int bottom = view.getBottom() + (int)(toYDelta - fromYDelta);
			
//				Log.i("123", "after left="+left+" top="+top+" right="+right+" bottom="+bottom+" hMax="+hMax);
				view.clearAnimation();
			
				if (bshow) {
					view.layout(left, 0, right, hMax);
					Message message = new Message();
					message.what = BasePlayActivity.MSG_PTZ_SHAKE;
					message.obj = view;
					handler.sendMessage(message);
				}else{
					view.setVisibility(View.GONE);
				}
				if (bAnimating()) {
					resetAnimationNum();
					bAnimatingFinish = true;
				}
			}
		});
	
		animationSet.addAnimation(translateAnimation);
		animationSet.addAnimation(scaleAnimation);
		animationSet.addAnimation(alphaAnimation);
//		Log.i("123","before: view left="+view.getLeft()+" top="+view.getTop()+" right="+view.getRight()+" bottom="+view.getBottom());
		view.startAnimation(animationSet);	
	}
	
	
	
	public void ptzShake(Context context,View v){
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.shake_x);
		v.startAnimation(animation);
	}
	

}
