package com.howell.activity;

/**
 * @author 霍之昊 
 * 
 * 类说明：app登录页面
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.util.Log;

import com.howell.action.ConfigAction;
import com.howell.action.LoginAction;
import com.howell.bean.Custom;
import com.howell.bean.UserLoginDBBean;
import com.howell.db.UserLoginDao;
import com.android.howell.webcam.R;
import com.howell.modules.Login.ILoginContract;
import com.howell.modules.Login.bean.Type;
import com.howell.modules.Login.presenter.LoginSoapPresenter;
import com.howell.protocol.SoapManager;
import com.howell.service.MyService;
import com.howell.utils.NetWorkUtils;

import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import retrofit2.http.Header;

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

	}

	private void init(){
		mIsFromNotification = getIntent().getBooleanExtra("notification",false);

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
			mPresenter = new LoginSoapPresenter();//// FIXME: 2017/9/14 add  http
		}
		mPresenter.bindView(this);
	}

	@Override
	public void unbindPresenter() {
		mPresenter.unbindView();
	}

	@Override
	public void onError() {
		startActivity(new Intent(this,LoginActivity.class));
	}

	@Override
	public void onLoginResult(Type type) {
		switch (type){
			case OK:
				startActivity(new Intent(LogoActivity.this, HomeExActivity.class).putExtra("notification",mIsFromNotification));
				break;
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
	public void onLogoutResult(Type type) {

	}

	private void doLogin(){
		mPresenter.init(this);
		mPresenter.login(null,null,null);
	}

}
