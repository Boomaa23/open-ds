@echo off
"C:\Program Files\TDM-GCC-64\bin\gcc" -m64 -c -I"C:\Program Files\Java\jdk1.8.0_251\include" -I"C:\Program Files\Java\jdk1.8.0_251\include\win32" -D__int64="long long" com_boomaa_opends_usb_input_DirectInput.c
"C:\Program Files\TDM-GCC-64\bin\gcc" -m64 -c -I"C:\Program Files\Java\jdk1.8.0_251\include" -I"C:\Program Files\Java\jdk1.8.0_251\include\win32" -D__int64="long long" com_boomaa_opends_usb_input_DirectInputDevice.c
"C:\Program Files\TDM-GCC-64\bin\gcc" -m64 -c -I"C:\Program Files\Java\jdk1.8.0_251\include" -I"C:\Program Files\Java\jdk1.8.0_251\include\win32" -D__int64="long long" com_boomaa_opends_usb_input_PlaceholderWindow.c
"C:\Program Files\TDM-GCC-64\bin\gcc" -m64 -c -I"C:\Program Files\Java\jdk1.8.0_251\include" -I"C:\Program Files\Java\jdk1.8.0_251\include\win32" -D__int64="long long" win32util.c
"C:\Program Files\TDM-GCC-64\bin\gcc" -m64 -D__int64="long long" -shared -s -l dinput8 -l dxguid -I"C:\Program Files\Java\jdk1.8.0_251\include" -I"C:\Program Files\Java\jdk1.8.0_251\include\win32" win32util.c com_boomaa_opends_usb_input_DirectInput.c com_boomaa_opends_usb_input_DirectInputDevice.c com_boomaa_opends_usb_input_PlaceholderWindow.c -Wl,--add-stdcall-alias,--kill-at -o ../../../../../../resources/ods-input.dll
REM rm com_boomaa_opends_usb_input_DirectInput.o
REM rm com_boomaa_opends_usb_input_DirectInputDevice.o
REM rm com_boomaa_opends_usb_input_PlaceholderWindow.o
REM rm win32util.o