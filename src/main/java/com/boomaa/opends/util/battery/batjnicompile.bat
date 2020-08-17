"C:\Program Files\TDM-GCC-64\bin\gcc" -m64 -c -I"C:\Program Files\Java\jdk-11.0.6\include" -I"C:\Program Files\Java\jdk-11.0.6\include\win32" -D__int64="long long" com_boomaa_opends_util_battery_Win32BatteryJNI.c
C:\MinGW-x64\bin\x86_64-w64-mingw32-gcc.exe -shared -I"C:\Program Files\Java\jdk-11.0.6\include" -I"C:\Program Files\Java\jdk-11.0.6\include\win32" -s -o ../../../../../../resources/batteryjni-win32.dll com_boomaa_opends_util_battery_Win32BatteryJNI.c -Wl,--add-stdcall-alias,--kill-at
rm com_boomaa_opends_util_battery_Win32BatteryJNI.o
PAUSE