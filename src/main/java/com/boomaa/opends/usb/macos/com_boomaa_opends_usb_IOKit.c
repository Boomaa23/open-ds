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
#include "com_boomaa_opends_usb_IOKitDevice.h"

#ifndef _Included_com_boomaa_opends_usb_IOKit
#define _Included_com_boomaa_opends_usb_IOKit
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_boomaa_opends_usb_IOKit
 * Method:    createIterator
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_boomaa_opends_usb_IOKit_createIterator
  (JNIEnv *env, jobject obj) {
    io_iterator_t hidObjectIterator;
    CFMutableDictionaryRef hidMatchDictionary = IOServiceMatching(kIOHIDDeviceKey);
    // kIOMasterPortDefault --> kIOMainPortDefault eventually
    IOReturn ioRtn = IOServiceGetMatchingServices(kIOMasterPortDefault, hidMatchDictionary, &hidObjectIterator);

    if (ioRtn != kIOReturnSuccess) {
		printf("Failed to create iterator (%d)\n", ioRtn);
		return -1;
	}

	if (hidObjectIterator == IO_OBJECT_NULL) {
		printf("Failed to create iterator\n");
		return -1;
	}
	return (jlong) hidObjectIterator;
}

/*
 * Class:     com_boomaa_opends_usb_IOKit
 * Method:    next
 * Signature: (J)Lcom/boomaa/opends/usb/IOKitDevice {
}
 */
JNIEXPORT jobject JNICALL Java_com_boomaa_opends_usb_IOKit_next
  (JNIEnv *env, jobject obj, jlong address) {
    io_iterator_t iterator = (io_iterator_t)address;
    io_object_t hidDevice;
    hidDevice = IOIteratorNext(iterator);
	if (hidDevice == MACH_PORT_NULL)
		return NULL;

    IOHIDDeviceInterface **device_interface;
    IOCFPlugInInterface **plugInInterface;
    SInt32 score;
    IOReturn ioReturnValue = IOCreatePlugInInterfaceForService(hidDevice, kIOHIDDeviceUserClientTypeID,
                                                            kIOCFPlugInInterfaceID, &plugInInterface, &score);

    if (ioReturnValue != kIOReturnSuccess) {
        printf("Couldn't create plugin for device interface (%d)\n", ioReturnValue);
        IOObjectRelease(hidDevice);
        return NULL;
    }
    HRESULT plugInResult = (*plugInInterface)->QueryInterface(plugInInterface,
            CFUUIDGetUUIDBytes(kIOHIDDeviceInterfaceID),
            (LPVOID)&device_interface);
    (*plugInInterface)->Release(plugInInterface);
    if (plugInResult != S_OK) {
        printf("Couldn't create HID class device interface (%d)\n", plugInResult);
        IOObjectRelease(hidDevice);
        return NULL;
    }
    jobject device_object = newJObject(env, "com/boomaa/opends/usb/IOKitDevice", "(JJ)V", (jlong)hidDevice, (jlong)(intptr_t)device_interface);
	if (device_object == NULL) {
		(*device_interface)->Release(device_interface);
		IOObjectRelease(hidDevice);
		return NULL;
	}
	return device_object;
}

/*
 * Class:     com_boomaa_opends_usb_IOKit
 * Method:    close
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_IOKit_close
  (JNIEnv *env, jobject obj, jlong address) {
    io_iterator_t iterator = (io_iterator_t) address;
    return (jint) IOObjectRelease(iterator);
}

#ifdef __cplusplus
}
#endif
#endif
