package com.dsafileorganiser.duplicate;

import com.dsafileorganiser.hash.Sha256HashGenerator;
import com.dsafileorganiser.normalise.JavaCodeNormalizer;
import com.dsafileorganiser.source.JavaSourceFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DuplicateCodeDetector {
    private final JavaCodeNormalizer javaCodeNormalizer;
    private final Sha256HashGenerator sha256HashGenerator;

    public DuplicateCodeDetector() {
        this(new JavaCodeNormalizer(), new Sha256HashGenerator());
    }

    public DuplicateCodeDetector(JavaCodeNormalizer javaCodeNormalizer, Sha256HashGenerator sha256HashGenerator) {
        this.javaCodeNormalizer = javaCodeNormalizer;
        this.sha256HashGenerator = sha256HashGenerator;
    }

    public DuplicateDetectionResult detectDuplicates(
            List<JavaSourceFile> sourceFiles,
            boolean removeCommentsForDuplicateCheck
    ) {
        Map<String, JavaSourceFile> firstFileByHash = new LinkedHashMap<>();
        List<JavaSourceFile> uniqueFiles = new ArrayList<>();
        List<DuplicateJavaFile> duplicateFiles = new ArrayList<>();

        for (JavaSourceFile sourceFile : sourceFiles) {
            String normalizedCode = javaCodeNormalizer.normalize(
                    sourceFile.content(),
                    removeCommentsForDuplicateCheck
            );
            String codeHash = sha256HashGenerator.generateHash(normalizedCode);

            JavaSourceFile existingFile = firstFileByHash.get(codeHash);
            if (existingFile == null) {
                firstFileByHash.put(codeHash, sourceFile);
                uniqueFiles.add(sourceFile);
            } else {
                duplicateFiles.add(new DuplicateJavaFile(sourceFile, existingFile, codeHash));
            }
        }

        return new DuplicateDetectionResult(uniqueFiles, duplicateFiles);
    }
}
