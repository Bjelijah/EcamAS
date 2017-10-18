package com.howell.activity;

/**
 * @author 霍之昊 
 * 
 * 类说明：app登录页面
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import com.android.howell.webcam.R;
import com.howell.action.ConfigAction;
import com.howell.jni.JniUtil;
import com.howell.modules.login.ILoginContract;
import com.howell.modules.login.bean.Type;
import com.howell.modules.login.presenter.LoginHttpPresenter;
import com.howell.modules.login.presenter.LoginSoapPresenter;
import com.howell.protocol.SoapManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class LogoActivity extends Activity implements ILoginContract.IView{

	private static final int MSG_START = 0x01;
	private ILoginContract.IPresenter mPresenter;
	//与平台交互协议单例
	private SoapManager mSoapManager;

	//是否显示开场导航标志位，存于配置文件中
	private boolean isFirstLogin;

	private String account;
	private String password;
	private boolean mIsFromNotification;

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case MSG_START:
					doLogin();
					break;
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().build());
		//init
		init();

		//bind
		bindPresenter();
		//开启service

		//推送服务初始化


		//判断手机是否连接网络
		mHandler.sendEmptyMessageDelayed(MSG_START,1000);

//		Observable.timer(1, TimeUnit.SECONDS)
//				.subscribe(new Consumer<Long>() {
//					@Override
//					public void accept(Long aLong) throws Exception {
//						doLogin();
//					}
//				}, new Consumer<Throwable>() {
//					@Override
//					public void accept(Throwable throwable) throws Exception {
//
//					}
//				});

	}

	private void init(){

		JniUtil.logEnable(true);
		mIsFromNotification = getIntent().getBooleanExtra("notification",false);
		ConfigAction.getInstance(this);
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindPresenter();
	}



	@Override
	public void bindPresenter() {
		if (mPresenter==null){

			switch (ConfigAction.getInstance(this).getMode()){
				case 0:
					mPresenter = new LoginSoapPresenter();//// FIXME: 2017/9/14 add  http
					break;
				case 1:
					mPresenter = new LoginHttpPresenter();//// FIXME: 2017/10/17 add  http
					break;
				default:
					mPresenter = new LoginSoapPresenter();//// FIXME: 2017/9/14 add  http
					break;
			}

		}
		mPresenter.bindView(this);
		mPresenter.init(this);
	}

	@Override
	public void unbindPresenter() {
        if (mPresenter!=null) {
            mPresenter.unbindView();
        }
	}

	@Override
	public void onError(Type type) {
		Log.e("123","logo on error type="+type);
		switch (type){
			case FIRST_LOGIN:
				startActivity(new Intent(this,NavigationActivity.class));
				break;
			default:
				startActivity(new Intent(this,LoginActivity.class));
				break;
		}
		finish();


	}

	@Override
	public void onLoginSuccess(String account,String email) {
		startActivity(new Intent(LogoActivity.this, HomeExActivity.class)
				.putExtra("notification",mIsFromNotification)
				.putExtra("account",account)
				.putExtra("email",email)
				.putExtra("isGuest",false)
		);
		finish();
	}

	@Override
	public void onLogoutResult(Type type) {

	}

	private void doLogin(){
		mPresenter.login(null,null,null);
	}

}
