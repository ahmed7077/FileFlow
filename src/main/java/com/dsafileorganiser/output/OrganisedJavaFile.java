package com.dsafileorganiser.output;

import com.dsafileorganiser.source.JavaSourceFile;

import java.nio.file.Path;

public record OrganisedJavaFile(
        JavaSourceFile sourceFile,
        Path organisedRelativePath
) {
}
