export MSYS2_ARG_CONV_EXCL=*

USB_SRC := src/main/java/com/boomaa/opends/usb
LIB_OUT_DIR := src/main/resources
LIB_NAME := opends-lib

UNIX_JDK_INCLUDE_PATH ?= /usr/lib/jvm/java-8-openjdk-amd64/include
WIN32_JDK_INCLUDE_PATH ?= C:\\Program Files\\Eclipse Foundation\\jdk-8.0.302.8-hotspot\\include
VS_YEAR ?= 2019
VS_PATH ?= C:\\Program Files (x86)\\Microsoft Visual Studio\\$(VS_YEAR)\\

CC_SEED ?= 0x58185818

ifndef OS_TYPE
	ifeq ($(OS),Windows_NT)
		OS_TYPE = win32
	else
		UNAME_S = $(shell uname -s)
		ifeq ($(UNAME_S),Linux)
			OS_TYPE = linux
		endif
		ifeq ($(UNAME_S),Darwin)
			OS_TYPE = osx
		endif
	endif
endif

ifndef ARCH_TYPE
	ifeq ($(OS),Windows_NT)
		ifeq ($(PROCESSOR_ARCHITEW6432),AMD64)
			ARCH_TYPE = amd64
		else
			ifeq ($(PROCESSOR_ARCHITECTURE),AMD64)
				ARCH_TYPE = amd64
			endif
			ifeq ($(PROCESSOR_ARCHITECTURE),x86)
				ARCH_TYPE = x86
			endif
		endif
	else
		UNAME_P = $(shell uname -m)
		ifeq ($(UNAME_P),x86_64)
			ARCH_TYPE = amd64
		else
			ifneq ($(filter %86,$(UNAME_P)),)
				ARCH_TYPE = x86
			else
				ARCH_TYPE = $(UNAME_P)
			endif
		endif
	endif
endif

ifeq ($(OS_TYPE),win32)
	CWD = ${CURDIR}
	LIB_OUT_DIR_WIN32 = $(subst /,\\,$(LIB_OUT_DIR))
else
	CWD = $(shell pwd)
endif

ifeq ($(ARCH_TYPE),aarch64)
	ifeq ($(OS_TYPE),linux)
		CC ?= /usr/bin/aarch64-linux-musl-gcc
	endif
	ifeq ($(OS_TYPE),win32)
		CC ?= aarch64-w64-mingw32-gcc
		VCVARS_SELECTOR = amd64_arm64
	endif
endif
ifeq ($(ARCH_TYPE),amd64)
	ifeq ($(OS_TYPE),win32)
		CC ?= x86_64-w64-mingw32-gcc
		VCVARS_SELECTOR = 64
	endif
endif
CC ?= gcc

ifeq ($(OS_TYPE),win32)
	LIB_OUT_PATH ?= $(LIB_OUT_DIR_WIN32)\\$(LIB_NAME)-win32-$(ARCH_TYPE).dll
endif
ifeq ($(OS_TYPE),linux)
	LIB_OUT_PATH ?= $(LIB_OUT_DIR)/$(LIB_NAME)-linux-$(ARCH_TYPE).so
endif
ifeq ($(OS_TYPE),osx)
	LIB_OUT_PATH ?= $(LIB_OUT_DIR)/$(LIB_NAME)-osx-$(ARCH_TYPE).jnilib
endif

.PHONY: build check jar clean native native-all-docker native-linux native-osx native-win32 lib-out-path

build:
	mvn -B package --file pom.xml

check:
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

native-all-docker:
	docker build --platform linux/amd64 -t opends-linux-build .
	docker run -v $(CWD):/opends -e ARCH_TYPE=aarch64 -e OS_TYPE=linux  \
		opends-linux-build sh -c "make native-linux"
#	docker run -v $(CWD):/opends -e ARCH_TYPE=amd64 -e OS_TYPE=linux  \
#		opends-linux-build sh -c "make native-linux"
#	docker run -v $(CWD):/opends -e ARCH_TYPE=aarch64 -e OS_TYPE=win32  \
#    		opends-linux-build sh -c "make native-win32-gcc"
#	docker run -v $(CWD):/opends -e ARCH_TYPE=amd64 -e OS_TYPE=win32  \
#			opends-linux-build sh -c "make native-win32-gcc"

native-linux:
	$(CC) -Os -s -I$(UNIX_JDK_INCLUDE_PATH) -I$(UNIX_JDK_INCLUDE_PATH)/linux/ \
		-shared -frandom-seed=$(CC_SEED) -o $(LIB_OUT_PATH) $(USB_SRC)/linux/*.c

native-osx:
	$(CC) -c -fPIC -frandom-seed=$(CC_SEED) -I$(UNIX_JDK_INCLUDE_PATH) -I$(UNIX_JDK_INCLUDE_PATH)/darwin/ $(USB_SRC)/osx/*.c
	$(CC) -shared -framework IOKit -framework CoreServices -o $(LIB_OUT_PATH) *.o
	rm com_boomaa_opends_usb_IOKit.o com_boomaa_opends_usb_IOKitDevice.o

native-win32:
	where cl.exe
	if %ERRORLEVEL% NEQ 0 "$(VS_PATH)\\BuildTools\\VC\\Auxiliary\\Build\\vcvars$(VCVARS_SELECTOR).bat"
	cl.exe /LD /I"$(WIN32_JDK_INCLUDE_PATH)" /I"$(WIN32_JDK_INCLUDE_PATH)\\win32" $(USB_SRC)/win32/*.c /O1 /MD /Zc:inline /W4 /Brepro
	del *.exp *.lib *.obj
	move /y com_boomaa_opends_usb_DirectInput.dll "$(LIB_OUT_DIR_WIN32)\\"
	del "$(LIB_OUT_PATH)"
	ren "$(LIB_OUT_DIR_WIN32)\\com_boomaa_opends_usb_DirectInput.dll" "$(LIB_NAME)-win32-$(ARCH_TYPE).dll"

native-win32-gcc:
	$(CC) -Os -s -I$(UNIX_JDK_INCLUDE_PATH) -I$(UNIX_JDK_INCLUDE_PATH)/linux/ \
		-shared -o $(LIB_OUT_DIR)/$(LIB_NAME)-win32-$(ARCH_TYPE).dll $(USB_SRC)/win32/*.c -ldinput8

lib-out-path:
	@echo $(LIB_OUT_PATH)