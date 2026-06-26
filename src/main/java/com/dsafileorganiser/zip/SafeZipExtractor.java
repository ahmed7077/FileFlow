package com.dsafileorganiser.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class SafeZipExtractor {
    public void extract(Path zipPath, Path destinationDirectory) throws IOException {
        Path safeDestinationRoot = destinationDirectory.toAbsolutePath().normalize();
        Files.createDirectories(safeDestinationRoot);

        try (InputStream inputStream = Files.newInputStream(zipPath);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                Path targetPath = safeDestinationRoot.resolve(zipEntry.getName()).normalize();
                if (!targetPath.startsWith(safeDestinationRoot)) {
                    throw new IOException("Unsafe ZIP entry detected: " + zipEntry.getName());
                }

                if (zipEntry.isDirectory()) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(zipInputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zipInputStream.closeEntry();
            }
        }
    }
}
