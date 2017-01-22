package com.howell.jni;

import com.howell.bean.ApTimeBean;
import com.howell.bean.ReplayFile;
import com.howell.entityclass.StreamReqContext;

public class JniUtil {
	static{
		System.loadLibrary("jpush");
		System.loadLibrary("hwtrans");
		System.loadLibrary("hwplay");
		System.loadLibrary("player_jni");
		System.loadLibrary("play_jni");
	}
	
	//yuv
	public static native void YUVInit();			//初始化
	public static native void YUVDeinit();			//释放内存
	public static native void YUVSetCallbackObject(Object callbackObject,int flag);
	public static native void YUVSetCallbackMethodName(String methodStr,int flag);
	public static native void YUVLock();
	public static native void YUVUnlock();
	public static native void YUVSetEnable();//开始显示YUV数据
	public static native void YUVRenderY();			//渲染Y数据
	public static native void YUVRenderU();			//渲染U数据
	public static native void YUVRenderV();			//渲染V数据

	public static native void nativeAudioInit();
	public static native void nativeAudioDeinit();
    public static native void nativeAudioSetCallbackObject(Object o,int flag);
    public static native void nativeAudioSetCallbackMethodName(String str,int flag);
    public static native void nativeAudioBPlayable();
    public static native void nativeAudioStop();		
   	public static native boolean nativeAudioSetdata(byte [] buf,int len);
	
	//test
	
	//net & play
	public static native void netInit();
	public static native void netDeinit();
	public static native boolean login(String ip);//no using
	public static native boolean loginOut();//no using
	public static native void setCallBackObj(Object o);
	public static native void setPlayBackTime(ApTimeBean beg,ApTimeBean end);
	public static native boolean netReadyPlay(int isPlayBack,int slot,int isSub);
	public static native boolean readyPlay(int vCodeFlag,int aCodeFlag,int isPlayBack);//vCodeFlag:0 ap,1 ecam,2 h265  //aCodeFlag 0 aac ,1g711u
	public static native void releasePlay();
	public static native void netStopPlay();
	public static native void stopView();
	public static native void playView();
	public static native int netGetStreamLenSomeTime();
	public static native boolean pause(boolean bPause);
	public static native long getCurPlayTimestamp();
	public static native long getBegPlayTimestamp();
	public static native void keepTimestamp();//before playView()
	//net ptz
	public static native boolean netPtzMove(int flag);//flag: 0 1 2 3 4 停 上下左右
	public static native boolean netPtzCam(int flag);//0 1 2 stop tele wide
	public static native boolean netPtzIris(int flag);//0 stop 1 open

	//vod file list

	public static native int netGetVideoListCount(ApTimeBean beg,ApTimeBean end);//获取总录像文件数
	public static native int netGetVideoListPageCount(ApTimeBean beg,ApTimeBean end,int pageSize,int curPageNo);//获取分页 总页数 pageSize:单页文件数  curPageNo当前第几页
	public static native ReplayFile[] netGetVideoListAll(int count);
	public static native void netCloseVideoList();


	public static native void getHI265Version();

	//transmission
	
	public static native void transInit(String ip,int port);
	public static native void transSetCallBackObj(Object o,int flag);
	public static native void transSetCallbackMethodName(String methodName,int flag);
	public static native void transDeinit();
	public static native void transConnect(int type,String id,String name,String pwd);
	public static native void transDisconnect();
	public static native void transSubscribe(String jsonStr,int jsonLen);
	public static native void transUnsubscribe();
	public static native void catchPic(String path);
	public static native int transGetStreamLenSomeTime();
	public static native void transGetCam(String jsonStr,int jsonLen);
	public static native void transGetRecordFiles(String jsonStr,int jsonLen);
	public static native void transSetCrt(String ca,String client,String key);
	public static native void transSetCrtPaht(String caPath,String clientPath,String keyPath);
	
	//turn
	public static native void turnInputViewData(byte [] data,int len);


	//ecam
	public static native void ecamInit(String account);
	public static native void ecamDeinit();
	public static native void ecamSetCallbackObj(Object obj,int flag);
	public static native void ecamSetContextObj(StreamReqContext obj);
	public static native int ecamGetAudioType();//return:-1:error;0:aac;1:g711u
	public static native String ecamPrepareSDP();
	public static native void ecamHandleRemoteSDP(String dialogID,String remoteSDP);
	public static native int ecamStart();//return:0 ok; -1 :error; -2:timeout
	public static native int ecamStop();
	public static native int ecamGetMethod();//retrun 0:other; 1:turn;2sturn;3:other;-1 error
	public static native long []ecamGetSdpTime();//return long[0]:begtime ,long[1]:endTime;
	public static native int ecamSendAudioData(byte [] bytes,int len);
	public static native int ecamGetStreamLenSomeTime();
}
