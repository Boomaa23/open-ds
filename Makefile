USB_SRC := src/main/java/com/boomaa/opends/usb
LIB_OUT := src/main/resources
LIB_NAME := opends-lib

OS_TYPE := $(shell uname -s)
ARCH_TYPE := $(shell uname -p)
JAVA_PATH ?= $(shell which java 2>/dev/null)
INCLUDE_PATH ?= $(shell readlink -f $(JAVA_PATH) 2>/dev/null | head --bytes=-10)/include

ifeq ($(ARCH_TYPE),x86_64)
	ARCH_TYPE := amd64
endif

.PHONY: native native-linux native-osx native-win32

native:
#TODO do not call make again here
ifeq ($(OS_TYPE),Linux)
	make native-linux
else
ifeq ($(OS_TYPE),Darwin)
	make native-osx
else
	make native-win32
endif
endif

native-linux:
	gcc -c -fPIC -I$(INCLUDE_PATH) -I$(INCLUDE_PATH)/linux/ \
		$(USB_SRC)/linux/com_boomaa_opends_usb_LinuxController.c
	gcc -shared -o $(LIB_OUT)/$(LIB_NAME)-linux-$(ARCH_TYPE).so com_boomaa_opends_usb_LinuxController.o
	rm com_boomaa_opends_usb_LinuxController.o

native-osx:
# TODO figure out how to get java include path (similar to linux?)
	gcc -c -fPIC -I$(INCLUDE_PATH) -I$(INCLUDE_PATH)/darwin/ \
		$(USB_SRC)/osx/com_boomaa_opends_usb_IOKit.c $(USB_SRC)/com_boomaa_opends_usb_IOKitDevice.c
	gcc -shared -framework IOKit -framework CoreServices -o $(LIB_OUT)/$(LIB_NAME)-osx-$(ARCH_TYPE).jnilib \
		com_boomaa_opends_usb_IOKit.o com_boomaa_opends_usb_IOKitDevice.o
	rm com_boomaa_opends_usb_IOKit.o com_boomaa_opends_usb_IOKitDevice.o

native-win32:
# TODO implement (if possible)
	echo "WIN#@"