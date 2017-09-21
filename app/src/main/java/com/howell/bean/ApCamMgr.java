package com.howell.bean;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.howell.action.AudioAction;
import com.howell.action.ConfigAction;
import com.howell.action.LoginAction;
import com.howell.activity.BasePlayActivity;
import com.howell.db.ApDeviceDao;
import com.howell.entityclass.VODRecord;
import com.howell.jni.JniUtil;
import com.howell.utils.ServerConfigSp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howell on 2016/12/6.
 */

public class ApCamMgr implements ICam {
    Context mContext=null;
    CameraItemBean mCamBean=null;

    int mIsSub = 1;
    int mIsPlayBack = 0;
    Handler mHandler;
    ICam.IStream mStreamCB = null;
    private MyTimerTask myTimerTask = null;
    private Timer timer = null;
    private static final int F_TIME = 2;//刷新率  s
    private ApTimeBean mBegVideoListTime = null ,mEndVideoListTime = null;
    private boolean mIsNetInit = false;

    int mCurCount = 0;//当前页的总文件数
    int isCrypto = 0;
    String mLastDay = "";


    @Override
    public void init(Context context, CameraItemBean bean) {
        this.mContext = context;
        this.mCamBean = bean;
    }

    @Override
    public void deInit() {
        deInitVideoList();
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void registStreamLenCallback(IStream cb) {
        mStreamCB = cb;
    }

    @Override
    public void unregistStreamLenCallback() {
        mStreamCB = null;
    }

    @Override
    public void setStreamBSub(boolean isSub) {
        mIsSub = isSub?1:0;
    }

    @Override
    public void setPlayBack(boolean isPlayback) {
        mIsPlayBack = isPlayback?1:0;
    }

    @Override
    public void setPlayBackTime(String startTime, String endTime) {
        //单个回放录像的时间
        ApTimeBean [] beans = phaseTime(startTime,endTime);
        JniUtil.setPlayBackTime(beans[0],beans[1]);
    }


    @Override
    public boolean bind() {
        if (!checkInit())return false;
        return addAP2DB(mContext
                , ConfigAction.getInstance(mContext).getName()            /*LoginAction.getInstance().getmInfo().getAccount()*/
                ,mCamBean.getCameraName()
                ,mCamBean.getUpnpIP()
                ,mCamBean.getUpnpPort());
    }

    @Override
    public boolean unBind() {
        if (!checkInit()){
            Log.e("123","ap unBind error");
            return false;
        }
        ApDeviceDao dao = new ApDeviceDao(mContext,"user.db",1);

        dao.deleteByName(LoginAction.getInstance().getmInfo().getAccount(),mCamBean.getCameraName());
        dao.close();
        Log.i("123","ap unBind ok");
        return true;
    }

    @Override
    public boolean loginCam() {
        isCrypto = ServerConfigSp.loadServerIsCrypto(mContext)?1:0;
        return apLoginCamera();
    }

    @Override
    public boolean logoutCam() {
        return apLogoutCamera();
    }

    @Override
    public boolean playViewCam() {
        return apPlayViewCam();
    }


    @Override
    public boolean stopViewCam() {
        return apStopPlayViewCam();
    }

    @Override
    public boolean reLink() {
        if(!apStopPlayViewCam()){
            Log.e("123","stop view cam error");
            return false;
        }
        if(!apLogoutCamera()){
            Log.e("123","logout cam error");
            return false;
        }
        if(!apLoginCamera()){
            Log.e("123","login cam error");
            return false;
        }
        if(!apPlayViewCam()){
            Log.e("123","play view cam error");
            return false;
        }
        return true;
    }

    @Override
    public boolean playBackReplay(long begOffset,long curProgress) {





        return false;
    }

    @Override
    public boolean playBackPause(boolean bPause, long begOffset, long curProgress) {
        return false;
    }


    @Override
    public boolean catchPic(String path) {
        Log.i("123","save path="+path);
        JniUtil.catchPic(path);
        return true;
    }

    @Override
    public boolean soundSetData(byte[] buf, int len) {
        return true;
    }

    @Override
    public boolean ptzSetInfo(String account, String loginSession, String devID, int channelNo) {
        return true;
    }

    @Override
    public boolean zoomTeleStart() {
        return JniUtil.netPtzCam(1);
    }

    @Override
    public boolean zoomTeleStop() {
        return JniUtil.netPtzCam(0);
    }

    @Override
    public boolean zoomWideStart() {
        return JniUtil.netPtzCam(2);
    }

    @Override
    public boolean zoomWideStop() {
        return JniUtil.netPtzCam(0);
    }

    @Override
    public boolean ptzMoveStart(String direction) {
        int cmd = 0;
        if ("Up".equals(direction)){
            cmd = 1;
        }else if("Down".equals(direction)){
            cmd = 2;
        }else if ("Left".equals(direction)){
            cmd = 3;
        }else if ("Right".equals(direction)){
            cmd = 4;
        }
        return JniUtil.netPtzMove(cmd);
    }

    @Override
    public boolean ptzMoveStop() {
        return JniUtil.netPtzMove(0);
    }


    @Override
    public boolean hasVideoList() {



        return true;
    }

    private ApTimeBean [] phaseTime(String startTime,String endTime){
        ApTimeBean [] beans = new ApTimeBean[2];

        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateStart = null;
        Date dateEnd = null;
        Calendar calendar = Calendar.getInstance();
        try {
            dateStart = sdf.parse(startTime);
            dateEnd  = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(dateEnd);
        beans[1] = new ApTimeBean((short) (calendar.get(calendar.YEAR)),(short)(1+calendar.get(calendar.MONTH)),
                (short)(calendar.get(calendar.DAY_OF_WEEK)-1),(short)calendar.get(calendar.DAY_OF_MONTH),
                (short)calendar.get(calendar.HOUR_OF_DAY),(short)calendar.get(calendar.MINUTE),
                (short)calendar.get(calendar.SECOND),(short)calendar.get(calendar.MILLISECOND));
        if (dateStart.getYear()==70){
            calendar.add(Calendar.MONTH,-2);//2 month before
        }else{
            calendar.setTime(dateStart);
        }

        beans[0] = new ApTimeBean((short) (calendar.get(calendar.YEAR)),(short)(1+calendar.get(calendar.MONTH)),
                (short)(calendar.get(calendar.DAY_OF_WEEK)-1),(short)calendar.get(calendar.DAY_OF_MONTH),
                (short)calendar.get(calendar.HOUR_OF_DAY),(short)calendar.get(calendar.MINUTE),
                (short)calendar.get(calendar.SECOND),(short)calendar.get(calendar.MILLISECOND));
        return beans;
    }


    @Override
    public void setVideoListTime(String startTime, String endTime) {
        //回放列表   结束时间为当前  开始时间为2月前

        ApTimeBean [] beans = phaseTime(startTime,endTime);
        mBegVideoListTime = beans[0];
        mEndVideoListTime = beans[1];

//        Log.i("123","~~~  endTime="+calendar.get(calendar.YEAR)  +" "+calendar.get(calendar.MONTH)+" "+calendar.get(calendar.DAY_OF_WEEK)+" "+calendar.get(calendar.DAY_OF_MONTH)+" "+calendar.get(calendar.DAY_OF_WEEK_IN_MONTH)+"  "
//                +calendar.get(calendar.HOUR_OF_DAY)+" "+calendar.get(calendar.MINUTE)+"  "+calendar.get(calendar.SECOND)+" "+calendar.get(calendar.MILLISECOND));
    }

    @Override
    public int getVideoListPageCount(int nowPage, int pageSize) {
        if (mBegVideoListTime==null||mEndVideoListTime==null)return 0;
        initVideoList();
//        return JniUtil.netGetVideoListCount(mBegVideoListTime,mEndVideoListTime);
        mCurCount = JniUtil.netGetVideoListPageCount(mBegVideoListTime,mEndVideoListTime,pageSize,nowPage);
        return mCurCount;
    }

    @Override
    public ArrayList<VODRecord> getVideoList() {
        if (mCurCount<=0)return null;
        ReplayFile[] replayFiles = JniUtil.netGetVideoListAll(mCurCount);
        List<ReplayFile> list = Arrays.asList(replayFiles);
        ArrayList<VODRecord> vodList = new ArrayList<VODRecord>();
        for(ReplayFile f:list){
            StringBuffer begSb = new StringBuffer()
                    .append(f.getBegYear()).append("-")
                    .append(f.getBegMonth()).append("-")
                    .append(f.getBegDay()).append(" ")
                    .append(f.getBegHour()).append(":")
                    .append(f.getBegMin()).append(":")
                    .append(f.getBegSec());
            StringBuffer endSb = new StringBuffer()
                    .append(f.getEndYear()).append("-")
                    .append(f.getEndMonth()).append("-")
                    .append(f.getEndDay()).append(" ")
                    .append(f.getEndHour()).append(":")
                    .append(f.getEndMin()).append(":")
                    .append(f.getEndSec());

            VODRecord vod = new VODRecord();
            vod.setStartTime(begSb.toString());
            vod.setEndTime(endSb.toString());
            vod.setTimeZoneStartTime(begSb.toString());
            vod.setTimeZoneEndTime(endSb.toString());
            vod.setHasTitle(mLastDay.equals(f.getEndDay()+"")?false:true);
            vod.setWatched(false);
            mLastDay = f.getEndDay()+"";

            vodList.add(vod);
        }
        return vodList;
    }

    @Override
    public boolean playPause(boolean b) {
        return JniUtil.pause(b);
    }

    @Override
    public boolean isPlayBackCtrlAllow() {
        return false;
    }


    private boolean initVideoList() {
        Log.i("123","mIsNetInit="+mIsNetInit);
        if (mIsNetInit)return true;
        JniUtil.netInit();
        JniUtil.login(mCamBean.getUpnpIP());
        mIsNetInit = true;
        return true;
    }


    private boolean deInitVideoList() {
        if (mIsNetInit) {
            JniUtil.netCloseVideoList();
            JniUtil.loginOut();
            JniUtil.netDeinit();
            mIsNetInit = false;
        }
        return true;
    }


    private boolean checkInit(){
        if (mCamBean==null||mContext==null)return false;
        return true;
    }

    private boolean addAP2DB(Context context,String userName, String deviceName, String ip, int port){
        int portNum = port;
        ApDeviceDao dao = new ApDeviceDao(context,"user.db",1);
        APDeviceDBBean bean = new APDeviceDBBean(userName,deviceName,ip,portNum);
        if (dao.findByName(userName,deviceName)){
            dao.updataByName(bean,userName,deviceName);
        }else{
            dao.insert(bean);
        }
        dao.close();
        return true;
    }

    private boolean apLoginCamera(){
        if (!checkInit())return false;
        JniUtil.netInit();
        JniUtil.setCallBackObj(this);

        if(!JniUtil.login(mCamBean.getUpnpIP())){
            Log.e("123","ap login error");
            return false;
        }


        //Audio
        AudioAction.getInstance().initAudio();
        AudioAction.getInstance().playAudio();
        return true;
    }

    private boolean apLogoutCamera(){
        JniUtil.loginOut();
        JniUtil.releasePlay();//释放解码器
        AudioAction.getInstance().deInitAudio();
        JniUtil.netDeinit();
        return true;
    }

    private boolean apPlayViewCam(){
        if(!JniUtil.netReadyPlay(isCrypto,mIsPlayBack,mCamBean.getChannelNo(),mIsSub)){
            return false;
        }
        JniUtil.playView();
        startTimerTask();
        return true;
    }

    private boolean apStopPlayViewCam(){
        JniUtil.netStopPlay();
        AudioAction.getInstance().stopAudio();
        JniUtil.stopView();
        stopTimerTask();
        return true;
    }

    private void startTimerTask(){
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask,0,F_TIME*1000);
    }

    private void stopTimerTask(){
        if (timer!=null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (myTimerTask!=null){
            myTimerTask.cancel();
            myTimerTask = null;
        }
    }

    class MyTimerTask extends TimerTask {

        int mUnexpectNoFrame = 0;
        boolean doOnce = false;
        @Override
        public void run() {
            int streamLen = JniUtil.netGetStreamLenSomeTime();
            int speed = streamLen*8/1024/F_TIME;
            if (mStreamCB!=null){
                mStreamCB.showStreamSpeed(speed);
            }
            if (streamLen==0){
                mUnexpectNoFrame++;
            }else {
                mUnexpectNoFrame = 0;
                if (!doOnce) {
                    doOnce = true;
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_UNWAIT);
                }
            }

            if (mUnexpectNoFrame==3){
                mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_WAIT);
                doOnce = false;
            }
            if (mUnexpectNoFrame == 10){
                //TODO relink
            }
        }
    }


}
