

#include <fcntl.h>
#include <asm-generic/ioctl.h>
#include "fflog.h"

#define FM_IOCTL_POWERDOWN	_IOW('T', 0x41, int) /* Get exclusive mode state */
#define FM_IOCTL_POWERUP	_IOW('T', 0x42, int)
#define FM_IOCTL_FM_TUNE_TO	_IOW('T', 0x43, int)
#define FM_IOCTL_SET_MUTE	_IOW('T', 0x44, int)
#define FM_IOCTL_SET_VOL	_IOW('T', 0x45, int)
#define FM_IOCTL_SEEK	    _IOW('T', 0x46, int)

int fd = -1;
JNIEXPORT void JNICALL
Java_com_example_fmutil_FMUtil_openDevice(JNIEnv *env, jobject instance, jstring path_)
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
Java_com_example_fmutil_FMUtil_closeDevice(JNIEnv *env, jobject instance)
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
Java_com_example_fmutil_FMUtil_powerOn(JNIEnv *env, jobject instance, jint arg)
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
Java_com_example_fmutil_FMUtil_powerDown(JNIEnv *env, jobject instance, jint arg)
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
Java_com_example_fmutil_FMUtil_tune(JNIEnv *env, jobject instance, jint arg)
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
Java_com_example_fmutil_FMUtil_setMute(JNIEnv *env, jobject instance, jint arg)
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
Java_com_example_fmutil_FMUtil_setVolume(JNIEnv *env, jobject instance, jint arg)
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
Java_com_example_fmutil_FMUtil_seek(JNIEnv *env, jobject instance, jint arg)
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