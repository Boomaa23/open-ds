/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_boomaa_opends_usb_IOKitDevice */

#ifndef _Included_com_boomaa_opends_usb_IOKitDevice
#define _Included_com_boomaa_opends_usb_IOKitDevice
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_boomaa_opends_usb_IOKitDevice
 * Method:    open
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_IOKitDevice_open
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_boomaa_opends_usb_IOKitDevice
 * Method:    getDeviceProperties
 * Signature: (J)Ljava/util/Map;
 */
JNIEXPORT jobject JNICALL Java_com_boomaa_opends_usb_IOKitDevice_getDeviceProperties
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_boomaa_opends_usb_IOKitDevice
 * Method:    getElementValue
 * Signature: (JJ)Lcom/boomaa/opends/usb/IOKitEvent;
 */
JNIEXPORT jobject JNICALL Java_com_boomaa_opends_usb_IOKitDevice_getElementValue
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     com_boomaa_opends_usb_IOKitDevice
 * Method:    release
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_IOKitDevice_release
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     com_boomaa_opends_usb_IOKitDevice
 * Method:    close
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_IOKitDevice_close
  (JNIEnv *, jobject, jlong);

#ifdef __cplusplus
}
#endif
#endif