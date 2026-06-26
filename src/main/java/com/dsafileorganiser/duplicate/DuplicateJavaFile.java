package com.dsafileorganiser.duplicate;

import com.dsafileorganiser.source.JavaSourceFile;

public record DuplicateJavaFile(
        JavaSourceFile duplicateFile,
        JavaSourceFile keptFile,
        String contentHash
) {
}
