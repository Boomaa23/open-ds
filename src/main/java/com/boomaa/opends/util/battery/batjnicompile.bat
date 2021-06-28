"C:\Program Files\TDM-GCC-64\bin\gcc" -m64 -c -I"C:\Program Files\Java\jdk1.8.0_251\include" -I"C:\Program Files\Java\jdk1.8.0_251\include\win32" -D__int64="long long" Win32BatteryJNI.c
C:\MinGW-x64\bin\x86_64-w64-mingw32-gcc.exe -shared -I"C:\Program Files\Java\jdk1.8.0_251\include" -I"C:\Program Files\Java\jdk1.8.0_251\include\win32" -s -o ../../../../../../resources/batteryjni-win32.dll Win32BatteryJNI.c -Wl,--add-stdcall-alias,--kill-at
rm Win32BatteryJNI.o
PAUSE