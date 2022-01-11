package com.boomaa.opends.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class Libraries {
    private static final String[] NATIVE_LIBS = new String[] {
            "ods-input"
    };

    public static void init() {
        String tmpPath = System.getProperty("java.io.tmpdir");
        String fSep = System.getProperty("file.separator");
        if (!tmpPath.endsWith(fSep)) {
            tmpPath += fSep;
        }

        OperatingSystem os = OperatingSystem.getCurrent();
        String nLibExt = "-" + os.getCommonName() + ".";
        switch (OperatingSystem.getCurrent())  {
            case WINDOWS:
                nLibExt += "dll";
                break;
            case MACOS:
                nLibExt += "jnilib";
                break;
            case UNIX:
                nLibExt += "so";
                break;
            case UNSUPPORTED:
                throw OperatingSystem.unsupportedException();
        }

        for (String libName : NATIVE_LIBS) {
            try {
                libName += nLibExt;
                Files.copy(Objects.requireNonNull(Libraries.class.getResourceAsStream("/" + libName)),
                    Paths.get(tmpPath + libName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.load(tmpPath + libName);
        }
    }
}
