//
// Created by Administrator on 2016/12/14.
//
#include <jni.h>
#include <android/log.h>

#include <pthread.h>
#include <semaphore.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "JNI", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "JNI", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "JNI", __VA_ARGS__))

typedef struct YV12glDisplay
{
    char * y;
    char * u;
    char * v;
    unsigned long long time;
    int width;
    int height;
    int enable;
    int is_catch_picture;
    char path[50];
    /* multi thread */
    int method_ready;
    JavaVM * jvm;
    JNIEnv * env;
    jmethodID mid,mSetTime;
    jobject obj;
    pthread_mutex_t lock;
    sem_t over_sem;
    sem_t over_ret_sem;
    int lock_ret;
}YV12_display_t;

static YV12_display_t self;










