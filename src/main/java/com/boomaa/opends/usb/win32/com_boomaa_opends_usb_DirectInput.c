#pragma comment(lib, "dinput8")
#include <windows.h>
#include <jni.h>
#include "win32util.h"
#include <dinput.h>
#include "com_boomaa_opends_usb_DirectInput.h"

typedef struct {
    LPDIRECTINPUT8 lpDirectInput;
    JNIEnv *env;
    jobject obj;
} DIEnumContext;

/*
 * Class:     com_boomaa_opends_usb_DirectInput
 * Method:    create
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_boomaa_opends_usb_DirectInput_create
  (JNIEnv *env, jobject obj) {
    LPDIRECTINPUT8 lpDirectInput;
    DirectInput8Create(GetModuleHandle(NULL), DIRECTINPUT_VERSION, &IID_IDirectInput8, (void *) &lpDirectInput, NULL);
    return (jlong) (INT_PTR) lpDirectInput;
}

static BOOL CALLBACK enumDevicesCallback(LPCDIDEVICEINSTANCE inst, LPVOID context) {
    DIEnumContext *enumContext = (DIEnumContext *) context;
    LPDIRECTINPUTDEVICE8 lpDevice;
    HRESULT result = IDirectInput8_CreateDevice(enumContext -> lpDirectInput, &(inst -> guidInstance), &lpDevice, NULL);
    if (FAILED(result)) {
        throwIOException(enumContext -> env, "Failed to create device (%d)\n", result);
        return DIENUM_STOP;
    }
    (*enumContext -> env) -> CallVoidMethod(
            enumContext -> env,
            enumContext -> obj,
            (*enumContext -> env) -> GetMethodID(
                    enumContext -> env,
                    (*enumContext -> env) -> GetObjectClass(
                            enumContext -> env,
                            enumContext -> obj
                    ),
                    "addDevice",
                    "(J[B[BIILjava/lang/String;Ljava/lang/String;)V"
            ),
            (jlong) (INT_PTR) lpDevice,
            wrapGUID(enumContext -> env, &(inst -> guidInstance)),
            wrapGUID(enumContext -> env, &(inst -> guidProduct)),
            GET_DIDEVICE_TYPE(inst -> dwDevType),
            GET_DIDEVICE_SUBTYPE(inst -> dwDevType),
            (*enumContext -> env) -> NewStringUTF(enumContext -> env, inst -> tszInstanceName),
            (*enumContext -> env) -> NewStringUTF(enumContext -> env, inst -> tszProductName)
    );
    if ((*enumContext -> env) -> ExceptionOccurred(enumContext -> env) != NULL) {
        IDirectInputDevice8_Release(lpDevice);
        return DIENUM_STOP;
    }
    return DIENUM_CONTINUE;
}

/*
 * Class:     com_boomaa_opends_usb_DirectInput
 * Method:    enumDevices
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_boomaa_opends_usb_DirectInput_enumDevices
(JNIEnv* env, jobject obj, jlong address) {
    LPDIRECTINPUT8 lpDirectInput = (LPDIRECTINPUT8)(INT_PTR)address;
    HRESULT res;

    DIEnumContext enum_context;
    enum_context.lpDirectInput = lpDirectInput;
    enum_context.env = env;
    enum_context.obj = obj;
    res = IDirectInput8_EnumDevices(lpDirectInput, DI8DEVCLASS_GAMECTRL, enumDevicesCallback, &enum_context, DIEDFL_ATTACHEDONLY);
    if (FAILED(res)) {
        throwIOException(env, "Failed to enumerate devices (%d)\n", res);
    }
}

/*
 * Class:     com_boomaa_opends_usb_DirectInput
 * Method:    release
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_boomaa_opends_usb_DirectInput_release
  (JNIEnv *env, jobject obj, jlong address) {
    IDirectInput8_Release((LPDIRECTINPUT8) (INT_PTR) address);
}