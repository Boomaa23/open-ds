#pragma comment (lib, "User32.lib")
#include <stdio.h>
#include <jni.h>
#include "win32util.h"

static jstring sprintfJavaString(JNIEnv *env, const char *format, va_list ap) {
#define BUFFER_SIZE 4000
    char buffer[BUFFER_SIZE];
    jstring str;
#ifdef _MSC_VER
	vsnprintf_s(buffer, BUFFER_SIZE, _TRUNCATE, format, ap);
#else
    vsnprintf(buffer, BUFFER_SIZE, format, ap);
#endif
    buffer[BUFFER_SIZE - 1] = '\0';
    str = (*env)->NewStringUTF(env, buffer);
    return str;
}

void printfJava(JNIEnv *env, const char *format, ...) {
    jstring str;
    va_list ap;
    va_start(ap, format);
    str = sprintfJavaString(env, format, ap);
    va_end(ap);
}

jbyteArray wrapGUID(JNIEnv *env, const GUID *guid) {
    jbyteArray guid_array = (*env)->NewByteArray(env, sizeof(GUID));
    if (guid_array == NULL)
        return NULL;
    (*env)->SetByteArrayRegion(env, guid_array, 0, sizeof(GUID), (jbyte *)guid);
    return guid_array;
}

void unwrapGUID(JNIEnv *env, const jobjectArray byte_array, GUID *guid) {
    (*env)->GetByteArrayRegion(env, byte_array, 0, sizeof(GUID), (jbyte *)guid);
}

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

static void throwException(JNIEnv *env, const char *exception_name, const char *format, va_list ap) {
    jstring str;
    jobject exception;

    if ((*env)->ExceptionCheck(env) == JNI_TRUE)
        return; // The JVM crashes if we try to throw two exceptions from one native call
    str = sprintfJavaString(env, format, ap);
    exception = newJObject(env, exception_name, "(Ljava/lang/String;)V", str);
    (*env)->Throw(env, exception);
}

void throwIOException(JNIEnv *env, const char *format, ...) {
    va_list ap;
    va_start(ap, format);
    throwException(env, "java/io/IOException", format, ap);
    va_end(ap);
    (void) ap;
}