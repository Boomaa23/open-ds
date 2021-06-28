cmake_minimum_required(VERSION 3.16)
project(open-ds)

set(CMAKE_C_STANDARD 11)
include_directories("C:\\Program Files\\Java\\jdk1.8.0_251\\include")
include_directories("C:\\Program Files\\Java\\jdk1.8.0_251\\include\\win32")
include_directories(${DXSDK_DIR}include)
include_directories (include ${PROJECT_BINARY_DIR})

add_library(ods-input SHARED
        src/main/java/com/boomaa/opends/usb/input/com_boomaa_opends_usb_input_DirectInput.c
        src/main/java/com/boomaa/opends/usb/input/com_boomaa_opends_usb_input_DirectInputDevice.c
        src/main/java/com/boomaa/opends/usb/input/com_boomaa_opends_usb_input_PlaceholderWindow.c
        src/main/java/com/boomaa/opends/usb/input/win32util.c
)