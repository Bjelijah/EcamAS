package com.howell.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
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
    public static final int MSG_SETTING_GAIN_MSG            = 0x03;
    public static final int MSG_SETTING_SAVE_ERROR          = 0x04;
    public static final int MSG_SETTING_SAVE_OK             = 0x05;
    Toolbar mTb;
    CameraItemBean mBean;
    TextView mDeviceName,mResolutionTv,mPictureTv,mUpdataTv;
    SeekBar mResolutionSb,mPictureSb;
    AppCompatCheckBox mTurnCb,mLampCb,mDetRecCb,mDetAlarmCb;
    Button mUpdataBtn,mRenameBtn;
    RelativeLayout mSharell;
    ImageView mShareIv;

    boolean mIsSaved,mIsTurn,mIsLamp,mIsRec,mIsAlarm,mIsRename=false;
    int mResolution,mPicture;

    private ProgressDialog mPd;


    public static String [] mFrameSizeValues;
    public static String [] mResolutionTexts;
    public static String [] mImageQualityTexts;
    public static int[][] reso_bitrate_map_ = {{96,128,196},{128,256,384},{1024,1536,2048}};

    private int mResoIndex,mQualityIndex;
    private boolean mBRotation,mBLamp,mBVmd,mBPush;
    private String renameNewName;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SETTING_GAIN_ERROR:
                    Snackbar.make(mTb,"获取信息失败", Snackbar.LENGTH_SHORT).show();
                    break;
                case MSG_SETTING_WAIT_DISSHOW:
                    waitUnshow();
                    break;
                case MSG_SETTING_WAIT_SHOW:
                    Bundle bundle = msg.getData();
                    String titleStr = bundle.getString("title");
                    String msgStr = bundle.getString("msg");
                    waitShow(titleStr,msgStr,0);
                    break;
                case MSG_SETTING_GAIN_MSG:
                    doShowParam(msg.getData());
                    break;
                case MSG_SETTING_SAVE_ERROR:
                    break;
                case MSG_SETTING_SAVE_OK:
                    break;
                default:
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
                waitShow(getResources().getString(R.string.gain_set),getResources().getString(R.string.please_wait),0);
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

