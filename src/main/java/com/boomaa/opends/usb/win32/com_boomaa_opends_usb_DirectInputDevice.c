#include <windows.h>
#include <jni.h>
#include "win32util.h"
#include <dinput.h>
#include "com_boomaa_opends_usb_DirectInputDevice.h"

static jint numericGUID(const GUID *guid) {
    if (IsEqualGUID(guid, &GUID_XAxis)) {
		return 1;
    } else if (IsEqualGUID(guid, &GUID_YAxis)) {
        return 2;
    } else if (IsEqualGUID(guid, &GUID_ZAxis)) {
        return 3;
    } else if (IsEqualGUID(guid, &GUID_RxAxis)) {
        return 4;
    } else if (IsEqualGUID(guid, &GUID_RyAxis)) {
        return 5;
    } else if (IsEqualGUID(guid, &GUID_RzAxis)) {
        return 6;
    } else if (IsEqualGUID(guid, &GUID_Slider)) {
		return 7;
	} else if (IsEqualGUID(guid, &GUID_Button)) {
		return 8;
	} else if (IsEqualGUID(guid, &GUID_Key)) {
        return 9;
    } else if (IsEqualGUID(guid, &GUID_POV)) {
		return 10;
	} else {
		return 11;
    }
return 0;
}

static BOOL CALLBACK enumObjectsCallback(LPCDIDEVICEOBJECTINSTANCE lpddoi, LPVOID pvRef) {
    DIEnumContext *enumContext = (DIEnumContext *) pvRef;
    JNIEnv *env = enumContext->env;
    const GUID *guidType = &(lpddoi -> guidType);
    const DWORD dwType = lpddoi -> dwType;
    (*env) -> CallBooleanMethod(
            env,
            enumContext -> obj,
            (*env) -> GetMethodID(
                    env,
                    (*env) -> GetObjectClass(env, enumContext -> obj),
                    "addObject",
                    "([BIIIIILjava/lang/String;)V"
            ),
            wrapGUID(env, guidType),
            numericGUID(guidType),
            (jint) dwType,
            (jint) DIDFT_GETTYPE(dwType),
            (jint) DIDFT_GETINSTANCE(dwType),
            (jint) lpddoi->dwFlags,
            (*env) -> NewStringUTF(env, lpddoi -> tszName)
    );
    if ((*env) -> ExceptionOccurred(env)) {
        return DIENUM_STOP;
    }
    return DIENUM_CONTINUE;
}

