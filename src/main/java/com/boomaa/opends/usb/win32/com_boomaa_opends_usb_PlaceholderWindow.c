#include <jni.h>
#include <windows.h>

/* Header for class com_boomaa_opends_usb_PlaceholderWindow */

#ifndef _Included_com_boomaa_opends_usb_PlaceholderWindow
#define _Included_com_boomaa_opends_usb_PlaceholderWindow
#ifdef __cplusplus
extern "C" {
#endif

static const TCHAR* DUMMY_WINDOW_NAME = "JInputControllerWindow";

static LRESULT CALLBACK ProcWindow(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam) {
    return DefWindowProc(hWnd, message, wParam, lParam);
}

/*
 * Class:     com_boomaa_opends_usb_PlaceholderWindow
 * Method:    createWindow
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_boomaa_opends_usb_PlaceholderWindow_createWindow
  (JNIEnv *env, jclass clazz) {
    HINSTANCE inst = GetModuleHandle(NULL);

    WNDCLASSEX wcex;
    wcex.cbSize = sizeof(WNDCLASSEX);
    wcex.style = CS_HREDRAW | CS_VREDRAW;
    wcex.lpfnWndProc = (WNDPROC) ProcWindow;
    wcex.cbClsExtra = 0;
    wcex.cbWndExtra	= 0;
    wcex.hInstance = inst;
    wcex.hIcon = NULL;
    wcex.hCursor = NULL;
    wcex.hbrBackground = (HBRUSH)(COLOR_WINDOW+1);
    wcex.lpszMenuName = (LPCSTR)NULL;
    wcex.lpszClassName = DUMMY_WINDOW_NAME;
    wcex.hIconSm = NULL;
    RegisterClassEx(&wcex);

    WNDCLASSEX classInfo;
    classInfo.cbSize = sizeof(WNDCLASSEX);
    classInfo.cbClsExtra = 0;
    classInfo.cbWndExtra = 0;

    HWND hwnd = CreateWindow(DUMMY_WINDOW_NAME, NULL,
            WS_POPUP | WS_ICONIC, 0, 0, 0, 0, NULL, NULL, inst, NULL);

    return (jlong) (intptr_t) hwnd;
}

/*
 * Class:     com_boomaa_opends_usb_PlaceholderWindow
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_boomaa_opends_usb_PlaceholderWindow_destroy
  (JNIEnv *env, jclass clazz, jlong address) {
    DestroyWindow((HWND) (INT_PTR) address);
}

#ifdef __cplusplus
}
#endif
#endif
