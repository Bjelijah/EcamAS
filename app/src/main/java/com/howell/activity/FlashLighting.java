package com.howell.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howell.action.FlashLightManager;
import com.howell.bean.CameraItemBean;
import com.howell.bean.PlayType;
import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.android.howell.webcam.R;
import com.howell.modules.device.IDeviceContract;
import com.howell.modules.device.presenter.DeviceSoapPresenter;
import com.howell.utils.CameraUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class FlashLighting extends DaggerAppCompatActivity implements OnClickListener,IDeviceContract.IVew{
	private TextView /*tips,*/btnTips;
	private ImageButton mBack,mFlashLight;
	//private ImageView mBackground;
	private LinearLayout mSucceedTips;
	private Activities mActivities;
//	private CameraUtils c;
	private FlashLightManager f;
	private boolean isBtnClicked;
	private String wifi_ssid,wifi_password,device_name;
	
	private ImageView ivFlash;
	
	private static final int LIGHTON = 1;
	private static final int LIGHTOFF = 2;
	private FlashThread thread;
	private Handler mHandler = new Handler(){};

	@Inject
	HomeKeyEventBroadCastReceiver receiver;

	@Inject
	IDeviceContract.IPresenter mPresenter;
	String mMatchCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_light);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		bindPresenter();
		isBtnClicked = false;
		mActivities = Activities.getInstance();
        mActivities.addActivity("FlashLighting",FlashLighting.this);
//        receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
		Intent intent = getIntent();
		wifi_ssid = intent.getStringExtra("wifi_ssid");
		wifi_password = intent.getStringExtra("wifi_password");
		device_name = intent.getStringExtra("device_name");
		
//		c = new CameraUtils();
		f = new FlashLightManager(this);
		f.init(mHandler);
		//tips = (TextView)findViewById(R.id.tv_flash_light_success);
		mBack = (ImageButton)findViewById(R.id.ib_flash_light_back);
		mFlashLight = (ImageButton)findViewById(R.id.ib_flash_light);
		btnTips = (TextView)findViewById(R.id.tv_flash_light);
		mSucceedTips = (LinearLayout)findViewById(R.id.ll_flash_light_success);
		//mBackground = (ImageView)findViewById(R.id.iv_flash_background2);
		//mFinish = (Button)findViewById(R.id.btn_flash_light_finish);
		ivFlash = (ImageView)findViewById(R.id.iv_flash_light_success);
		
		mBack.setOnClickListener(this);
		mFlashLight.setOnClickListener(this);
		mPresenter.getDevicesMatchCode();
		if(thread == null){
	        thread = new FlashThread();
	        thread.start();
        }
		//mFinish.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_flash_light:
			if(!isBtnClicked){
//				c.twinkle();
				f.twinkle();
				isBtnClicked = true;
				mFlashLight.setImageDrawable(getResources().getDrawable(R.drawable.ok_btn_red_selector));
				btnTips.setText(getResources().getString(R.string.flash_activity_turn_red_btn_name));
				btnTips.setTextColor(getResources().getColor(R.color.red));
				mSucceedTips.setVisibility(View.VISIBLE);
			}else{
//				c.stopTwinkle();
				f.stopTwinkle();
				Intent intent = new Intent(FlashLighting.this,SendWifi.class);
				intent.putExtra("wifi_ssid", wifi_ssid);
				intent.putExtra("wifi_password", wifi_password);
				intent.putExtra("device_name", device_name);
				intent.putExtra("match_code",mMatchCode);
				startActivity(intent);
				finish();
			}
			break;
			
		case R.id.ib_flash_light_back:
//			if(c.getCamera() != null){
//				c.stopTwinkle();
//			}

			f.stopTwinkle();


			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//        	if(c.getCamera() != null){
//				c.stopTwinkle();
//			}

			f.stopTwinkle();

			finish();
        }
        return false;
    }
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("onStop");
		thread.setThreadExit(true);
    	thread = null;
		isBtnClicked = false;
		mFlashLight.setImageDrawable(getResources().getDrawable(R.drawable.flash_light_btn_selecor));
		btnTips.setText(getResources().getString(R.string.flash_lighting_btn_tips));
		btnTips.setTextColor(getResources().getColor(R.color.btn_blue_color));
		mSucceedTips.setVisibility(View.GONE);
	}
	
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
		unbindPresenter();
    	mActivities.removeActivity("FlashLighting");
    	unregisterReceiver(receiver);
    }
    
    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	super.onRestart();
    	System.out.println("onRestart");
    	if(thread == null){
    		thread = new FlashThread();
    		thread.start();
    	}
    }
    
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case LIGHTON:
				System.out.println("on");
				ivFlash.setVisibility(View.VISIBLE);
				break;
			case LIGHTOFF:
				System.out.println("off");
				ivFlash.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void bindPresenter() {
//		if(mPresenter==null){
//			mPresenter = new DeviceSoapPresenter();
//		}
		mPresenter.init(this);
		mPresenter.bindView(this);
	}

	@Override
	public void unbindPresenter() {
		mPresenter.unbindView();
	}

	@Override
	public void onQueryResult(List<CameraItemBean> beanList) {

	}

	@Override
	public void onAddResult(boolean isSuccess, PlayType type) {

	}

	@Override
	public void onRemoveResult(boolean isSuccess, int pos) {

	}

	@Override
	public void onError() {

	}

	@Override
	public void onUpdateCamBean(@Nullable Boolean isTurn, @Nullable Boolean isCrypto) {

	}

	@Override
	public void onDeviceMatchCode(String s) {
		mMatchCode = s;
	}

	class FlashThread extends Thread{
		private boolean threadExit ;
		
		public FlashThread() {
			super();
			this.threadExit = false;
		}

		public boolean isThreadExit() {
			return threadExit;
		}

		public void setThreadExit(boolean threadExit) {
			this.threadExit = threadExit;
		}

		public void run() {
			while(!threadExit){
//				System.out.println("start thread");
				try {
					if(mSucceedTips.isShown()){
						handler.sendEmptyMessage(LIGHTON);
						Thread.sleep(500);
						handler.sendEmptyMessage(LIGHTOFF);
						Thread.sleep(500);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

}
