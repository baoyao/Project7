#include <fcntl.h>
#include <asm-generic/ioctl.h>
#include "fflog.h"

#define FM_IOCTL_POWERDOWN	_IOW('T', 0x41, int) /* Get exclusive mode state */
#define FM_IOCTL_POWERUP	_IOW('T', 0x42, int)
#define FM_IOCTL_FM_TUNE_TO	_IOW('T', 0x43, int)
#define FM_IOCTL_SET_MUTE	_IOW('T', 0x44, int)
#define FM_IOCTL_SET_VOL	_IOW('T', 0x45, int)
#define FM_IOCTL_SEEK	    _IOW('T', 0x46, int)

//typedef void (*callback)(int status, int level, int usn, int wam, int offset, int modulation);

int fd = -1;
JNIEXPORT void JNICALL
Java_com_example_fmutil_FMUtil_openDevice(JNIEnv *env, jclass type, jstring path_)
{
    const char *path = (*env)->GetStringUTFChars(env, path_, 0);
    if(fd < 0) {
        fd = open(path, O_RDWR);
        if(fd < 0) {
            LOGFE("can not open %s", path);
        }
    }
    (*env)->ReleaseStringUTFChars(env, path_, path);
}

JNIEXPORT void JNICALL
Java_com_example_fmutil_FMUtil_closeDevice(JNIEnv *env, jclass type)
{
    BEGIN
    if(fd < 0) {
        LOGFE("Error!!! fd(%d)", fd);
        return;
    }
    close(fd);
    fd = -1;
    END
}

JNIEXPORT void JNICALL
Java_com_example_fmutil_FMUtil_powerOn(JNIEnv *env, jclass type, jint arg)
{
    BEGIN
    if(fd < 0) {
        LOGFE("Error!!! fd(%d)", fd);
        return;
    }
    int ret = ioctl(fd, FM_IOCTL_POWERUP, &arg);
    if(ret < 0) {
        LOGFE("FM_IOCTL_POWERUP failed\n");
    }
    END

}

JNIEXPORT void JNICALL
Java_com_example_fmutil_FMUtil_powerDown(JNIEnv *env, jclass type, jint arg)
{
    BEGIN
    if(fd < 0) {
        LOGFE("Error!!! fd(%d)", fd);
        return;
    }
    int ret = ioctl(fd, FM_IOCTL_POWERDOWN, &arg);
    if(ret < 0) {
        LOGFE("FM_IOCTL_POWERDOWN failed\n");
    }
    END
}

JNIEXPORT void JNICALL
Java_com_example_fmutil_FMUtil_tune(JNIEnv *env, jclass type, jint arg)
{
    BEGIN
    if(fd < 0) {
        LOGFE("Error!!! fd(%d)", fd);
        return;
    }
    int ret = ioctl(fd, FM_IOCTL_FM_TUNE_TO, &arg);
    if(ret < 0) {
        LOGFE("FM_IOCTL_FM_TUNE_TO failed\n");
    }
    END
}

JNIEXPORT void JNICALL
Java_com_example_fmutil_FMUtil_setMute(JNIEnv *env, jclass type, jint arg)
{

    BEGIN
    if(fd < 0) {
        LOGFE("Error!!! fd(%d)", fd);
        return;
    }
    int ret = ioctl(fd, FM_IOCTL_SET_MUTE, &arg);
    if(ret < 0) {
        LOGFE("FM_IOCTL_SET_MUTE failed\n");
    }
    END

}

JNIEXPORT void JNICALL
Java_com_example_fmutil_FMUtil_setVolume(JNIEnv *env, jclass type, jint arg)
{

    BEGIN
    if(fd < 0) {
        LOGFE("Error!!! fd(%d)", fd);
        return;
    }
    int ret = ioctl(fd, FM_IOCTL_SET_VOL, &arg);
    if(ret < 0) {
        LOGFE("FM_IOCTL_SET_VOL failed\n");
    }
    END

}

JNIEXPORT void JNICALL
Java_com_example_fmutil_FMUtil_seek(JNIEnv *env, jclass type, jint arg)
{
    BEGIN
    if(fd < 0) {
        LOGFE("Error!!! fd(%d)", fd);
        return;
    }
    int ret = ioctl(fd, FM_IOCTL_SEEK, &arg);
    if(ret < 0) {
        LOGFE("FM_IOCTL_SEEK failed\n");
    }
    END
}

JNIEXPORT jbyteArray JNICALL
Java_com_example_fmutil_FMUtil_read(JNIEnv *env, jclass type)
{
    BEGIN
    if(fd < 0) {
        LOGFE("Error!!! fd(%d)", fd);
        return NULL;
    }
    unsigned char data[10] = {0};
    int ret = read(fd, data, 10);
    LOGD("+++++++++++++++++++++++");
    LOGD("read result length(%d)",ret);
    for(int i=0; i < ret; i++) {
        LOGD("read [%d] --> [0x%x]", i, data[i]);
    }
    LOGD("-----------------------");
    jbyteArray result = (*env)->NewByteArray(env, ret);
    (*env)->SetByteArrayRegion(env, result, 0, ret, (jbyte *)data);
    END
    return result;
}