gcc -c -fPIC -I/usr/lib/jvm/default/include -I/usr/lib/jvm/default/include/linux com_boomaa_opends_usb_IOKit.c com_boomaa_opends_usb_IOKitDevice.c
gcc -shared -o ods-input-linux.jnilib com_boomaa_opends_usb_IOKit.o com_boomaa_opends_usb_IOKitDevice.o
