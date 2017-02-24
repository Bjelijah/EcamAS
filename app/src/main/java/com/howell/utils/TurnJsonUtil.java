package com.howell.utils;

import com.howell.bean.TurnSubScribeAckBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/2/23.
 */

public class TurnJsonUtil {

    public static TurnSubScribeAckBean getTurnSubscribeAckAllFromJsonStr(String jsonStr){
        TurnSubScribeAckBean bean = new TurnSubScribeAckBean();
        int code = 0;
        String subscribeID = null;
        String detail = null;
        JSONObject obj = null;
        JSONObject mediaObj = null;
        int dialogID = 0;
        JSONObject metaObj = null;
        String deviceID = null;
        String mode = null;
        int channel = 0;
        int stream = 0;
        JSONObject videoObj = null;
        int [] resolution = new int[2];
        int frameRate = 0;
        String bitctrl = null;
        int bitrate = 0;
        int gop = 0;
        String videoCodec = null;
        JSONObject audioObj = null;
        int samples = 0;
        int channels = 0;
        int bitwidth = 0;
        String audioCodec = null;
        boolean keyFrameIndex = false;
        try {
            obj = new JSONObject(jsonStr);
            code = obj.getInt("code");
            if (code!=200){
                detail = obj.getString("detail");
                bean.setCode(code);
                bean.setDetail(detail);
            }else{
                subscribeID = obj.getString("subscribe_id");
                mediaObj = obj.getJSONObject("media");
                dialogID = mediaObj.getInt("dialog_id");
                metaObj = mediaObj.getJSONObject("meta");
                deviceID = metaObj.getString("device_id");
                mode = metaObj.getString("mode");
                channel = metaObj.getInt("channel");
                stream = metaObj.getInt("stream");
                videoObj = metaObj.getJSONObject("video");
                JSONArray arr = videoObj.getJSONArray("resolution");
                resolution[0] = arr.getInt(0);
                resolution[1] = arr.getInt(1);
                frameRate = videoObj.getInt("frame_rate");
                bitctrl = videoObj.getString("bitctrl");
                bitrate = videoObj.getInt("bitrate");
                gop = videoObj.getInt("gop");
                videoCodec = videoObj.getString("codec");
                audioObj = metaObj.getJSONObject("audio");
                samples = audioObj.getInt("samples");
                channels = audioObj.getInt("channels");
                bitwidth = audioObj.getInt("bitwidth");
                audioCodec = audioObj.getString("codec");
                keyFrameIndex = metaObj.getBoolean("key_frame_index");

                //set
                bean.setCode(code);
                bean.setSubscribeId(subscribeID);
                bean.setMediaDialogID(dialogID);
                bean.setDeviceID(deviceID);
                bean.setMode(mode);
                bean.setChannel(channel);
                bean.setStream(stream);
                bean.setVideoResolution(resolution);
                bean.setVideoFrameRate(frameRate);
                bean.setVideoBitctrl(bitctrl);
                bean.setVideoBitrate(bitrate);
                bean.setVideoGop(gop);

                if (videoCodec.contains("h265")){
                    bean.setVideoCodec(videoCodec.contains("encrypt")?3:2);
                }else if(videoCodec.contains("h264")){
                    bean.setVideoCodec(videoCodec.contains("encrypt")?1:0);
                }else {
                    bean.setVideoCodec(0);
                }
//				bean.setVideoCodec(1);//FIXME just for test hwplay.so
                bean.setAudioSamples(samples);
                bean.setAudioChannels(channels);
                bean.setAudioBitwidth(bitwidth);

                if (audioCodec.contains("aac")){
                    bean.setAudioCodec(0);
                }else if(audioCodec.contains("G711")){
                    bean.setAudioCodec(1);
                }
                bean.setKeyFrameIndex(keyFrameIndex);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }


}
