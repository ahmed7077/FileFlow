package com.dsafileorganiser.source;

import java.util.Optional;

public record JavaSourceMetadata(
        Optional<String> packageName,
        Optional<String> primaryTypeName
) {
}
