package com.howell.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.howell.action.SettingAction;
import com.howell.bean.CameraItemBean;
import com.howell.ecam.R;
import com.howell.utils.AlerDialogUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

/**
 * Created by howell on 2016/12/8.
 */

public class DeviceSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,SeekBar.OnSeekBarChangeListener {

    public static final int MSG_SETTING_GAIN_ERROR          = 0x00;
    public static final int MSG_SETTING_WAIT_DISSHOW        = 0x01;
    public static final int MSG_SETTING_WAIT_SHOW           = 0x02;



    Toolbar mTb;
    CameraItemBean mBean;
    TextView mDeviceName,mResolutionTv,mPictureTv,mUpdataTv;
    SeekBar mResolutionSb,mPictureSb;
    AppCompatCheckBox mTurnCb,mLampCb,mDetRecCb,mDetAlarmCb;
    Button mUpdataBtn;
    RelativeLayout mSharell;
    ImageView mShareIv;

    boolean mIsSaved,mIsTurn,mIsLamp,mIsRec,mIsAlarm;
    int mResolution,mPicture;

    private ProgressDialog mPd;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SETTING_GAIN_ERROR:
                    break;
                case MSG_SETTING_WAIT_DISSHOW:
                    waitUnshow();
                    break;
                case MSG_SETTING_WAIT_SHOW:
                    Bundle bundle = (Bundle) msg.obj;
                    String titleStr = bundle.getString("title");
                    String msgStr = bundle.getString("msg");
                    waitShow(titleStr,msgStr);
                    break;
            }




        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_setting_action_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.camera_setting_action_refresh:
                //TODO refresh:
                Log.i("123","refresh");
                gainSet();
                waitShow(getResources().getString(R.string.gain_set),getResources().getString(R.string.please_wait));
                break;
            case R.id.camera_setting_action_save:
                Log.i("123","save it");
                mIsSaved = true;
                //TODO save:
                saveSet(false);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_setting);
        mBean = (CameraItemBean) getIntent().getSerializableExtra("bean");
        initToolbar();
        initView();
        initFun();
    }

    @Override
    protected void onDestroy() {
        //



        super.onDestroy();
    }

    private void initToolbar(){
        mTb = (Toolbar) findViewById(R.id.camera_setting_toolbar);

//        Octicons.Icon.oct_chevron_left
//        GoogleMaterial.Icon.gmd_arrow_left_bottom
        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));
//        mTb.setOverflowIcon(new IconicsDrawable(this,GoogleMaterial.Icon.gmd_refresh_sync).actionBar().color(Color.WHITE));
//        mTb.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_theaters_white_24dp));
        mTb.showOverflowMenu();
        mTb.setTitle(getString(R.string.camera_settting_title));



        mTb.inflateMenu(R.menu.camera_setting_action_menu);
//        mTb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.camera_setting_action_refresh:
//                        //TODO refresh:
//                        Log.i("123","refresh");
//                        gainSet();
//                        waitShow(getResources().getString(R.string.gain_set),getResources().getString(R.string.please_wait));
//                        break;
//                    case R.id.camera_setting_action_save:
//                        Log.i("123","save it");
//                        mIsSaved = true;
//                        //TODO save:
//                        saveSet(false);
//                        break;
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });




//        mTb.getMenu().findItem(R.id.camera_setting_action_refresh);//.setIcon(new IconicsDrawable(this,GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));

//        mTb.setSubtitle(getString(R.string.add_listen_subtitle));
        setSupportActionBar(mTb);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        mTb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsSaved)finish();
                doSaveDialog();
            }
        });
    }
    private void initView(){
        mDeviceName = (TextView) findViewById(R.id.camera_setting_name_tv);
        mResolutionSb = (SeekBar) findViewById(R.id.camera_setting_resolution_sb);
        mPictureSb = (SeekBar) findViewById(R.id.camera_setting_picture_sb);

        mResolutionTv = (TextView) findViewById(R.id.camera_setting_resolution_tv);
        mPictureTv = (TextView) findViewById(R.id.camera_setting_picture_tv);

        mTurnCb = (AppCompatCheckBox) findViewById(R.id.camera_setting_turn_cb);
        mLampCb = (AppCompatCheckBox) findViewById(R.id.camera_setting_lamp_cb);


        mDetRecCb = (AppCompatCheckBox) findViewById(R.id.camera_setting_det_dec_cb);
        mDetAlarmCb = (AppCompatCheckBox) findViewById(R.id.camera_setting_det_alarm_cb);

        mUpdataTv = (TextView) findViewById(R.id.camera_setting_updata_tv);
        mUpdataBtn = (Button) findViewById(R.id.camera_setting_updata_btn);

        mSharell = (RelativeLayout) findViewById(R.id.camera_setting_share_ll);
        mShareIv = (ImageView) findViewById(R.id.camera_setting_share_iv);
        mShareIv.setImageDrawable(new IconicsDrawable(this,GoogleMaterial.Icon.gmd_chevron_right).actionBar().colorRes(R.color.homeText));
    }

    private void initFun(){
        mIsSaved = false;
        mTurnCb.setOnCheckedChangeListener(this);
        mLampCb.setOnCheckedChangeListener(this);
        mDetRecCb.setOnCheckedChangeListener(this);
        mDetAlarmCb.setOnCheckedChangeListener(this);
        mResolutionSb.setOnSeekBarChangeListener(this);
        mPictureSb.setOnSeekBarChangeListener(this);

        mDeviceName.setText(mBean.getCameraName());
        //send msg

        gainSet();


        //wait dialog
        waitShow(getResources().getString(R.string.gain_set),getResources().getString(R.string.please_wait));
    }

    private void waitShow(String title,String msg){
        mPd = new ProgressDialog(this);
        mPd.setTitle(title);
        mPd.setMessage(msg);
        mPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPd.show();
    }

    private void waitUnshow(){
        mPd.dismiss();
    }

    private void gainSet(){
        SettingAction.getInstance().loadSetting();
    }

    private void saveSet(boolean bExit){
        mIsSaved = true;






    }

    private void doSaveDialog(){
        if (mIsSaved)return;
        AlerDialogUtils.postDialogMsg(this,
                getResources().getString(R.string.camera_setting_save_exit_title),
                getResources().getString(R.string.camera_setting_save_exit_msg),
                getResources().getString(R.string.camera_setting_save_exit_ok),
                getResources().getString(R.string.camera_setting_save_exit_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("123","save it");
                        saveSet(true);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("123","no save it");
                        finish();
                    }
                });
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.camera_setting_turn_cb:
                mIsTurn = isChecked;
                break;
            case R.id.camera_setting_lamp_cb:
                mIsLamp = isChecked;
                break;
            case R.id.camera_setting_det_dec_cb:
                mIsRec = isChecked;
                break;
            case R.id.camera_setting_det_alarm_cb:
                mIsAlarm = isChecked;
                break;
            default:
                break;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.camera_setting_resolution_sb:
                mResolution = progress;
                break;
            case R.id.camera_setting_picture_sb:
                mPicture = progress;
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {    }







}
