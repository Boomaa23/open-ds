#include <jni.h>
#include <stdbool.h>
#include <windows.h>
#include <winbase.h>

/* Header for class com_boomaa_opends_util_battery_Win32BatteryJNI */

#ifndef _Included_com_boomaa_opends_util_battery_Win32BatteryJNI
#define _Included_com_boomaa_opends_util_battery_Win32BatteryJNI
#ifdef __cplusplus
extern "C" {
#endif

SYSTEM_POWER_STATUS CreatePowerStatus() {
    SYSTEM_POWER_STATUS status;
    if (!GetSystemPowerStatus(&status)) {
        printf("Cannot get Win32 power status, error %lu", GetLastError());
    }
    return status;
}

/*
 * Class:     com_boomaa_opends_util_battery_Win32BatteryJNI
 * Method:    isAC
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_boomaa_opends_util_battery_Win32BatteryJNI_isAC
  (JNIEnv *env, jclass clazz) {
    SYSTEM_POWER_STATUS status = CreatePowerStatus();
    unsigned char ac = status.ACLineStatus;
    if (ac == 1) {
        return JNI_TRUE;
    } else if (ac == 0 || ac == 255) {
        return JNI_FALSE;
    }
  }

/*
 * Class:     com_boomaa_opends_util_battery_Win32BatteryJNI
 * Method:    getFlag
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_util_battery_Win32BatteryJNI_getFlag
  (JNIEnv *env, jclass clazz) {
    return (jint) CreatePowerStatus().BatteryFlag;
  }

/*
 * Class:     com_boomaa_opends_util_battery_Win32BatteryJNI
 * Method:    getPercent
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_util_battery_Win32BatteryJNI_getPercent
  (JNIEnv *env, jclass clazz) {
    SYSTEM_POWER_STATUS status;
    return (jint) CreatePowerStatus().BatteryLifePercent;
  }

/*
 * Class:     com_boomaa_opends_util_battery_Win32BatteryJNI
 * Method:    getLifeTime
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_util_battery_Win32BatteryJNI_getLifeTime
  (JNIEnv *env, jclass clazz) {
    return (jint) CreatePowerStatus().BatteryLifeTime;
  }

/*
 * Class:     com_boomaa_opends_util_battery_Win32BatteryJNI
 * Method:    getFullTime
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_boomaa_opends_util_battery_Win32BatteryJNI_getFullTime
  (JNIEnv *env, jclass clazz) {
    return (jint) CreatePowerStatus().BatteryFullLifeTime;
  }

#ifdef __cplusplus
}
#endif
#endif