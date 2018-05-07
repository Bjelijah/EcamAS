package com.howell.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.android.howell.webcam.R;
import com.howell.jni.JniUtil;
import com.howellsdk.utils.ThreadUtil;
import com.xququ.OfflineSDK.XQuquerService;
import com.xququ.OfflineSDK.XQuquerService.XQuquerListener;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SendWifi extends DaggerAppCompatActivity implements OnClickListener , XQuquerListener{
	
//	private XQuquerService xququerService;
	public  AudioManager audiomanage;  



	private String wifi_ssid,wifi_password,device_name,mMatchCode;
	
	private ImageButton mBack,mBtnSend,mBtnFinish;
	private LinearLayout mSend,mFinish;
	private TextView tips,btnTips;
	private LinearLayout mSucceedTips;
	private boolean mIsAudioPlay;
	@Inject
	AudioTrack mAudio;

	@Inject
	Activities mActivities;

	@Inject
	HomeKeyEventBroadCastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_wifi_config);
//		mActivities = Activities.getInstance();
        mActivities.addActivity("SendWifi",SendWifi.this);
//        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE); 
	    int maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  
	    audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume - 1 , 0);
	    
//		xququerService = XQuquerService.getInstance();
		
		Intent intent = getIntent();
		wifi_ssid = intent.getStringExtra("wifi_ssid");
		wifi_password = intent.getStringExtra("wifi_password");
		device_name = intent.getStringExtra("device_name");
		mMatchCode = intent.getStringExtra("match_code");
		tips = (TextView)findViewById(R.id.tv_send_wifi_tips);
		btnTips = (TextView)findViewById(R.id.tv_send_btn_tip);
		mBack = (ImageButton)findViewById(R.id.ib_send_wifi_back);
		mSend = (LinearLayout)findViewById(R.id.ll_ib_send_wifi);
		//mAddCamera = (LinearLayout)findViewById(R.id.ll_ib_add_device);
		mFinish = (LinearLayout)findViewById(R.id.ll_ib_finish_set);
		//mBtnAdd = (ImageButton)findViewById(R.id.ib_add_device);
		mBtnSend = (ImageButton)findViewById(R.id.ib_send_wifi);
		mBtnFinish = (ImageButton)findViewById(R.id.ib_finish_set);
		mSucceedTips = (LinearLayout)findViewById(R.id.ll_send_wifi_success);
		
		mBack.setOnClickListener(this);
		mBtnSend.setOnClickListener(this);
		//mBtnAdd.setOnClickListener(this);
		mBtnFinish.setOnClickListener(this);
	}

    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mActivities.removeActivity("SendWifi");
    	unregisterReceiver(receiver);
		releaseAudio();
    }
    
	@Override
	protected void onStart()
	{
		Log.i("", "onStart");
		super.onStart();
//		xququerService.start(this);
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
//		xququerService.stop();
		stopAudio();
		Log.i("", "onStop");
	}

	@Override
	public void onRecv(byte[] data) {
		// TODO Auto-generated method stub
		String message = new String(data);
		//MessageUtiles.postToast(this, "onRecv:"+message, 2000);
		Log.i("", "onRecv:"+message);
	}

	@Override
	public void onSend() {
		// TODO Auto-generated method stub
		Log.i("", "onSend");
		tips.setText(getResources().getString(R.string.send_wifi_config_activity_tip_finish));
		//btnTips.setText("重新发送");
		//mAddCamera.setVisibility(View.VISIBLE);
		mFinish.setVisibility(View.VISIBLE);
		mSucceedTips.setVisibility(View.VISIBLE);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_send_wifi:
			//if(SoapManager.getInstance().getmGetDeviceMatchingCodeRes() == null){
			if(mMatchCode==null){
				Dialog alertDialog = new AlertDialog.Builder(SendWifi.this).   
			            setTitle(getResources().getString(R.string.send_wifi_config_activity_dialog_title)).   
			            setMessage(getResources().getString(R.string.send_wifi_config_activity_dialog_message)).   
			            setIcon(R.drawable.expander_ic_minimized).   
			            setPositiveButton(getResources().getString(R.string.send_wifi_config_activity_dialog_yes_btn), new DialogInterface.OnClickListener() {   

			                @Override   
			                public void onClick(DialogInterface dialog, int which) {   
			                    // TODO Auto-generated method stub    
			                	finish();
			                	if(mActivities.getmActivityList().containsKey("FlashLighting")){
									mActivities.getmActivityList().get("FlashLighting").finish();
								}
			                	if(mActivities.getmActivityList().containsKey("SetDeviceWifi")){
									mActivities.getmActivityList().get("SetDeviceWifi").finish();
								}
			                }   
			            }).   
			    create();   
				alertDialog.show(); 
				return;
			}
//			send();
			sendNoise();
			tips.setText(getResources().getString(R.string.send_wifi_config_activity_tip_sending));
			break;
		case R.id.ib_send_wifi_back:
			finish();
			break;
		case R.id.ib_finish_set:
			Intent intent = new Intent(SendWifi.this,GetMatchResult.class);
			intent.putExtra("device_name", device_name);
			intent.putExtra("match_code",mMatchCode);
        	startActivity(intent);
		default:
			break;
		}
		
	}
	
	private void send()
	{
		//String wifiMeesage = "Wo:"+wifi_ssid+"|"+wifi_password+"|"+SoapManager.getInstance().getmGetDeviceMatchingCodeRes().getMatchingCode();
		String wifiMeesage = "Wo:"+wifi_ssid+"|"+wifi_password+"|"+mMatchCode;
		System.out.println(wifiMeesage);
		byte[] data = wifiMeesage.getBytes();
//		if(data.length>0) xququerService.sendData(data, 0.5f);  //0.0 ~ 1.0
	}

	private void sendNoise(){
		final String wifiMeesage = "Wo:"+wifi_ssid+"|"+wifi_password+"|"+mMatchCode;
		Log.e("123","send wifimessage="+wifiMeesage);
		ThreadUtil.cachedThreadStart(new Runnable() {
			final byte [] b= JniUtil.sendNativeVoice(wifiMeesage);

			@Override
			public void run() {
				playAudio();
				while (mIsAudioPlay){
					writeAudio(b,b.length);
				}
			}
		});
		onSend();

		//延迟一分钟停
		Observable.timer(1, TimeUnit.MINUTES)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<Long>() {
					@Override
					public void accept(Long aLong) throws Exception {
						stopAudio();
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						throwable.printStackTrace();
					}
				});

	}

	private void playAudio(){
		mIsAudioPlay = true;
		if(mAudio.getPlayState()!=AudioTrack.PLAYSTATE_PLAYING){
			mAudio.play();
		}
	}
	private void stopAudio(){
		mIsAudioPlay = false;
		if (mAudio!=null){
			mAudio.stop();
		}
	}

	private void writeAudio(byte [] b,int len){
		mAudio.write(b,0,len);
	}
	private void releaseAudio(){
		if(mAudio.getPlayState()==AudioTrack.PLAYSTATE_PLAYING){
			mAudio.stop();
		}
		mAudio.release();
	}
}
