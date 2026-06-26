package com.dsafileorganiser.output;

import com.dsafileorganiser.source.JavaSourceFile;
import com.dsafileorganiser.source.JavaSourceMetadata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class OrganisedCodeWriter {
    public List<OrganisedJavaFile> writeUniqueFiles(List<JavaSourceFile> uniqueFiles, Path organisedRoot)
            throws IOException {
        List<OrganisedJavaFile> organisedJavaFiles = new ArrayList<>();
        Set<Path> usedRelativePaths = new LinkedHashSet<>();

        for (JavaSourceFile uniqueFile : uniqueFiles) {
            Path requestedRelativePath = buildOrganisedRelativePath(uniqueFile);
            Path finalRelativePath = makeUniquePath(requestedRelativePath, usedRelativePaths);
            Path targetPath = organisedRoot.resolve(finalRelativePath).normalize();

            if (!targetPath.startsWith(organisedRoot.toAbsolutePath().normalize())
                    && !targetPath.startsWith(organisedRoot.normalize())) {
                throw new IOException("Refusing to write outside output folder: " + finalRelativePath);
            }

            Files.createDirectories(targetPath.getParent());
            Files.writeString(targetPath, uniqueFile.content(), StandardCharsets.UTF_8);
            organisedJavaFiles.add(new OrganisedJavaFile(uniqueFile, finalRelativePath));
        }

        return organisedJavaFiles;
    }

    private Path buildOrganisedRelativePath(JavaSourceFile sourceFile) {
        JavaSourceMetadata metadata = sourceFile.metadata();
        Path packageFolder = metadata.packageName()
                .map(packageName -> Path.of(packageName.replace('.', '/')))
                .orElse(Path.of("default-package"));

        String fileName = metadata.primaryTypeName()
                .map(typeName -> typeName + ".java")
                .orElse(sourceFile.originalPath().getFileName().toString());

        return packageFolder.resolve(sanitizeFileName(fileName));
    }

    private Path makeUniquePath(Path requestedPath, Set<Path> usedRelativePaths) {
        if (usedRelativePaths.add(requestedPath)) {
            return requestedPath;
        }

        Path parent = requestedPath.getParent();
        String fileName = requestedPath.getFileName().toString();
        String baseName = removeJavaExtension(fileName);
        String extension = fileName.endsWith(".java") ? ".java" : "";

        int copyIndex = 2;
        while (true) {
            Path candidate = parent.resolve(baseName + "-" + copyIndex + extension);
            if (usedRelativePaths.add(candidate)) {
                return candidate;
            }
            copyIndex++;
        }
    }

    private String removeJavaExtension(String fileName) {
        return fileName.endsWith(".java") ? fileName.substring(0, fileName.length() - ".java".length()) : fileName;
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
