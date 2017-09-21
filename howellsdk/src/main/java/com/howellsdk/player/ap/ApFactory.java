package com.howellsdk.player.ap;

import android.support.annotation.Nullable;

import com.howell.jni.JniUtil;
import com.howellsdk.api.HWPlayApi;
import com.howellsdk.audio.AudioAction;

import com.howellsdk.player.HwBasePlay;
import com.howellsdk.player.ap.bean.ApTimeBean;
import com.howellsdk.player.turn.bean.PTZ_CMD;
import com.howellsdk.utils.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/9/4.
 */

public class ApFactory {
    String ip;
    int slot;
    int isCrypto;
    private ApFactory(String ip,int slot,int isCrypto){
        this.ip = ip;
        this.slot = slot;
        this.isCrypto = isCrypto;
    }
    public static final class Builder{
        String ip;
        int slot;
        int isCrypto;
        public Builder setIP(String ip){
            this.ip = ip;
            return this;
        }
        public Builder setSlot(int slot){
            this.slot = slot;
            return this;
        }
        public Builder setCrypto(int crypto){
            isCrypto = crypto;
            return this;
        }

        public ApFactory build(){
            return new ApFactory(ip,slot,isCrypto);
        }
    }
    public HWPlayApi create(){
        return new ApProduct();
    }

    public final class ApProduct extends HwBasePlay{
        boolean lastIsPlayback = false;
        int mCurCount;
        @Override
        public HWPlayApi bindCam() {
            super.bindCam();
            JniUtil.setCallBackObj(this);
            return this;
        }

        @Override
        public void unBindCam() {
            JniUtil.releasePlay();//释放解码器
            super.unBindCam();
        }

        @Override
        public boolean connect() {
            return JniUtil.login(ip);
        }

        @Override
        public boolean disconnect() {
            return JniUtil.loginOut();
        }

        @Override
        public void play(boolean isSub) {
            lastIsPlayback = false;
            JniUtil.netReadyPlay(isCrypto,0,slot,isSub?1:0);
            super.play(isSub);
        }

        @Override
        public void playback(boolean isSub, String begTime, String endTime) {
//            ApTimeBean bean[] = phaseTime(begTime,endTime);
            lastIsPlayback = true;
            JniUtil.netSetPlayBackTime(new ApTimeBean(begTime),new ApTimeBean(endTime));
            JniUtil.netReadyPlay(isCrypto,1,slot,isSub?1:0);
            super.playback(isSub,begTime,endTime);
        }

        @Override
        public boolean playPause() {
            return super.playPause();

        }

        @Override
        public void stop() {
            JniUtil.netStopPlay();
            super.stop();
        }

        @Override
        public void reLink(boolean isSub, @Nullable String begTime, @Nullable String endTime) {
            //stop
            stop();
            //reconnect;
            disconnect();
            connect();
            //play
            if (lastIsPlayback){
                playback(isSub,begTime,endTime);
            }else{
                play(isSub);
            }
        }

        @Override
        public boolean getRecordedFiles(String beg, String end,@Nullable Integer nowPage,@Nullable Integer pageSize) {

            ApTimeBean [] timeBeen = phaseTime(beg,end);




            return false;
        }

        @Override
        public boolean ptzControl(PTZ_CMD cmd, int speed, @Nullable Integer presetNo) {
            boolean ret = false;
            switch (cmd){
                case ptz_up:
                    ret = JniUtil.netPtzMove(1);
                    break;
                case ptz_down:
                    ret = JniUtil.netPtzMove(2);
                    break;
                case ptz_left:
                    ret = JniUtil.netPtzMove(3);
                    break;
                case ptz_right:
                    ret = JniUtil.netPtzMove(4);
                    break;
                case ptz_stop:
                    ret = JniUtil.netPtzMove(0);
                    break;
                case ptz_lrisOpen:
                    ret = JniUtil.netPtzIris(1);
                    break;
                case ptz_lrisClose:
                    ret = JniUtil.netPtzIris(0);
                    break;
                case ptz_focusFar:
                    ret = JniUtil.netPtzCam(3);
                    break;
                case ptz_focusNear:
                    ret = JniUtil.netPtzCam(4);
                    break;
                case ptz_focusStop:
                case ptz_zoomStop:
                    ret = JniUtil.netPtzCam(0);
                    break;
                case ptz_zoomTele:
                    ret = JniUtil.netPtzCam(1);
                    break;
                case ptz_zoomWide:
                    ret = JniUtil.netPtzCam(2);
                    break;
                default:
                    ret = false;
                    break;
            }
            return ret;
        }

        @Override
        public int getStreamLen() {
            return 0;
        }

        @Override
        public long getFirstTimestamp() {
            return 0;
        }

        @Override
        public long getTimestamp() {
            return 0;
        }
    }

    private ApTimeBean [] phaseTime(String startTime,String endTime){
        ApTimeBean [] beans = new ApTimeBean[2];
        Date dateStart = null;
        Date dateEnd = null;
        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat(
//                "yyyy-MM-dd'T'HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//        try {
//            dateStart = sdf.parse(startTime);
//            dateEnd  = sdf.parse(endTime);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        dateStart = Util.ISODateString2ISODate(startTime);
        dateEnd = Util.ISODateString2ISODate(endTime);


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

}
