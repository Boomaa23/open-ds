#include <windows.h>
#include <jni.h>
#include "win32util.h"
#include <dinput.h>
#include "com_boomaa_opends_usb_DirectInputDevice.h"

typedef struct {
	JNIEnv* env;
	jobject obj;
} DIDEnumContext;

static jint numericAxisGUID(const GUID *guid) {
    if (IsEqualGUID(guid, &GUID_XAxis)) {
		return 0;
    } else if (IsEqualGUID(guid, &GUID_YAxis)) {
        return 1;
    } else if (IsEqualGUID(guid, &GUID_ZAxis)) {
        return 2;
    } else if (IsEqualGUID(guid, &GUID_RxAxis)) {
        return 3;
    } else if (IsEqualGUID(guid, &GUID_RyAxis)) {
        return 4;
    } else if (IsEqualGUID(guid, &GUID_RzAxis)) {
        return 5;
    } else if (IsEqualGUID(guid, &GUID_Slider)) {
		return 6;
    } else if (IsEqualGUID(guid, &GUID_POV)) {
		return 7;
	}
    return 8;
}

static BOOL CALLBACK enumObjectsCallback(LPCDIDEVICEOBJECTINSTANCE lpddoi, LPVOID pvRef) {
    DIDEnumContext *enumContext = (DIDEnumContext *) pvRef;
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
                    "([BIIIILjava/lang/String;)V"
            ),
            wrapGUID(env, guidType),
            (jint) dwType,
            (jint) DIDFT_GETINSTANCE(dwType),
            (jint) DIDFT_GETTYPE(dwType),
            numericAxisGUID(guidType),
            (*env) -> NewStringUTF(env, lpddoi -> tszName)
    );
    if ((*env) -> ExceptionOccurred(env)) {
        return DIENUM_STOP;
    }
    return DIENUM_CONTINUE;
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    enumObjects
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_enumObjects
  (JNIEnv *env, jobject obj, jlong address, jint flags) {
	LPDIRECTINPUTDEVICE8 lpDevice = (LPDIRECTINPUTDEVICE8)(INT_PTR) address;
	HRESULT res;
	DIDEnumContext enum_context;

	enum_context.env = env;
	enum_context.obj = obj;
	res = IDirectInputDevice8_EnumObjects(lpDevice, enumObjectsCallback, &enum_context, flags);
	return res;
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    poll
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_poll
  (JNIEnv *env, jclass obj, jlong address) {
    UNUSED(env);  // suppress C4100
    UNUSED(obj);  // suppress C4100
	LPDIRECTINPUTDEVICE8 lpDevice = (LPDIRECTINPUTDEVICE8)(INT_PTR) address;
    return IDirectInputDevice8_Poll(lpDevice);
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    acquire
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_acquire
  (JNIEnv *env, jclass obj, jlong address) {
    UNUSED(env);  // suppress C4100
    UNUSED(obj);  // suppress C4100
	LPDIRECTINPUTDEVICE8 lpDevice = (LPDIRECTINPUTDEVICE8)(INT_PTR) address;
    return IDirectInputDevice8_Acquire(lpDevice);
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    unacquire
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_unacquire
  (JNIEnv *env, jclass obj, jlong address) {
    UNUSED(env);  // suppress C4100
    UNUSED(obj);  // suppress C4100
	LPDIRECTINPUTDEVICE8 lpDevice = (LPDIRECTINPUTDEVICE8)(INT_PTR) address;
    return IDirectInputDevice8_Unacquire(lpDevice);
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    release
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_release
  (JNIEnv *env, jclass obj, jlong address) {
    UNUSED(env);  // suppress C4100
    UNUSED(obj);  // suppress C4100
	LPDIRECTINPUTDEVICE8 lpDevice = (LPDIRECTINPUTDEVICE8)(INT_PTR) address;
    return IDirectInputDevice8_Release(lpDevice);
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    fetchDeviceState
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_fetchDeviceState
  (JNIEnv *env, jclass obj, jlong address, jintArray deviceStateArray) {
    UNUSED(env);  // suppress C4100
    UNUSED(obj);  // suppress C4100
	LPDIRECTINPUTDEVICE8 lpDevice = (LPDIRECTINPUTDEVICE8)(INT_PTR) address;
	jsize state_length = (*env)->GetArrayLength(env, deviceStateArray);
	DWORD state_size = state_length * sizeof(jint);
	HRESULT res;
	jint* device_state = (*env)->GetIntArrayElements(env, deviceStateArray, NULL);
	if (device_state == NULL)
		return -1;

	res = IDirectInputDevice8_GetDeviceState(lpDevice, state_size, device_state);
	(*env)->ReleaseIntArrayElements(env, deviceStateArray, device_state, 0);
	return res;
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    setDataFormat
 * Signature: (JI[Lcom/boomaa/opends/usb/input/DIDeviceObject;)I
 */

JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_setDataFormat
(JNIEnv* env, jclass obj, jlong address, jint flags, jobjectArray objects) {
    UNUSED(obj);  // suppress C4100
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
	jmethodID getDIDFTType_method;
	jmethodID getDIDFTInstance_method;
	jobject object;
	jint type;
	jint instance;
	jobject guid_array;
	DWORD composite_type;
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
	getDIDFTType_method = (*env)->GetMethodID(env, clazz, "getDIDFTType", "()I");
	if (getDIDFTType_method == NULL)
		return -1;
	getDIDFTInstance_method = (*env)->GetMethodID(env, clazz, "getDIDFTInstance", "()I");
	if (getDIDFTInstance_method == NULL)
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
		type = (*env)->CallIntMethod(env, object, getDIDFTType_method);
		if ((*env)->ExceptionOccurred(env)) {
			free(guids);
			free(object_formats);
			return -1;
		}
		instance = (*env)->CallIntMethod(env, object, getDIDFTInstance_method);
		if ((*env)->ExceptionOccurred(env)) {
			free(guids);
			free(object_formats);
			return -1;
		}
		(*env)->DeleteLocalRef(env, object);
		composite_type = type | DIDFT_MAKEINSTANCE(instance);
		object_format = object_formats + i;
		object_format->pguid = guids + i;
		object_format->dwType = composite_type;
		object_format->dwFlags = flags;
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
  (JNIEnv *env, jclass obj, jlong address, jlong hwndAddress, jint flags) {
    UNUSED(env);  // suppress C4100
    UNUSED(obj);  // suppress C4100
	LPDIRECTINPUTDEVICE8 lpDevice = (LPDIRECTINPUTDEVICE8)(INT_PTR) address;
	HWND hwnd = (HWND)(INT_PTR) hwndAddress;
	HRESULT res;
	res = IDirectInputDevice8_SetCooperativeLevel(lpDevice, hwnd, flags);
	return res;
}

/*
 * Class:     com_boomaa_opends_usb_DirectInputDevice
 * Method:    fetchRangeProperty
 * Signature: (JI[J)I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_usb_DirectInputDevice_fetchRangeProperty
  (JNIEnv *env, jclass obj, jlong address, jint objectId, jlongArray rangeArrayObj) {
    UNUSED(env);  // suppress C4100
    UNUSED(obj);  // suppress C4100
	LPDIRECTINPUTDEVICE8 lpDevice = (LPDIRECTINPUTDEVICE8)(INT_PTR) address;
	DIPROPRANGE range;
	HRESULT res;
	jlong range_array[2];
	range.diph.dwSize = sizeof(DIPROPRANGE);
	range.diph.dwHeaderSize = sizeof(DIPROPHEADER);
	range.diph.dwObj = objectId;
	range.diph.dwHow = DIPH_BYID;
	res = IDirectInputDevice8_GetProperty(lpDevice, DIPROP_RANGE, &(range.diph));
	range_array[0] = range.lMin;
	range_array[1] = range.lMax;
	(*env)->SetLongArrayRegion(env, rangeArrayObj, 0, 2, range_array);
	return res;
}
