package com.howell.action;


import android.content.Context;
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
import com.android.howell.webcam.R;
import com.howell.modules.player.IPlayContract;
import com.howell.modules.player.bean.PTZ;
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
	IPlayContract.IPresent mPresenter;

	private int animationNum = 0;
	private boolean bAnimatingFinish = true;
	private void resetAnimationNum(){
		animationNum = 0;
	}


	public boolean bAnimationFinish(){
		return bAnimatingFinish;
	}
	
	public PTZControlAction setHandle(Handler handler){
		this.handler = handler;
		return this;
	}



	public PTZControlAction setPresenter(IPlayContract.IPresent p){
		mPresenter = p;
		return this;
	}


	
	public boolean bAnimating(){
		return animationNum<2?false:true;
	}
	
	

	

	public  void zoomTeleStart(){
		mPresenter.ptzCtrl(PTZ.PTZ_ZOOM_TELE);
	}
	
	public void zoomTeleStop(){
		mPresenter.ptzCtrl(PTZ.PTZ_ZOOM_STOP);
	}
	
	
	public void zoomWideStart(){
		mPresenter.ptzCtrl(PTZ.PTZ_ZOOM_WIDE);
	}

	public void zoomWideStop(){
		mPresenter.ptzCtrl(PTZ.PTZ_ZOOM_STOP);
	}


	public void ptzMoveStart(PTZ cmd){
		Log.e("123", "ptz move start");
		mPresenter.ptzCtrl(cmd);
	}

	public void ptzMoveStop(){
		mPresenter.ptzCtrl(PTZ.PTZ_STOP);
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
