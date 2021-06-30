#include <jni.h>
#include <linux/joystick.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>

#ifndef _Included_com_boomaa_opends_usb_LinuxController
#define _Included_com_boomaa_opends_usb_LinuxController
#ifdef __cplusplus
extern "C" {
#endif

jobject newJObject(JNIEnv *env, const char *class_name, const char *constructor_signature, ...) {
    va_list ap;
    jclass clazz;
    jmethodID constructor;
    jobject obj;

    clazz = (*env)->FindClass(env, class_name);
    if (clazz == NULL)
        return NULL;
    constructor = (*env)->GetMethodID(env, clazz, "<init>", constructor_signature);
    if (constructor == NULL)
        return NULL;
            va_start(ap, constructor_signature);
    obj = (*env)->NewObjectV(env, clazz, constructor, ap);
            va_end(ap);
    return obj;
}

/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    open
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_com_boomaa_opends_usb_LinuxController_open
  (JNIEnv *env, jobject obj, jint idx) {
    char path[16];
    sprintf(path, "/dev/input/js%i", idx);
    return open(path, O_NONBLOCK);
}

/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    getNumAxes
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_LinuxController_getNumAxes
  (JNIEnv *env, jobject obj, jlong address) {
    char num_axes;
    ioctl((int) address, JSIOCGAXES, &num_axes);
    return (jint) num_axes;
}

/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    getNumButtons
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_LinuxController_getNumButtons
  (JNIEnv *env, jobject obj, jlong address) {
    char num_buttons;
    ioctl((int) address, JSIOCGBUTTONS, &num_buttons);
    return (jint) num_buttons;
}

/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    poll
 * Signature: (J)Lcom/boomaa/opends/usb/LinuxJSEvent;
 */
JNIEXPORT jobject JNICALL Java_com_boomaa_opends_usb_LinuxController_poll
  (JNIEnv *env, jobject obj, jlong address) {
    struct js_event event;
    ssize_t data;
    data = read((int) address, &event, sizeof(event));
    if (data == sizeof(event)) {
        return newJObject(env, "com/boomaa/opends/usb/LinuxJSEvent", "(IBI)V", (&event)->value, (&event)->type, (&event)->number);
    } else if (errno != 0 && errno != EAGAIN) {
        return newJObject(env, "com/boomaa/opends/usb/LinuxJSEvent", "(IBI)V", -1, -1, -1);
    } else {
        return NULL;
    }
}

/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    getName
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_boomaa_opends_usb_LinuxController_getName
  (JNIEnv *env, jobject obj, jlong address) {
    char name[128];
    if (ioctl((int) address, JSIOCGNAME(sizeof(name)), name) < 0) {
        strncpy(name, "Unknown", sizeof(name));
    }
    return (*env)->NewStringUTF(env, name);
}

/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_boomaa_opends_usb_LinuxController_close
  (JNIEnv *env, jobject obj, jlong address) {
    close((int) address);
}

#ifdef __cplusplus
}
#endif
#endif
