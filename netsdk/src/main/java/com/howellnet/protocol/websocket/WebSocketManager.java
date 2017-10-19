package com.howellnet.protocol.websocket;




import android.util.Log;

import com.howellnet.bean.websocket.WSRes;
import com.howellnet.protocol.autobahn.*;
import com.howellnet.protocol.utils.SDKDebugLog;

import org.json.JSONException;


import java.util.HashSet;


/**
 * Created by Administrator on 2017/4/6.<br/>
 * WS Manager : client get alarm push,notice push,event push from server<br/>
 * long link ,keep alive by heartbeat<br/>
 * @author howell
 */
public class WebSocketManager {
    private static final String TAG = WebSocketManager.class.getName();
    public static final int ERROR_SEND = 0x01;
    public static final int ERROR_RECEIVE = 0x02;

    private static WebSocketManager mInstance = null;
    private WebSocketManager(){}
    public static WebSocketManager getInstance(){
        if (mInstance==null) {
            synchronized (WebSocketManager.class) {
                if (mInstance == null) {
                    mInstance = new WebSocketManager();
                }
            }
        }
        return mInstance;
    }


    private String wsuri;
    private WebSocketConnection mConnect;
    HashSet<IMessage> mCallback=null;
    private boolean mIsOpen = false;
    public WebSocketManager registMessage(IMessage c){
        if (mCallback==null)mCallback = new HashSet<IMessage>();
        mCallback.add(c);
        Log.i("547","mcallback  size="+mCallback.size());
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

    /**
     * deInit<br/>
     * disconnect
     */
    public void deInit(){
        synchronized (this) {
            if (mConnect != null) {
                mConnect.disconnect();
                mConnect = null;
            }
        }
    }

    /**
     * init url
     * @param serverIP sever ip
     * @return this Manager
     * @throws WebSocketException
     */
    public WebSocketManager initURL(String serverIP) throws WebSocketException {

            if (mConnect != null && mConnect.isConnected()) {
                Log.e("547", "initURL  mConnect is connect  we return");
                return this;
            }
            if(mIsOpen){
                Log.e("547","init url misOpen we return");
                return this;
            }

            wsuri = "ws://" + serverIP + ":8803/howell/ver10/ADC";
            mConnect = new WebSocketConnection();
            Log.i("547", "connect~~~~~~~~~~~~~~   wsuri=" + wsuri);
            mConnect.connect(wsuri, new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {
                    super.onOpen();
                    Log.i("547", "~~~~~~~~~~~~ws onOpen");
                    mIsOpen = true;
                    sendOpen();
                }

                @Override
                public void onClose(int code, String reason) {
                    super.onClose(code, reason);
                    Log.i("547", "ws onClose");
                    mIsOpen = false;
                    sendClose();
                }

                @Override
                public void onTextMessage(String payload) {
                    super.onTextMessage(payload);
                    Log.i("547", "onTextMessage=" + payload);
                    try {
                        handleMessageJsonString(payload);
                    } catch (JSONException e) {
                        sendError(ERROR_RECEIVE);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRawTextMessage(byte[] payload) {
                    super.onRawTextMessage(payload);
                }

                @Override
                public void onBinaryMessage(byte[] payload) {
                    super.onBinaryMessage(payload);
                }
            });

        return this;
    }

    public boolean isOpen(){
        return mIsOpen;
    }


    /**
     * send link information to server (like login protocol)
     * @param cseq
     * @param session the session u login in to server
     * @param username the username u login in to server
     * @throws JSONException
     */
    public void alarmLink(int cseq,String session,String username) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        String msg = JsonUtil.createAlarmPushConnectJsonObject(cseq,session,username).toString();
        Log.i("547","alarm link  msg="+msg);
        mConnect.sendTextMessage(msg);
    }

    /**
     * send heartbeat to server
     * @param cseq
     * @param systemUpTime system time: how long has this app run (can be 0)
     * @param longitude longitude (can be 0)
     * @param latitude latitude (can be 0)
     * @param useLongitudeOrLatitude if use it (if u set longitude or latitude 0:false; else true)
     * @throws JSONException
     */
    public void alarmAlive(int cseq,long systemUpTime,double longitude,double latitude,boolean useLongitudeOrLatitude) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createAlarmAliveJsonObject(cseq,systemUpTime,longitude,latitude,useLongitudeOrLatitude).toString());
    }

    /**
     * after get event push from server send handle result back
     * @param cseq same as event cseq that u got in event push
     * @throws JSONException
     */
    public void ADCEventRes(int cseq) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createADCEventResJsonObject(cseq).toString());
    }

    /**
     * after get notice push from server,send handle result back
     * @param cseq same as cseq that u got in notice push
     * @throws JSONException
     */
    public void ADCNoticeRes(int cseq) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createADCNoticeResJsonObject(cseq).toString());
    }

    public void pushRes(int cseq) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createPushResJsonObject(cseq).toString());
    }


    /**
     * client send notice to server
     * @deprecated not support in current
     * @param n notice
     * @throws JSONException
     */
    public void MCUSendNotice(WSRes.AlarmNotice n) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createMCUSendMessage(n).toString());
    }



    private void handleMessageJsonString(String jsonStr) throws JSONException {
        SDKDebugLog.logI(TAG+":handleMessageJsonString","jsonStr="+jsonStr);
        sendMessage(JsonUtil.parseResJsonString(jsonStr));
    }



    public interface IMessage{
        void onWebSocketOpen();
        void onWebSocketClose();
        void onGetMessage(WSRes res);
        void onError(int error);
    }
}