//                gmd_chevron_left
//        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));
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
        mRenameBtn = (Button) findViewById(R.id.camera_setting_name_btn);
        mRenameBtn.setPadding(2,2,2,2);
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
        mUpdataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mRenameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameDailogShow();
            }
        });

        mDeviceName.setText(mBean.getCameraName());

        mResolutionTexts = getResources().getStringArray(R.array.ResolutionText);
        mFrameSizeValues = getResources().getStringArray(R.array.FrameSize);
        mImageQualityTexts = getResources().getStringArray(R.array.ImageQualityText);

        SettingAction.getInstance().setHandler(mHandler).setBean(mBean);

        //send msg

        gainSet();


        //wait dialog
        waitShow(getResources().getString(R.string.gain_set),getResources().getString(R.string.please_wait),0);
    }

    private void waitShow(String title,String msg,long autoDismissMS){
        mPd = new ProgressDialog(this);
        mPd.setTitle(title);
        mPd.setMessage(msg);
        mPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPd.show();
        if (autoDismissMS>0){
            mHandler.sendEmptyMessageDelayed(MSG_SETTING_WAIT_DISSHOW,autoDismissMS);
        }
    }

    private void waitUnshow(){
        mPd.dismiss();
    }

    private void renameDailogShow(){
        View v = LayoutInflater.from(this).inflate(R.layout.dailog_rename,null);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(v);
        final AutoCompleteTextView atv = (AutoCompleteTextView) v.findViewById(R.id.camera_setting_rename_dailog_et);
        adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIsRename = false;
            }
        });
        adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIsRename = false;
                renameNewName = atv.getText().toString();
                if (!renameNewName.equals("")){
                    mIsRename = true;
                    mDeviceName.setText(renameNewName);
                }
            }
        });
        adb.setTitle(getResources().getString(R.string.camera_setting_rename_title));
        AlertDialog ad = adb.create();
        ad.show();
    }


    private void gainSet(){
        SettingAction.getInstance().loadSetting();
    }

    private void saveSet(boolean bExit){
        mIsSaved = true;
        doSaveParam();
        if (bExit){
            finish();
        }else{
            waitShow(getResources().getString(R.string.camera_setting_save_title),getResources().getString(R.string.camera_setting_save_msg),1000);
        }

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

    private void doShowParam(Bundle b){
        if (b==null){
            Log.i("123","b==null");
            return;
        }
        Log.i("123","do show param b="+b.toString());
        String frameSize = b.getString("frameSize");
        int bitrate = b.getInt("bitrate");
        boolean bVmd = b.getBoolean("bVmd");
        boolean bPush = b.getBoolean("bPush");
        boolean bLamp = b.getBoolean("bLamp");
        boolean bRotation = b.getBoolean("bRotation");
        boolean bNeedUpdata = b.getBoolean("bNeedUpdata");
        String curVer = b.getString("curVer");
        String newVer = b.getString("newVer");
        Log.i("123","do Show Param");
        int resoIndex = getResoIndex(frameSize);
        mResoIndex = resoIndex;
        Log.i("123","reso Index="+resoIndex);
        mResolutionSb.setProgress(resoIndex);
        mResolutionTv.setText(mResolutionTexts[resoIndex]);
        int qualityIndex = getQualityIndex(resoIndex,bitrate);
        mQualityIndex = qualityIndex;
        Log.i("123","quality index="+qualityIndex);
        mPictureSb.setProgress(qualityIndex);
        mPictureTv.setText(mImageQualityTexts[qualityIndex]);

        mBRotation = bRotation;
        mBLamp = bLamp;
        mTurnCb.setChecked(bRotation);
        mLampCb.setChecked(bLamp);

        mBVmd = bVmd;
        mBPush = bPush;
        mDetRecCb.setChecked(bVmd);
        mDetAlarmCb.setChecked(bVmd?bPush:false);
        mDetAlarmCb.setEnabled(bVmd);

        mUpdataBtn.setVisibility(bNeedUpdata?View.VISIBLE:View.GONE);
        String updataTv = bNeedUpdata?
                getResources().getString(R.string.camera_setting_cur_version)+":("+curVer+") "+getResources().getString(R.string.camera_setting_new_version)+":("+newVer+")":
                getResources().getString(R.string.camera_setting_no_new_version)+" ("+curVer+")";
        mUpdataTv.setText(updataTv);
    }

    private int getResoIndex(String frameSize){
        for (int i=0;i<mFrameSizeValues.length;++i){
            if (mFrameSizeValues[i].equals(frameSize)){
                return i;
            }
        }
        return 0;
    }

    private int getQualityIndex(int resoIndex,int bitrate){
        for (int i=0;i<reso_bitrate_map_[resoIndex].length;++i){
            if (reso_bitrate_map_[resoIndex][i]>=bitrate){
                return i;
            }
        }
        return 0;
    }



    private void doSaveParam(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("bSaveEncode",mResoIndex==mResolutionSb.getProgress()&&mQualityIndex==mPictureSb.getProgress()?false:true);
        bundle.putInt("resoIndex",mResolutionSb.getProgress());
        bundle.putInt("qualityIndex",mPictureSb.getProgress());
        bundle.putBoolean("bSaveTurn",mTurnCb.isChecked()!=mBRotation);
        bundle.putBoolean("bTurn",mTurnCb.isChecked());
        bundle.putBoolean("bSaveLamp",mLampCb.isChecked()!=mBLamp);
        bundle.putBoolean("bLamp",mLampCb.isChecked());
        bundle.putBoolean("bSaveVmd",mDetRecCb.isChecked()!=mBVmd);
        bundle.putBoolean("bVmd",mDetRecCb.isChecked());
        bundle.putBoolean("bSavePush",mDetAlarmCb.isChecked()!=mBPush);
        bundle.putBoolean("bPush",mDetAlarmCb.isChecked());
        bundle.putBoolean("bRename",mIsRename);
        bundle.putString("newName",renameNewName);
        SettingAction.getInstance().saveSetting(bundle);

    }




    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mIsSaved =false;
        switch (buttonView.getId()){
            case R.id.camera_setting_turn_cb:
                mIsTurn = isChecked;
                break;
            case R.id.camera_setting_lamp_cb:
                mIsLamp = isChecked;
                break;
            case R.id.camera_setting_det_dec_cb:
                mIsRec = isChecked;
                mDetAlarmCb.setEnabled(mIsRec);
                if (!mIsRec){
                    mDetAlarmCb.setChecked(false);
                }
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
        mIsSaved = false;
        switch (seekBar.getId()){
            case R.id.camera_setting_resolution_sb:
                mResolution = progress;
                mResolutionTv.setText(mResolutionTexts[mResolution]);
                break;
            case R.id.camera_setting_picture_sb:
                mPicture = progress;
                mPictureTv.setText(mImageQualityTexts[mPicture]);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if (mIsSaved)finish();
                doSaveDialog();
                return false;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
