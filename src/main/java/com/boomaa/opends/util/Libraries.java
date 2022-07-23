package com.boomaa.opends.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Libraries {
    private static final String[] NATIVE_LIBS = new String[] {
            "opends-lib"
    };

    private Libraries() {
    }

    public static void init() {
        String tmpPath = OperatingSystem.getTempFolder();
        Debug.println("Temporary directory located at: " + tmpPath);

        OperatingSystem os = OperatingSystem.getCurrent();
        Debug.println("Detected operating system: " + os);
        Architecture arch = Architecture.getCurrent();
        Debug.println("Detected architecture: " + arch);
        String nLibExt = "-" + os.getCommonName() + "-" + arch.toString() + "." + os.getNativeExt();

        for (String libName : NATIVE_LIBS) {
            try {
                libName += nLibExt;
                InputStream libData = Libraries.class.getResourceAsStream("/" + libName);
                if (libData == null) {
                    throw new NativeSystemError("Could not find native file (invalid system configuration): " + libName);
                }
                Files.copy(libData, Paths.get(tmpPath + libName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.load(tmpPath + libName);
            Debug.println("Loaded " + libName);
        }
        Debug.println("Native input libraries loaded");
    }
}
