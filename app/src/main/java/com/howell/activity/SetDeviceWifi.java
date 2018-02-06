package com.howell.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.howell.broadcastreceiver.HomeKeyEventBroadCastReceiver;
import com.android.howell.webcam.R;

import com.howell.utils.NetWorkUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

public class SetDeviceWifi extends AppCompatActivity implements OnClickListener{
	private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION= 0x1234;
	private static final int STATE_OK			= 0xe0;
	private static final int STATE_ERROR_NO_SSID = 0xe1;
	private static final int STATE_ERROR_NO_NAME = 0xe2;
	private static final int STATE_ERROR_NO_PASSWORD = 0xe3;

	private NetWorkUtils mWifiAdmin;

	private EditText wifi_password,device_name /*,wifi_ssid*/;
	//private Button btnSend,btnSendFinish;
	private ImageButton mOk;
	private ImageButton mBack;
	private ImageView mIvLocation;
	private Activities mActivities;
	private HomeKeyEventBroadCastReceiver receiver;

	private Spinner wifi_ssid;
	private String[] Member;
	private ArrayAdapter<String> myAdapter;

//	private SoapManager mSoapManager;
	private MyssidReceive mReceive = new MyssidReceive();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_device_wifi);
		mActivities = Activities.getInstance();
		mActivities.addActivity("SetDeviceWifi",SetDeviceWifi.this);
		receiver = new HomeKeyEventBroadCastReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
//		mSoapManager = SoapManager.getInstance();
		mWifiAdmin = new NetWorkUtils(this);
//		System.out.println(mWifiAdmin.getWifiSSID());
//		Log.e("123","wifi admin="+mWifiAdmin.getWifiSSID());
		//wifi_ssid = (EditText)findViewById(R.id.et_wifi);
		wifi_password = (EditText)findViewById(R.id.et_wifi_password);
		device_name  = (EditText)findViewById(R.id.et_device_name);
		//btnSend = (Button)findViewById(R.id.btn_send);
		//btnSendFinish = (Button)findViewById(R.id.btn_send_finish);
		mOk = (ImageButton)findViewById(R.id.ib_set_device_ok);
		mBack = (ImageButton)findViewById(R.id.ib_set_device_wifi_back);
		//wifi_ssid.setText(removeMarks(mWifiAdmin.getWifiSSID()));
		mIvLocation = (ImageView) findViewById(R.id.iv_location);
		mIvLocation.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_gps).actionBar().color(Color.RED));


		registerReceiver(mReceive,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		mWifiAdmin.scan(this);


//		ArrayList<String> list = mWifiAdmin.getSSIDResultList();
		Member = new String[0];



//		list.toArray(Member);
//		wifi_ssid = (EditText)findViewById(R.id.et_wifi);
		wifi_ssid = (Spinner)findViewById(R.id.spinner_wifi);
		myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Member);
		myAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		wifi_ssid.setAdapter(myAdapter);


		wifi_ssid.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
				//Toast.makeText(getApplicationContext(),
				//        "你选择了："+Member[arg2], 0).show();
				//arg0.setVisibility(View.VISIBLE);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		//btnSend.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mOk.setOnClickListener(this);

		SendMatchCodeTask task = new SendMatchCodeTask();
		task.execute();
		//btnSendFinish.setOnClickListener(this);
	}

	public class SendMatchCodeTask extends AsyncTask<Void, Integer, Void> {
//		GetDeviceMatchingCodeRes res;
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			System.out.println("call doInBackground");
//			GetDeviceMatchingCodeReq req = new GetDeviceMatchingCodeReq(mSoapManager.getLoginResponse().getAccount(),mSoapManager.getLoginResponse().getLoginSession());
//			res = mSoapManager.getGetDeviceMatchingCodeRes(req);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
//			System.out.println(res.getResult()+","+res.getMatchingCode());
		}
	}

	@SuppressWarnings("unused")
	private String removeMarks(String SSID){
		if(SSID.startsWith("\"") && SSID.endsWith("\"")){
			SSID = SSID.substring(1, SSID.length()-1);
		}
		return SSID;
	}



	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		Log.e("123","onRequestPermissionsResult");
//		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
			getScanningResults();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mActivities.removeActivity("SetDeviceWifi");
		unregisterReceiver(receiver);
		unregisterReceiver(mReceive);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.ib_set_device_ok:
				int res = checkOkState();

				if (res==STATE_ERROR_NO_NAME){
					Toast.makeText(SetDeviceWifi.this,getString(R.string.add_listen_no_name),Toast.LENGTH_SHORT).show();
					break;
				}else if(res == STATE_ERROR_NO_SSID){
					Toast.makeText(SetDeviceWifi.this,getString(R.string.add_listen_no_ssid),Toast.LENGTH_SHORT).show();
					break;
				}else if(res == STATE_ERROR_NO_PASSWORD){
					Toast.makeText(SetDeviceWifi.this,getString(R.string.add_listen_no_pwd),Toast.LENGTH_SHORT).show();
					break;
				}
				Intent intent = new Intent(SetDeviceWifi.this,FlashLighting.class);
				intent.putExtra("wifi_ssid", wifi_ssid.getSelectedItem().toString());
				intent.putExtra("wifi_password", wifi_password.getText().toString());
				intent.putExtra("device_name", device_name.getText().toString());
				startActivity(intent);
				break;
			case R.id.ib_set_device_wifi_back:
				finish();
				break;
			default:
				break;
		}
	}
	private int checkOkState(){
		if (device_name.getText().toString().equals("")){
			return STATE_ERROR_NO_NAME;
		}
		if (wifi_ssid.getSelectedItem()==null){
			return STATE_ERROR_NO_SSID;
		}
		if (wifi_ssid.getSelectedItem().toString().equals("")){
			return STATE_ERROR_NO_SSID;
		}
		if (wifi_password.getText().toString().equals("")){
			return STATE_ERROR_NO_PASSWORD;
		}

		return STATE_OK;
	}


	private void getScanningResults(){
		ArrayList<String> list = mWifiAdmin.getSSIDResultList();
		Member = new String[list.size()];
		Log.e("123","getScanningResults size="+list.size());
//		for (String s:list){
//			Log.e("123","ssid="+s);
//		}
		list.toArray(Member);
//		for (int i=0;i<Member.length;i++){
//			Log.i("123","member "+i+"    "+Member[i]);
//		}

//		myAdapter.notifyDataSetChanged();
		myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Member);
		myAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

		wifi_ssid.setAdapter(myAdapter);
	}

	class MyssidReceive extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION){
				Log.e("123","on receive");
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
					requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
							PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
					//After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
				}else{
					getScanningResults();
					//do something, permission was previously granted; or legacy device
				}
			}
		}
	}




}
