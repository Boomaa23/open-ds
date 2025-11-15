#ifndef WIN32_UTIL_H
#define WIN32_UTIL_H

#define UNUSED(x) (void)(x)
#define DXVERSION_H
#define DIRECTINPUT_VERSION 0x0800

#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#ifdef _MSC_VER
#include <Basetsd.h>
#else
#include <inttypes.h>
#endif
#include <initguid.h>
#include <dinput.h>

static jstring sprintfJavaString(JNIEnv *env, const char *format, va_list ap);

void printfJava(JNIEnv *env, const char *format, ...);

jbyteArray wrapGUID(JNIEnv *env, const GUID *guid);

void unwrapGUID(JNIEnv *env, const jobjectArray byte_array, GUID *guid);

jobject newJObject(JNIEnv *env, const char *class_name, const char *constructor_signature, ...);

static void throwException(JNIEnv *env, const char *exception_name, const char *format, va_list ap);

void throwIOException(JNIEnv *env, const char *format, ...);

#endif /* WIN32_UTIL_H */