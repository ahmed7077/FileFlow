package com.dsafileorganiser.source;

import com.dsafileorganiser.normalise.JavaCodeNormalizer;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JavaSourceMetadataExtractor {
    private static final Pattern PACKAGE_PATTERN = Pattern.compile(
            "(?m)^\\s*package\\s+([a-zA-Z_$][\\w$]*(?:\\.[a-zA-Z_$][\\w$]*)*)\\s*;"
    );
    private static final Pattern PUBLIC_TYPE_PATTERN = Pattern.compile(
            "\\bpublic\\s+(?:abstract\\s+|final\\s+|sealed\\s+|non-sealed\\s+)?(?:class|interface|enum|record)\\s+([a-zA-Z_$][\\w$]*)\\b"
    );
    private static final Pattern ANY_TYPE_PATTERN = Pattern.compile(
            "\\b(?:abstract\\s+|final\\s+|sealed\\s+|non-sealed\\s+|static\\s+)?(?:class|interface|enum|record)\\s+([a-zA-Z_$][\\w$]*)\\b"
    );

    private final JavaCodeNormalizer javaCodeNormalizer;

    public JavaSourceMetadataExtractor() {
        this(new JavaCodeNormalizer());
    }

    public JavaSourceMetadataExtractor(JavaCodeNormalizer javaCodeNormalizer) {
        this.javaCodeNormalizer = javaCodeNormalizer;
    }

    public JavaSourceMetadata extract(String code) {
        String codeWithoutComments = javaCodeNormalizer.stripComments(code);
        return new JavaSourceMetadata(
                findPackageName(codeWithoutComments),
                findPrimaryTypeName(codeWithoutComments)
        );
    }

    private Optional<String> findPackageName(String code) {
        Matcher matcher = PACKAGE_PATTERN.matcher(code);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }

    private Optional<String> findPrimaryTypeName(String code) {
        Matcher publicTypeMatcher = PUBLIC_TYPE_PATTERN.matcher(code);
        if (publicTypeMatcher.find()) {
            return Optional.of(publicTypeMatcher.group(1));
        }

        Matcher anyTypeMatcher = ANY_TYPE_PATTERN.matcher(code);
        if (anyTypeMatcher.find()) {
            return Optional.of(anyTypeMatcher.group(1));
        }

        return Optional.empty();
    }
}
