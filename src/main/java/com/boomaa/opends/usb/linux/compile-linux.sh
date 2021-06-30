gcc -c -fPIC -I/usr/lib/jvm/default/include -I/usr/lib/jvm/default/include/linux com_boomaa_opends_usb_LinuxController.c
gcc -shared -o ods-input-linux.so com_boomaa_opends_usb_LinuxController.o
