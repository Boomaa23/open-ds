#include <jni.h>
#include <IOKit/IOTypes.h>
#include <IOKit/IOKitLib.h>
#include <IOKit/IOCFPlugIn.h>
#include <IOKit/hid/IOHIDLib.h>
#include <IOKit/hid/IOHIDKeys.h>
#include <CoreFoundation/CoreFoundation.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#ifndef _Included_com_boomaa_opends_usb_IOKitDevice
#define _Included_com_boomaa_opends_usb_IOKitDevice
#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
	JNIEnv *env;
	jobject map;
} dict_context_t;

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
 * Class:     com_boomaa_opends_usb_IOKitDevice
 * Method:    open
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_IOKitDevice_open
  (JNIEnv *env, jobject obj, jlong iface_addr) {
    IOHIDDeviceInterface **hidDevIface = (IOHIDDeviceInterface **)(intptr_t)iface_addr;
    IOReturn ioRtn = (*hidDevIface)->open(hidDevIface, 0);
    return (jint) ioRtn;
}

/*
 * Class:     com_boomaa_opends_usb_IOKitDevice
 * Method:    getDeviceProperties
 * Signature: (J)Ljava/util/Map {
}
 */
JNIEXPORT jobject JNICALL Java_com_boomaa_opends_usb_IOKitDevice_getDeviceProperties
  (JNIEnv *env, jobject obj, jlong address) {
	io_object_t hidDevice = (io_object_t)device_address;
	CFMutableDictionaryRef properties;

	kern_return_t result = IORegistryEntryCreateCFProperties(hidDevice, &properties, kCFAllocatorDefault, kNilOptions);
	if (result != KERN_SUCCESS) {
		throwIOException(env, "Failed to create properties for device (%ld)", result);
		return NULL;
	}
	jobject map = newJObject(env, "java/util/HashMap", "()V");
    if (map == NULL) {
        return NULL;
    }
    dict_context_t dict_context;
    dict_context.env = env;
    dict_context.map = map;
    CFDictionaryApplyFunction(dict, createMapKeys, &dict_context);
	CFRelease(properties);
	return map;
}

/*
 * Class:     com_boomaa_opends_usb_IOKitDevice
 * Method:    getElementValue
 * Signature: (JJ)Lcom/boomaa/opends/usb/IOKitEvent {
}
 */
JNIEXPORT jobject JNICALL Java_com_boomaa_opends_usb_IOKitDevice_getElementValue
  (JNIEnv *env, jobject obj, jlong iface_addr, jlong cookie) {
    IOHIDDeviceInterface **hidDeviceInterface = (IOHIDDeviceInterface **)(intptr_t)lpDevice;
    IOHIDElementCookie cookie = (IOHIDElementCookie)(intptr_t)hidCookie;
    IOHIDEventStruct event;

    IOReturn ioReturnValue = (*hidDeviceInterface)->getElementValue(hidDeviceInterface, cookie, &event);
    if (ioReturnValue != kIOReturnSuccess) {
        throwIOException(env, "Device getElementValue failed: %d", ioReturnValue);
        return;
    }
    if (event.longValue != NULL) {
        free(event.longValue);
    }
    return newJObject(env, "com/boomaa/opends/usb/IOKitEvent", "(JJI)V",
                    (jlong)event->type
                    (jlong)(intptr_t)event->elementCookie,
                    (jint)event->value);
}

/*
 * Class:     com_boomaa_opends_usb_IOKitDevice
 * Method:    release
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_IOKitDevice_release
  (JNIEnv *env, jobject obj, jlong address, jlong iface_addr) {
    io_object_t hidDevice = (io_object_t)address;
    IOHIDDeviceInterface **device_interface = (IOHIDDeviceInterface **)(intptr_t)iface_addr;;
    (*device_interface)->Release(address);
    return (jint) IOObjectRelease(hidDevice);
}

/*
 * Class:     com_boomaa_opends_usb_IOKitDevice
 * Method:    close
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_IOKitDevice_close
  (JNIEnv *env, jobject obj, jlong iface_addr) {
    IOHIDDeviceInterface **hidDevIface = (IOHIDDeviceInterface **)(intptr_t)iface_addr;
    IOReturn ioRtn = (*hidDevIface)->close(hidDevIface);
    return (jint) ioRtn;
}

#ifdef __cplusplus
}
#endif
#endif
