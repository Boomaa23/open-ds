gcc -c -fPIC -I/Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home/include/ -I/Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home/include/darwin/ com_boomaa_opends_usb_IOKit.c com_boomaa_opends_usb_IOKitDevice.c
gcc -shared -framework IOKit -framework CoreServices -o ods-input-osx.jnilib com_boomaa_opends_usb_IOKit.o com_boomaa_opends_usb_IOKitDevice.o
