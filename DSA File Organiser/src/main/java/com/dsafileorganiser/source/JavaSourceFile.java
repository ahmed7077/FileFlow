package com.dsafileorganiser.source;

import java.nio.file.Path;

public record JavaSourceFile(
        Path originalPath,
        Path relativePath,
        String content,
        JavaSourceMetadata metadata
) {
}
