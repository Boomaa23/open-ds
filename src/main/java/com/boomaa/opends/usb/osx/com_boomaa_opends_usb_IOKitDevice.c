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

typedef struct {
	JNIEnv *env;
	jobjectArray array;
	jsize index;
} array_context_t;

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

jobject createMapFromCFDictionary(JNIEnv *env, CFDictionaryRef dict);

static jobject createObjectFromCFObject(JNIEnv *env, CFTypeRef cfobject);

static jstring createStringFromCFString(JNIEnv *env, CFStringRef cfstring) {
	CFIndex unicode_length = CFStringGetLength(cfstring);
	CFIndex utf8_length = CFStringGetMaximumSizeForEncoding(unicode_length, kCFStringEncodingUTF8);
	// Allocate buffer large enough, plus \0 terminator
	char *buffer = (char *)malloc(utf8_length + 1);
	if (buffer == NULL)
		return NULL;
	Boolean result = CFStringGetCString(cfstring, buffer, utf8_length + 1, kCFStringEncodingUTF8);
	if (!result) {
		free(buffer);
		return NULL;
	}
	jstring str = (*env)->NewStringUTF(env, buffer);
	free(buffer);
	return str;
}

static jobject createDoubleObjectFromCFNumber(JNIEnv *env, CFNumberRef cfnumber) {
	double value;
	Boolean result = CFNumberGetValue(cfnumber, kCFNumberDoubleType, &value);
	if (!result)
		return NULL;
	return newJObject(env, "java/lang/Double", "(D)V", (jdouble)value); 
}

static jobject createLongObjectFromCFNumber(JNIEnv *env, CFNumberRef cfnumber) {
	SInt64 value;
	Boolean result = CFNumberGetValue(cfnumber, kCFNumberSInt64Type, &value);
	if (!result)
		return NULL;
	return newJObject(env, "java/lang/Long", "(J)V", (jlong)value); 
}

static jobject createNumberFromCFNumber(JNIEnv *env, CFNumberRef cfnumber) {
	CFNumberType number_type = CFNumberGetType(cfnumber);
	switch (number_type) {
		case kCFNumberSInt8Type:
		case kCFNumberSInt16Type:
		case kCFNumberSInt32Type:
		case kCFNumberSInt64Type:
		case kCFNumberCharType:
		case kCFNumberShortType:
		case kCFNumberIntType:
		case kCFNumberLongType:
		case kCFNumberLongLongType:
		case kCFNumberCFIndexType:
			return createLongObjectFromCFNumber(env, cfnumber);
		case kCFNumberFloat32Type:
		case kCFNumberFloat64Type:
		case kCFNumberFloatType:
		case kCFNumberDoubleType:
			return createDoubleObjectFromCFNumber(env, cfnumber);
		default:
			return NULL;
	}
}

static void createArrayEntries(const void *value, void *context) {
	array_context_t *array_context = (array_context_t *)context;
	jobject jval = createObjectFromCFObject(array_context->env, value);
	(*array_context->env)->SetObjectArrayElement(array_context->env, array_context->array, array_context->index++, jval);
	(*array_context->env)->DeleteLocalRef(array_context->env, jval);
}

static jobject createArrayFromCFArray(JNIEnv *env, CFArrayRef cfarray) {
	jclass Object_class = (*env)->FindClass(env, "java/lang/Object");
	if (Object_class == NULL)
		return NULL;
	CFIndex size = CFArrayGetCount(cfarray);
	CFRange range = {0, size};
	jobjectArray array = (*env)->NewObjectArray(env, size, Object_class, NULL);
	array_context_t array_context;
	array_context.env = env;
	array_context.array = array;
	array_context.index = 0;
	CFArrayApplyFunction(cfarray, range, createArrayEntries, &array_context);
	return array;
}

static jobject createObjectFromCFObject(JNIEnv *env, CFTypeRef cfobject) {
	CFTypeID type_id = CFGetTypeID(cfobject);
	if (type_id == CFDictionaryGetTypeID()) {
		return createMapFromCFDictionary(env, cfobject);
	} else if (type_id == CFArrayGetTypeID()) {
		return createArrayFromCFArray(env, cfobject);
	} else if (type_id == CFStringGetTypeID()) {
		return createStringFromCFString(env, cfobject);
	} else if (type_id == CFNumberGetTypeID()) {
		return createNumberFromCFNumber(env, cfobject);
	} else {
		return NULL;
	}
}

static void createMapKeys(const void *key, const void *value, void *context) {
	dict_context_t *dict_context = (dict_context_t *)context;

	jclass Map_class = (*dict_context->env)->GetObjectClass(dict_context->env, dict_context->map);
	if (Map_class == NULL)
		return;
	jmethodID map_put = (*dict_context->env)->GetMethodID(dict_context->env, Map_class, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
	if (map_put == NULL)
		return;
	jobject jkey = createObjectFromCFObject(dict_context->env, key);
	jobject jvalue = createObjectFromCFObject(dict_context->env, value);
	if (jkey == NULL || jvalue == NULL)
		return;
	(*dict_context->env)->CallObjectMethod(dict_context->env, dict_context->map, map_put, jkey, jvalue);
	(*dict_context->env)->DeleteLocalRef(dict_context->env, jkey);
	(*dict_context->env)->DeleteLocalRef(dict_context->env, jvalue);
}

jobject createMapFromCFDictionary(JNIEnv *env, CFDictionaryRef dict) {
	jobject map = newJObject(env, "java/util/HashMap", "()V");
	if (map == NULL)
		return NULL;
	dict_context_t dict_context;
	dict_context.env = env;
	dict_context.map = map;
	CFDictionaryApplyFunction(dict, createMapKeys, &dict_context);
	return map;
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
	io_object_t hidDevice = (io_object_t)address;
	CFMutableDictionaryRef properties;

	kern_return_t result = IORegistryEntryCreateCFProperties(hidDevice, &properties, kCFAllocatorDefault, kNilOptions);
	if (result != KERN_SUCCESS) {
		printf("Failed to create properties for device (%d)\n", result);
		return NULL;
	}
	jobject map = newJObject(env, "java/util/HashMap", "()V");
    if (map == NULL) {
        return NULL;
    }
    dict_context_t dict_context;
    dict_context.env = env;
    dict_context.map = map;
    CFDictionaryApplyFunction(properties, createMapKeys, &dict_context);
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
  (JNIEnv *env, jobject obj, jlong iface_addr, jlong hidCookie) {
    IOHIDDeviceInterface **hidDeviceInterface = (IOHIDDeviceInterface **)(intptr_t)iface_addr;
    IOHIDElementCookie cookie = (IOHIDElementCookie)(intptr_t)hidCookie;
    IOHIDEventStruct event;

    IOReturn ioReturnValue = (*hidDeviceInterface)->getElementValue(hidDeviceInterface, cookie, &event);
    if (ioReturnValue != kIOReturnSuccess) {
        printf("Device getElementValue failed: %d\n", ioReturnValue);
        return NULL;
    }
    if (event.longValue != NULL) {
        free(event.longValue);
    }
    return newJObject(env, "com/boomaa/opends/usb/IOKitEvent", "(JJI)V",
                    (jlong)event.type,
                    (jlong)(intptr_t)event.elementCookie,
                    (jint)event.value);
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
    (*device_interface)->Release(device_interface);
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
