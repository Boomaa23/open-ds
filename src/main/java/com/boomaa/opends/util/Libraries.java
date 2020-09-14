package com.boomaa.opends.util;

import com.boomaa.opends.usb.USBInterface;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.URISyntaxException;

public class Libraries {
    private static boolean hasLibraryInit = false;

    // Workaround to JInput natives not being bundled with jinput
    //  extracts all natives to system temp folder from jar
    //  throws an illegal reflection error (not exception)
    public static void init(boolean force) {
        if (!hasLibraryInit || force) {
            String tmpPath = System.getProperty("java.io.tmpdir");
            UnzipUtils.unzip(getJarPath(), tmpPath);
            System.setProperty("java.library.path", tmpPath);
            try {
                MethodHandles.Lookup cl = MethodHandles.privateLookupIn(ClassLoader.class, MethodHandles.lookup());
                VarHandle sys_paths = cl.findStaticVarHandle(ClassLoader.class, "sys_paths", String[].class);
                sys_paths.set(null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            hasLibraryInit = true;
        }
    }

    public static String getJarPath() {
        try {
            return new File(USBInterface.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
