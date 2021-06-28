package com.boomaa.opends.util;

import com.boomaa.opends.usb.USBInterface;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Libs {
    private static final String[] NATIVE_LIBS = new String[] {
            "ods-input"
    };

    public static void init() {
        String tmpPath = System.getProperty("java.io.tmpdir");
        Path jarRoot = getJarPath();

        OperatingSystem os = OperatingSystem.getCurrent();
        String nLibExt = "-" + os.getCommonName() + ".";
        switch (OperatingSystem.getCurrent())  {
            case WINDOWS:
                nLibExt += "dll";
                break;
            case MACOS:
            case UNIX:
                nLibExt += ".so";
                break;
            case UNSUPPORTED:
                throw new UnsupportedOperationException("Operating system not supported. Switch to Windows/UNIX");
        }

        for (String libName : NATIVE_LIBS) {
            try {
                libName += nLibExt;
                Files.copy(jarRoot.resolve(libName), Paths.get(tmpPath + libName));
            } catch (FileAlreadyExistsException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.load(tmpPath + libName);
        }
    }

    public static Path getJarPath() {
        try {
            return new File(USBInterface.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI())
                    .getAbsoluteFile().toPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
