package com.dsafileorganiser.duplicate;

import com.dsafileorganiser.source.JavaSourceFile;

import java.util.List;

public record DuplicateDetectionResult(
        List<JavaSourceFile> uniqueFiles,
        List<DuplicateJavaFile> duplicateFiles
) {
}
