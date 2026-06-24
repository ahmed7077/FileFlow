package com.dsafileorganiser.zip;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.stream.Stream;

public final class ZipArchiveService {
    public void createZip(Path sourceDirectory, Path outputZipPath) throws IOException {
        Path absoluteOutputZipPath = outputZipPath.toAbsolutePath().normalize();
        if (absoluteOutputZipPath.getParent() != null) {
            Files.createDirectories(absoluteOutputZipPath.getParent());
        }
        Files.deleteIfExists(absoluteOutputZipPath);

        try (OutputStream outputStream = Files.newOutputStream(absoluteOutputZipPath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
             Stream<Path> paths = Files.walk(sourceDirectory)) {
            List<Path> filesToZip = paths
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(path -> sourceDirectory.relativize(path).toString()))
                    .toList();

            for (Path fileToZip : filesToZip) {
                Path relativePath = sourceDirectory.relativize(fileToZip);
                ZipEntry zipEntry = new ZipEntry(toZipPath(relativePath));
                zipOutputStream.putNextEntry(zipEntry);
                Files.copy(fileToZip, zipOutputStream);
                zipOutputStream.closeEntry();
            }
        }
    }

    private String toZipPath(Path path) {
        return path.toString().replace('\\', '/');
    }
}
