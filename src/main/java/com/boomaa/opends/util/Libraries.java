package com.boomaa.opends.util;

import com.boomaa.opends.usb.USBInterface;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
                Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
                fieldSysPath.setAccessible(true);
                fieldSysPath.set(null, null);
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

    public static void download(String loc) throws IOException {
        InputStream in = new URL("https://s3.amazonaws.com/s3.edu20.org/files/2796766/open-ds-natives.zip").openStream();
        Files.copy(in, Paths.get(loc), StandardCopyOption.REPLACE_EXISTING);
    }
}
