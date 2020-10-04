package com.boomaa.opends.util;

import com.boomaa.opends.display.frames.MessageBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipUtils {
    public static void unzip(String zipLoc, String unzipDir) {
        File destDir = new File(unzipDir);
        FileInputStream fis;
        try {
            fis = new FileInputStream(zipLoc);
        } catch (FileNotFoundException e0) {
            String tmpDir = System.getProperty("java.io.tmpdir");
            try {
                Libraries.download(tmpDir + "/open-ds-natives.zip");
                unzip(tmpDir + "/open-ds-natives.zip", tmpDir);
                return;
            } catch (IOException e1) {
                MessageBox.show("Cannot unzip libraries", MessageBox.Type.ERROR);
                return;
            }
        }
        try (ZipInputStream zis = new ZipInputStream(fis)) {
            byte[] buffer = new byte[1024];
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                try (FileOutputStream fos = new FileOutputStream(newFile(destDir, zipEntry))) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                } catch (FileNotFoundException ignored) {
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (IOException e) {
            MessageBox.show("Cannot unzip libraries", MessageBox.Type.ERROR);
            e.printStackTrace();
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
