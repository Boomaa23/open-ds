USB_SRC := src/main/java/com/boomaa/opends/usb
LIB_OUT := src/main/resources
LIB_NAME := opends-lib

ifndef CC
	CC := gcc
endif

ifndef UNIX_JDK_INCLUDE_PATH
	UNIX_JDK_INCLUDE_PATH := /usr/lib/jvm/java-8-openjdk-amd64/include
endif
ifndef WIN32_JDK_INCLUDE_PATH
	WIN32_JDK_INCLUDE_PATH := "C:\\Program Files\\Eclipse Foundation\\jdk-8.0.302.8-hotspot\\include"
endif
ifndef VS_PATH
	VS_PATH := "C:\\Program Files (x86)\\Microsoft Visual Studio\\2019\\"
endif

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
		LIB_OUT_WIN32 = $(subst /,\\,$(LIB_OUT))
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
else
	CWD = $(shell pwd)
endif

ifeq ($(ARCH_TYPE),aarch64)
	ifeq ($(OS_TYPE),linux)
		CC = /usr/bin/aarch64-linux-musl-gcc
	endif
	ifeq ($(OS_TYPE),win32)
		CC = aarch64-w64-mingw32-gcc
	endif
endif
ifeq ($(ARCH_TYPE),amd64)
	ifeq ($(OS_TYPE),win32)
		CC = x86_64-w64-mingw32-gcc
	endif
endif

.PHONY: build check jar clean native native-all-docker native-linux native-osx native-win32

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
	docker run -v $(CWD):/opends -e ARCH_TYPE=amd64 -e OS_TYPE=linux  \
		opends-linux-build sh -c "make native-linux"
	docker run -v $(CWD):/opends -e ARCH_TYPE=aarch64 -e OS_TYPE=win32  \
    		opends-linux-build sh -c "make native-win32-gcc"
	docker run -v $(CWD):/opends -e ARCH_TYPE=amd64 -e OS_TYPE=win32  \
			opends-linux-build sh -c "make native-win32-gcc"

native-linux:
	$(CC) -Os -s -I$(UNIX_JDK_INCLUDE_PATH) -I$(UNIX_JDK_INCLUDE_PATH)/linux/ \
		-shared -o $(LIB_OUT)/$(LIB_NAME)-linux-$(ARCH_TYPE).so $(USB_SRC)/linux/*.c

native-osx:
	$(CC) -c -fPIC -I$(UNIX_JDK_INCLUDE_PATH) -I$(UNIX_JDK_INCLUDE_PATH)/darwin/ $(USB_SRC)/osx/*.c
	$(CC) -shared -framework IOKit -framework CoreServices -o $(LIB_OUT)/$(LIB_NAME)-osx-$(ARCH_TYPE).jnilib *.o
	rm com_boomaa_opends_usb_IOKit.o com_boomaa_opends_usb_IOKitDevice.o

native-win32:
	$(VS_PATH)\\BuildTools\\VC\\Auxiliary\\Build\\vcvars64.bat && \
	cl.exe /LD /I$(WIN32_JDK_INCLUDE_PATH) /I$(WIN32_JDK_INCLUDE_PATH)\\win32 $(USB_SRC)/win32/*.c /O1 /MD /Zc:inline
	del *.exp *.lib *.obj
	move /y com_boomaa_opends_usb_DirectInput.dll "$(LIB_OUT_WIN32)\\"
	del $(LIB_OUT_WIN32)\\$(LIB_NAME)-win32-$(ARCH_TYPE).dll
	ren $(LIB_OUT_WIN32)\\com_boomaa_opends_usb_DirectInput.dll $(LIB_NAME)-win32-$(ARCH_TYPE).dll

native-win32-gcc:
	$(CC) -Os -s -I$(UNIX_JDK_INCLUDE_PATH) -I$(UNIX_JDK_INCLUDE_PATH)/linux/ \
		-shared -o $(LIB_OUT)/$(LIB_NAME)-win32-$(ARCH_TYPE).dll $(USB_SRC)/win32/*.c -ldinput8
