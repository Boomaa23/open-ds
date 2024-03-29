/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_boomaa_opends_usb_LinuxController */

#ifndef _Included_com_boomaa_opends_usb_LinuxController
#define _Included_com_boomaa_opends_usb_LinuxController
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    open
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_com_boomaa_opends_usb_LinuxController_open
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    getNumAxes
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_LinuxController_getNumAxes
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    getNumButtons
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_LinuxController_getNumButtons
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    poll
 * Signature: (J)Lcom/boomaa/opends/usb/LinuxJSEvent;
 */
JNIEXPORT jobject JNICALL Java_com_boomaa_opends_usb_LinuxController_poll
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    getName
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_boomaa_opends_usb_LinuxController_getName
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_boomaa_opends_usb_LinuxController
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_boomaa_opends_usb_LinuxController_close
  (JNIEnv *, jobject, jlong);

#ifdef __cplusplus
}
#endif
#endif
