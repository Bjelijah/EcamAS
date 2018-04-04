#ifndef __XHWWaveDataSDK__H__
#define __XHWWaveDataSDK__H__

#ifdef __cplusplus
extern "C" {
#endif
int32_t XHWWaveSndSDK_start(void);
int32_t XHWWaveSndSDK_stop(void);
int32_t XHWWaveSndSDK_GenWave(const char* sendbuf,int len,char** pWaveData,int* nlen);
int32_t XHWWaveSndSDK_SendData(const char* sendbuf,int len);
int32_t XHWWaveSndSDK_StopSend(void);
#ifdef __cplusplus
}
#endif

#endif
