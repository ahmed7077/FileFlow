package com.dsafileorganiser.organiser;

import java.nio.file.Path;

public record OrganisationSummary(
        int scannedFileCount,
        int uniqueFileCount,
        int duplicateFileCount,
        Path outputZipPath
) {
}
