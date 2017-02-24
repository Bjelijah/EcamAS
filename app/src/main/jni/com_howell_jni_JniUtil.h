/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_howell_jni_JniUtil */

#ifndef _Included_com_howell_jni_JniUtil
#define _Included_com_howell_jni_JniUtil
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_howell_jni_JniUtil
 * Method:    YUVInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVInit
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    YUVDeinit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVDeinit
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    YUVSetCallbackObject
 * Signature: (Ljava/lang/Object;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVSetCallbackObject
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    YUVSetCallbackMethodName
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVSetCallbackMethodName
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    YUVLock
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVLock
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    YUVUnlock
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVUnlock
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    YUVSetEnable
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVSetEnable
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    YUVRenderY
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVRenderY
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    YUVRenderU
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVRenderU
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    YUVRenderV
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_YUVRenderV
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    nativeAudioInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioInit
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    nativeAudioDeinit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioDeinit
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    nativeAudioSetCallbackObject
 * Signature: (Ljava/lang/Object;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioSetCallbackObject
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    nativeAudioSetCallbackMethodName
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioSetCallbackMethodName
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    nativeAudioBPlayable
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioBPlayable
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    nativeAudioStop
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_nativeAudioStop
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    nativeAudioSetdata
 * Signature: ([BI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_nativeAudioSetdata
  (JNIEnv *, jclass, jbyteArray, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_netInit
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netDeinit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_netDeinit
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    login
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_login
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    loginOut
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_loginOut
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    setCallBackObj
 * Signature: (Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_setCallBackObj
  (JNIEnv *, jclass, jobject);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    setPlayBackTime
 * Signature: (Lcom/howell/bean/ApTimeBean;Lcom/howell/bean/ApTimeBean;)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_setPlayBackTime
  (JNIEnv *, jclass, jobject, jobject);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netReadyPlay
 * Signature: (IIII)Z
 */
JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_netReadyPlay
  (JNIEnv *, jclass, jint, jint, jint, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    readyPlay
 * Signature: (Ljava/lang/Object;I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_readyPlay
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    releasePlay
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_releasePlay
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netStopPlay
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_netStopPlay
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    stopView
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_stopView
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    playView
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_playView
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netGetStreamLenSomeTime
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_jni_JniUtil_netGetStreamLenSomeTime
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    pause
 * Signature: (Z)Z
 */
JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_pause
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    getCurPlayTimestamp
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_howell_jni_JniUtil_getCurPlayTimestamp
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    getBegPlayTimestamp
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_howell_jni_JniUtil_getBegPlayTimestamp
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    keepTimestamp
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_keepTimestamp
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netPtzMove
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_netPtzMove
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netPtzCam
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_netPtzCam
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netPtzIris
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_netPtzIris
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netGetVideoListCount
 * Signature: (Lcom/howell/bean/ApTimeBean;Lcom/howell/bean/ApTimeBean;)I
 */
JNIEXPORT jint JNICALL Java_com_howell_jni_JniUtil_netGetVideoListCount
  (JNIEnv *, jclass, jobject, jobject);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netGetVideoListPageCount
 * Signature: (Lcom/howell/bean/ApTimeBean;Lcom/howell/bean/ApTimeBean;II)I
 */
JNIEXPORT jint JNICALL Java_com_howell_jni_JniUtil_netGetVideoListPageCount
  (JNIEnv *, jclass, jobject, jobject, jint, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netGetVideoListAll
 * Signature: (I)[Lcom/howell/bean/ReplayFile;
 */
JNIEXPORT jobjectArray JNICALL Java_com_howell_jni_JniUtil_netGetVideoListAll
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    netCloseVideoList
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_netCloseVideoList
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    getHI265Version
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_getHI265Version
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transInit
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transInit
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transSetCallBackObj
 * Signature: (Ljava/lang/Object;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transSetCallBackObj
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transSetCallbackMethodName
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transSetCallbackMethodName
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transSetUseSSL
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transSetUseSSL
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transDeinit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transDeinit
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transConnect
 * Signature: (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_howell_jni_JniUtil_transConnect
  (JNIEnv *, jclass, jint, jstring, jstring, jstring);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transDisconnect
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transDisconnect
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transSubscribe
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transSubscribe
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transUnsubscribe
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transUnsubscribe
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    catchPic
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_catchPic
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transGetStreamLenSomeTime
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_jni_JniUtil_transGetStreamLenSomeTime
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transGetCam
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transGetCam
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transGetRecordFiles
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transGetRecordFiles
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transSetCrt
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transSetCrt
  (JNIEnv *, jclass, jstring, jstring, jstring);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    transSetCrtPaht
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_transSetCrtPaht
  (JNIEnv *, jclass, jstring, jstring, jstring);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    turnInputViewData
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_turnInputViewData
  (JNIEnv *, jclass, jbyteArray, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamInit
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_ecamInit
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamDeinit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_ecamDeinit
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamSetCallbackObj
 * Signature: (Ljava/lang/Object;I)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_ecamSetCallbackObj
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamSetContextObj
 * Signature: (Lcom/howell/entityclass/StreamReqContext;)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_ecamSetContextObj
  (JNIEnv *, jclass, jobject);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamGetAudioType
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_jni_JniUtil_ecamGetAudioType
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamPrepareSDP
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_howell_jni_JniUtil_ecamPrepareSDP
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamHandleRemoteSDP
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_howell_jni_JniUtil_ecamHandleRemoteSDP
  (JNIEnv *, jclass, jstring, jstring);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamStart
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_jni_JniUtil_ecamStart
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamStop
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_jni_JniUtil_ecamStop
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamGetMethod
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_jni_JniUtil_ecamGetMethod
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamGetSdpTime
 * Signature: ()[J
 */
JNIEXPORT jlongArray JNICALL Java_com_howell_jni_JniUtil_ecamGetSdpTime
  (JNIEnv *, jclass);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamSendAudioData
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_com_howell_jni_JniUtil_ecamSendAudioData
  (JNIEnv *, jclass, jbyteArray, jint);

/*
 * Class:     com_howell_jni_JniUtil
 * Method:    ecamGetStreamLenSomeTime
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_howell_jni_JniUtil_ecamGetStreamLenSomeTime
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
