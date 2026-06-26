package com.dsafileorganiser.source;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class JavaSourceScanner {
    private final JavaSourceMetadataExtractor javaSourceMetadataExtractor;

    public JavaSourceScanner() {
        this(new JavaSourceMetadataExtractor());
    }

    public JavaSourceScanner(JavaSourceMetadataExtractor javaSourceMetadataExtractor) {
        this.javaSourceMetadataExtractor = javaSourceMetadataExtractor;
    }

    public List<JavaSourceFile> scan(Path sourceRoot) throws IOException {
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(this::isJavaFile)
                    .sorted(Comparator.comparing(path -> sourceRoot.relativize(path).toString()))
                    .map(path -> readJavaSourceFile(sourceRoot, path))
                    .toList();
        }
    }

    private JavaSourceFile readJavaSourceFile(Path sourceRoot, Path sourcePath) {
        try {
            String content = Files.readString(sourcePath, StandardCharsets.UTF_8);
            return new JavaSourceFile(
                    sourcePath,
                    sourceRoot.relativize(sourcePath),
                    content,
                    javaSourceMetadataExtractor.extract(content)
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read Java file: " + sourcePath, exception);
        }
    }

    private boolean isJavaFile(Path path) {
        return path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".java");
    }
}
