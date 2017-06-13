#ifndef __FFLOG_H__
#define __FFLOG_H__
#include <stdio.h>
#include <unistd.h>
#include <libgen.h>
#include <sys/time.h>

static unsigned long getTimestampMS()
{
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return (unsigned long)(tv.tv_usec*1e-3 + tv.tv_sec*1000);
}

static unsigned long getTimestamp()
{
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return (unsigned long)(tv.tv_usec*1e-6) + (unsigned long)tv.tv_sec;
}

#ifdef __ANDROID__

#include <android/log.h>
#include <jni.h>

#ifndef TAG
#define TAG "yzh"
#endif

//#define printf(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG ,  TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO ,   TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN ,   TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR ,  TAG, __VA_ARGS__)

#define LOGFV(x, ...) LOGV("[ %s | %s | %d ] "x, basename(__FILE__), __FUNCTION__,__LINE__, ##__VA_ARGS__)
#define LOGFD(x, ...) LOGD("[ %s | %s | %d ] "x, basename(__FILE__), __FUNCTION__,__LINE__, ##__VA_ARGS__)
#define LOGFI(x, ...) LOGI("[ %s | %s | %d ] "x, basename(__FILE__), __FUNCTION__,__LINE__, ##__VA_ARGS__)
#define LOGFW(x, ...) LOGW("[ %s | %s | %d ] "x, basename(__FILE__), __FUNCTION__,__LINE__, ##__VA_ARGS__)
#define LOGFE(x, ...) LOGE("[ %s | %s | %d ] "x, basename(__FILE__), __FUNCTION__,__LINE__, ##__VA_ARGS__)

#ifndef CAPI
#ifdef __cplusplus
#define CAPI extern "C"
#else
#define CAPI
#endif
#endif

CAPI static int jniThrowException(JNIEnv *env, const char *className, const char *msg)
{
    
    if ((*env)->ExceptionCheck(env)) {
        /* consider creating the new exception with this as "cause" */
        (*env)->ExceptionOccurred(env);
        (*env)->ExceptionClear(env);
    }
    
    jclass jcls = (*env)->FindClass(env, className);
    if (jcls == NULL) {
        LOGE("Unable to find exception class %s", className);
        /* ClassNotFoundException now pending */
        return -1;
    }
    
    if ((*env)->ThrowNew(env, jcls, msg) != JNI_OK) {
        LOGE("Failed throwing '%s' '%s'", className, msg);
        /* an exception, most likely OOM, will now be pending */
        return -1;
    }
    return 0;
}

#endif // end of __ANDROID__

#ifdef __APPLE__

#define LOG(format, ...) \
do { \
    fprintf(stderr, "[%s|%s|%d]:" format "\n", basename(__FILE__), __FUNCTION__, __LINE__, ##__VA_ARGS__ ); \
} while (0)

#define LOGV(x,...) printf(x"\n",##__VA_ARGS__)
#define LOGD(x,...) printf(x"\n",##__VA_ARGS__)
#define LOGI(x,...) printf(x"\n",##__VA_ARGS__)
#define LOGW(x,...) printf(x"\n",##__VA_ARGS__)
#define LOGE(x,...) printf(x"\n",##__VA_ARGS__)

#define LOGFV LOG
#define LOGFD LOG
#define LOGFI LOG
#define LOGFW LOG
#define LOGFE LOG

#endif  //end of __APPLE__

#define BEGIN LOGFD(" + ");
#define END   LOGFD(" - ");

#define CHECK_NULL(p)  \
{ \
    if(p == NULL) { \
        LOGFD("error : null-pointer!"); \
        return; \
    } \
}

#define CHECK_NULL_INFO(p, info)  \
{ \
    if(p == NULL) { \
        LOGFE("error : null-pointer! info:%s", info); \
        return; \
    } \
}

#define CHECK_NULL_R(p, r)  \
{ \
    if(p == NULL) { \
        LOGFD("error : null-pointer!"); \
        return r; \
    } \
}

#define CHECK_NULL_INFO_R(p, info, r)  \
{ \
    if(p == NULL) { \
        LOGFD("error : null-pointer! info:%s", info); \
        return r; \
    } \
}

#endif

