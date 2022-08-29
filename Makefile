USB_SRC := src/main/java/com/boomaa/opends/usb
LIB_OUT := src/main/resources
LIB_NAME := opends-lib

LINUX_INCLUDE_PATH := /usr/lib/jvm/java-8-openjdk-amd64/include
OSX_INCLUDE_PATH = LINUX_INCLUDE_PATH
WIN32_INCLUDE_PATH := "C:\Program Files\Java\jdk1.8.0_251\include"

VS_PATH := "C:\\Program Files (x86)\\Microsoft Visual Studio\\2019\\"

ifeq ($(OS),Windows_NT)
    OS_TYPE := win32
    SHELL = CMD
    LIB_OUT := $(subst /,\\,$(LIB_OUT))
    ifeq ($(PROCESSOR_ARCHITEW6432),AMD64)
        ARCH_TYPE := amd64
    else
        ifeq ($(PROCESSOR_ARCHITECTURE),AMD64)
            ARCH_TYPE := amd64
        endif
        ifeq ($(PROCESSOR_ARCHITECTURE),x86)
            ARCH_TYPE := x86
        endif
    endif
else
    UNAME_S := $(shell uname -s)
    ifeq ($(UNAME_S),Linux)
        OS_TYPE := linux
    endif
    ifeq ($(UNAME_S),Darwin)
        OS_TYPE := osx
    endif
    UNAME_P := $(shell uname -p)
    ifeq ($(UNAME_P),x86_64)
        ARCH_TYPE := amd64
    else
		ifneq ($(filter %86,$(UNAME_P)),)
			ARCH_TYPE := x86
		else
			ARCH_TYPE := $(UNAME_P)
		endif
	endif
endif

.PHONY: build check jar clean native-linux native-osx native-win32

build: clean
	mvn -B package --file pom.xml

check: clean
	mvn -B checkstyle:check --file pom.xml

jar: check build

clean:
ifeq ($(OS_TYPE),linux)
	rm -rf target/
endif
ifeq ($(OS_TYPE),osx)
	rm -rf target/
endif
ifeq ($(OS_TYPE),win32)
	rmdir /s /q target
endif

native:
ifeq ($(OS_TYPE),linux)
	make native-linux
endif
ifeq ($(OS_TYPE),osx)
	make native-osx
endif
ifeq ($(OS_TYPE),win32)
	make native-win32
endif

native-linux:
	gcc -c -fPIC -I$(LINUX_INCLUDE_PATH) -I$(LINUX_INCLUDE_PATH)/linux/ \
		$(USB_SRC)/linux/com_boomaa_opends_usb_LinuxController.c
	gcc -shared -o $(LIB_OUT)/$(LIB_NAME)-linux-$(ARCH_TYPE).so com_boomaa_opends_usb_LinuxController.o
	rm com_boomaa_opends_usb_LinuxController.o

native-osx:
	gcc -c -fPIC -I$(OSX_INCLUDE_PATH) -I$(OSX_INCLUDE_PATH)/darwin/ \
		$(USB_SRC)/osx/com_boomaa_opends_usb_IOKit.c \
		$(USB_SRC)/com_boomaa_opends_usb_IOKitDevice.c
	gcc -shared -framework IOKit -framework CoreServices -o $(LIB_OUT)/$(LIB_NAME)-osx-$(ARCH_TYPE).jnilib \
		com_boomaa_opends_usb_IOKit.o com_boomaa_opends_usb_IOKitDevice.o
	rm com_boomaa_opends_usb_IOKit.o com_boomaa_opends_usb_IOKitDevice.o

native-win32:
	$(VS_PATH)\\BuildTools\\VC\\Auxiliary\\Build\\vcvars64.bat && \
	cl.exe /LD /I$(WIN32_INCLUDE_PATH) /I$(WIN32_INCLUDE_PATH)\\win32 \
		$(USB_SRC)/win32/com_boomaa_opends_usb_DirectInput.c \
		$(USB_SRC)/win32/com_boomaa_opends_usb_DirectInputDevice.c \
		$(USB_SRC)/win32/com_boomaa_opends_usb_PlaceholderWindow.c \
		$(USB_SRC)/win32/win32util.c /O1 /MD /Zc:inline
	del com_boomaa_opends_usb_DirectInput.exp com_boomaa_opends_usb_DirectInput.lib \
 		com_boomaa_opends_usb_DirectInput.obj com_boomaa_opends_usb_DirectInputDevice.obj \
 		com_boomaa_opends_usb_PlaceholderWindow.obj win32util.obj
	move /y com_boomaa_opends_usb_DirectInput.dll "$(LIB_OUT)\\"
	del $(LIB_OUT)\\$(LIB_NAME)-win32-$(ARCH_TYPE).dll
	ren $(LIB_OUT)\\com_boomaa_opends_usb_DirectInput.dll $(LIB_NAME)-win32-$(ARCH_TYPE).dll
