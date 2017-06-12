package com.howell.websocket;

import android.util.Log;


import com.howell.bean.WSRes;

import org.json.JSONException;

import java.util.ArrayList;

import autobahn.WebSocketConnection;
import autobahn.WebSocketConnectionHandler;
import autobahn.WebSocketException;


/**
 * Created by Administrator on 2017/4/6.
 */

public class WebSocketManager {

    public static final int ERROR_SEND = 0x01;
    public static final int ERROR_RECEIVE = 0x02;


    private String wsuri;
    private WebSocketConnection mConnect;
    ArrayList<IMessage> mCallback=null;
    private boolean mIsOpen = false;
    public WebSocketManager registMessage(IMessage c){
        if (mCallback==null)mCallback = new ArrayList<IMessage>();
        mCallback.add(c);
        return this;
    }
    public void unregistMessage(IMessage c){
        if (mCallback==null)return;
        mCallback.remove(c);
    }

    private void sendOpen(){
        if (mCallback==null)return;
        for (IMessage msg:mCallback){
            msg.onWebSocketOpen();
        }
    }

    private void sendClose(){
        if (mCallback==null)return;
        for (IMessage msg:mCallback){
            msg.onWebSocketClose();
        }
    }

    private void sendError(int error){
        if (mCallback==null)return;
        for (IMessage msg:mCallback){
            msg.onError(error);
        }
    }

    private void sendMessage(WSRes res){
        if (mCallback==null)return;
        for (IMessage msg:mCallback){
            msg.onGetMessage(res);
        }
    }


    public WebSocketManager initURL(String serverIP) throws WebSocketException {
        mIsOpen = false;
        wsuri = "ws://"+serverIP+":8803/howell/ver10/ADC";
        mConnect = new WebSocketConnection();
        mConnect.connect(wsuri,new WebSocketConnectionHandler(){
            @Override
            public void onOpen() {
                super.onOpen();
                mIsOpen = true;
                sendOpen();
            }

            @Override
            public void onClose(int code, String reason) {
                super.onClose(code, reason);
                mIsOpen = false;
                sendClose();
            }

            @Override
            public void onTextMessage(String payload) {
                super.onTextMessage(payload);
                try {
                    handleMessageJsonString(payload);
                } catch (JSONException e) {
                    sendError(ERROR_RECEIVE);
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public void deInit(){
        mConnect.disconnect();
        mIsOpen = false;

    }


    public void alarmLink(int cseq,String session,String username) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createAlarmPushConnectJsonObject(cseq,session,username).toString());
    }

    public void alarmAlive(int cseq,long systemUpTime,double longitude,double latitude,boolean useLongitudeOrLatitude) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createAlarmAliveJsonObject(cseq,systemUpTime,longitude,latitude,useLongitudeOrLatitude).toString());
    }


    public void ADCEventRes(int cseq) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createADCEventResJsonObject(cseq).toString());
    }

    public void ADCNoticeRes(int cseq) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createADCNoticeResJsonObject(cseq).toString());
    }

    public void MCUSendNotice(WSRes.AlarmNotice n) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createMCUSendMessage(n).toString());
    }



    private void handleMessageJsonString(String jsonStr) throws JSONException {
        Log.i("123","handleMessageJsonString   ="+jsonStr);
        sendMessage(JsonUtil.parseResJsonString(jsonStr));
    }



    public interface IMessage{
        void onWebSocketOpen();
        void onWebSocketClose();
        void onGetMessage(WSRes res);
        void onError(int error);
    }
}