static LPDIRECTINPUTDEVICE8 deviceFromAddress(long address) {
    return (LPDIRECTINPUTDEVICE8) (INT_PTR) address;
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    enumObjects
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_enumObjects
  (JNIEnv *env, jobject obj, jlong address, jint flags) {
    DIEnumContext enum_context;
    enum_context.env = env;
    enum_context.obj = obj;
    return IDirectInputDevice8_EnumObjects(deviceFromAddress(address), enumObjectsCallback, &enum_context, flags);
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    poll
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_poll
  (JNIEnv *env, jclass unused, jlong address) {
    return IDirectInputDevice8_Poll(deviceFromAddress(address));
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    acquire
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_acquire
  (JNIEnv *env, jclass unused, jlong address) {
    return IDirectInputDevice8_Acquire(deviceFromAddress(address));
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    unacquire
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_unacquire
  (JNIEnv *env, jclass unused, jlong address) {
    return IDirectInputDevice8_Unacquire(deviceFromAddress(address));
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    release
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_release
  (JNIEnv *env, jclass unused, jlong address) {
    return IDirectInputDevice8_Release(deviceFromAddress(address));
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    fetchDeviceState
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_fetchDeviceState
  (JNIEnv *env, jclass unused, jlong address, jintArray deviceStateArray) {
	jsize state_length = (*env)->GetArrayLength(env, deviceStateArray);
	DWORD state_size = state_length * sizeof(jint);
	HRESULT res;
	jint* device_state = (*env)->GetIntArrayElements(env, deviceStateArray, NULL);
	if (device_state == NULL)
		return -1;

	res = IDirectInputDevice8_GetDeviceState(deviceFromAddress(address), state_size, device_state);
	(*env)->ReleaseIntArrayElements(env, deviceStateArray, device_state, 0);
	return res;
    /*jint *device_state = (*env)->GetIntArrayElements(env, deviceStateArray, NULL);
    HRESULT res = IDirectInputDevice8_GetDeviceState(deviceFromAddress(address), (*env) -> GetArrayLength(env, deviceStateArray) * sizeof(jint), device_state);
    (*env) -> ReleaseIntArrayElements(env, deviceStateArray, device_state, 0);
    return res;*/
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    setDataFormat
 * Signature: (JI[Lcom/boomaa/opends/usb/input/DIDeviceObject;)I
 */
/*
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_setDataFormat
  (JNIEnv *env, jclass unused, jlong address, jint flags, jobjectArray objects) {
    LPDIRECTINPUTDEVICE8 lpDevice = deviceFromAddress(address);
    jsize numObjects = (*env) -> GetArrayLength(env, objects);

    DIDATAFORMAT dataFormat;
    dataFormat.dwSize = sizeof(DIDATAFORMAT);
    dataFormat.dwObjSize = sizeof(DIOBJECTDATAFORMAT);
    dataFormat.dwFlags = flags;
    dataFormat.dwDataSize = numObjects * sizeof(jint);
    dataFormat.dwNumObjs = numObjects;

    jclass clazz = (*env)->FindClass(env, "com/boomaa/opends/usb/input/DIDeviceObject");
    jmethodID getGUIDMethod = (*env)->GetMethodID(env, clazz, "getGUID", "()[B");
    jmethodID getTypeMethod = (*env)->GetMethodID(env, clazz, "getType", "()I");
    jmethodID getInstanceMethod = (*env)->GetMethodID(env, clazz, "getInstance", "()I");
    GUID *guids = (GUID *) malloc(numObjects * sizeof(GUID));
    DIOBJECTDATAFORMAT *objectFormats = (DIOBJECTDATAFORMAT *) malloc(numObjects * sizeof(DIOBJECTDATAFORMAT));

    jobject object;
    LPDIOBJECTDATAFORMAT objFormat;
    for (int i = 0; i < numObjects; i++) {
        object = (*env) -> GetObjectArrayElement(env, objects, i);
        unwrapGUID(env, (*env) -> CallObjectMethod(env, object, getGUIDMethod), guids + i);
        (*env)->DeleteLocalRef(env, object);
        objFormat = objectFormats + i;
        objFormat -> pguid = guids + i;
        objFormat -> dwType = ((*env) -> CallIntMethod(env, object, getTypeMethod)) |
                              DIDFT_MAKEINSTANCE((*env) -> CallIntMethod(env, object, getInstanceMethod));
        objFormat -> dwFlags = flags & (DIDOI_ASPECTACCEL | DIDOI_ASPECTFORCE | DIDOI_ASPECTPOSITION | DIDOI_ASPECTVELOCITY);
        objFormat -> dwOfs = i * sizeof(jint);
    }
    dataFormat.rgodf = objectFormats;
    HRESULT res = IDirectInputDevice8_SetDataFormat(lpDevice, &dataFormat);
    free(guids);
    free(objectFormats);
    return res;
}*/

JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_setDataFormat
(JNIEnv* env, jclass unused, jlong address, jint flags, jobjectArray objects) {
	LPDIRECTINPUTDEVICE8 lpDevice = (LPDIRECTINPUTDEVICE8)(INT_PTR)address;
	DIDATAFORMAT data_format;
	jsize num_objects = (*env)->GetArrayLength(env, objects);
	/*
	 * Data size must be a multiple of 4, but since sizeof(jint) is
	 * 4, we're safe
	 */
	DWORD data_size = num_objects * sizeof(jint);
	GUID* guids;
	DIOBJECTDATAFORMAT* object_formats;
	int i;
	HRESULT res;
	jclass clazz;
	jmethodID getGUID_method;
	jmethodID getFlags_method;
	jmethodID getType_method;
	jmethodID getInstance_method;
	jobject object;
	jint type;
	jint object_flags;
	jint instance;
	jobject guid_array;
	DWORD composite_type;
	DWORD flags_masked;
	LPDIOBJECTDATAFORMAT object_format;

	data_format.dwSize = sizeof(DIDATAFORMAT);
	data_format.dwObjSize = sizeof(DIOBJECTDATAFORMAT);
	data_format.dwFlags = flags;
	data_format.dwDataSize = data_size;
	data_format.dwNumObjs = num_objects;

	clazz = (*env)->FindClass(env, "com/boomaa/opends/usb/DIDeviceObject");
	if (clazz == NULL)
		return -1;
	getGUID_method = (*env)->GetMethodID(env, clazz, "getGUID", "()[B");
	if (getGUID_method == NULL)
		return -1;
	getFlags_method = (*env)->GetMethodID(env, clazz, "getFlags", "()I");
	if (getFlags_method == NULL)
		return -1;
	getType_method = (*env)->GetMethodID(env, clazz, "getType", "()I");
	if (getType_method == NULL)
		return -1;
	getInstance_method = (*env)->GetMethodID(env, clazz, "getInstance", "()I");
	if (getInstance_method == NULL)
		return -1;

	guids = (GUID*)malloc(num_objects * sizeof(GUID));
	if (guids == NULL) {
		throwIOException(env, "Failed to allocate GUIDs");
		return -1;
	}
	object_formats = (DIOBJECTDATAFORMAT*)malloc(num_objects * sizeof(DIOBJECTDATAFORMAT));
	if (object_formats == NULL) {
		free(guids);
		throwIOException(env, "Failed to allocate data format");
		return -1;
	}
	for (i = 0; i < num_objects; i++) {
		object = (*env)->GetObjectArrayElement(env, objects, i);
		if ((*env)->ExceptionOccurred(env)) {
			free(guids);
			free(object_formats);
			return -1;
		}
		guid_array = (*env)->CallObjectMethod(env, object, getGUID_method);
		if ((*env)->ExceptionOccurred(env)) {
			free(guids);
			free(object_formats);
			return -1;
		}
		unwrapGUID(env, guid_array, guids + i);
		if ((*env)->ExceptionOccurred(env)) {
			free(guids);
			free(object_formats);
			return -1;
		}
		type = (*env)->CallIntMethod(env, object, getType_method);
		if ((*env)->ExceptionOccurred(env)) {
			free(guids);
			free(object_formats);
			return -1;
		}
		object_flags = (*env)->CallIntMethod(env, object, getFlags_method);
		if ((*env)->ExceptionOccurred(env)) {
			free(guids);
			free(object_formats);
			return -1;
		}
		instance = (*env)->CallIntMethod(env, object, getInstance_method);
		if ((*env)->ExceptionOccurred(env)) {
			free(guids);
			free(object_formats);
			return -1;
		}
		(*env)->DeleteLocalRef(env, object);
		composite_type = type | DIDFT_MAKEINSTANCE(instance);
		flags_masked = flags & (DIDOI_ASPECTACCEL | DIDOI_ASPECTFORCE | DIDOI_ASPECTPOSITION | DIDOI_ASPECTVELOCITY);
		object_format = object_formats + i;
		object_format->pguid = guids + i;
		object_format->dwType = composite_type;
		object_format->dwFlags = flags_masked;
		// dwOfs must be multiple of 4, but sizeof(jint) is 4, so we're safe
		object_format->dwOfs = i * sizeof(jint);
	}
	data_format.rgodf = object_formats;
	res = IDirectInputDevice8_SetDataFormat(lpDevice, &data_format);
	free(guids);
	free(object_formats);
	return res;
}


/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    setCooperativeLevel
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_setCooperativeLevel
  (JNIEnv *env, jclass unused, jlong address, jlong hwndAddress, jint flags) {
    return IDirectInputDevice8_SetCooperativeLevel(deviceFromAddress(address), (HWND) (INT_PTR) hwndAddress, flags);
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    fetchRangeProperty
 * Signature: (JI[J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_fetchRangeProperty
  (JNIEnv *env, jclass unused, jlong address, jint objectId, jlongArray rangeArrayObj) {
    DIPROPRANGE range;
    range.diph.dwSize = sizeof(DIPROPRANGE);
    range.diph.dwHeaderSize = sizeof(DIPROPHEADER);
    range.diph.dwObj = objectId;
    range.diph.dwHow = DIPH_BYID;

    HRESULT res = IDirectInputDevice8_GetProperty(deviceFromAddress(address), DIPROP_RANGE, &(range.diph));
    jlong rangeArray[2];
    rangeArray[0] = range.lMin;
    rangeArray[1] = range.lMax;
    (*env) -> SetLongArrayRegion(env, rangeArrayObj, 0, 2, rangeArray);
    return res;
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    fetchDeadbandProperty
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_fetchDeadbandProperty
  (JNIEnv *env, jclass unused, jlong address, jint objectId) {
    DIPROPDWORD deadzone;
    deadzone.diph.dwSize = sizeof(deadzone);
    deadzone.diph.dwHeaderSize = sizeof(DIPROPHEADER);
    deadzone.diph.dwObj = objectId;
    deadzone.diph.dwHow = DIPH_BYID;

    IDirectInputDevice8_GetProperty(deviceFromAddress(address), DIPROP_DEADZONE, &(deadzone.diph));
    return deadzone.dwData;
}