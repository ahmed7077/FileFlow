package com.dsafileorganiser.report;

import com.dsafileorganiser.duplicate.DuplicateJavaFile;
import com.dsafileorganiser.output.OrganisedJavaFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

public final class OrganisationReportWriter {
    public void writeReport(
            Path reportPath,
            int scannedFileCount,
            List<OrganisedJavaFile> organisedJavaFiles,
            List<DuplicateJavaFile> duplicateJavaFiles
    ) throws IOException {
        StringBuilder report = new StringBuilder();
        report.append("DSA File Organiser Report\n");
        report.append("Generated: ").append(OffsetDateTime.now()).append("\n\n");
        report.append("Java files scanned: ").append(scannedFileCount).append("\n");
        report.append("Unique Java files kept: ").append(organisedJavaFiles.size()).append("\n");
        report.append("Duplicate Java files removed: ").append(duplicateJavaFiles.size()).append("\n\n");

        report.append("Organised files\n");
        if (organisedJavaFiles.isEmpty()) {
            report.append("- No Java files were found.\n");
        } else {
            for (OrganisedJavaFile organisedJavaFile : organisedJavaFiles) {
                report.append("- ")
                        .append(toZipPath(organisedJavaFile.organisedRelativePath()))
                        .append(" <- ")
                        .append(organisedJavaFile.sourceFile().relativePath())
                        .append("\n");
            }
        }

        report.append("\nRemoved duplicates\n");
        if (duplicateJavaFiles.isEmpty()) {
            report.append("- No duplicates were found.\n");
        } else {
            for (DuplicateJavaFile duplicateJavaFile : duplicateJavaFiles) {
                report.append("- Removed: ")
                        .append(duplicateJavaFile.duplicateFile().relativePath())
                        .append("\n");
                report.append("  Kept: ")
                        .append(duplicateJavaFile.keptFile().relativePath())
                        .append("\n");
                report.append("  Hash: ")
                        .append(duplicateJavaFile.contentHash())
                        .append("\n");
            }
        }

        Files.createDirectories(reportPath.getParent());
        Files.writeString(reportPath, report.toString(), StandardCharsets.UTF_8);
    }

    private String toZipPath(Path path) {
        return path.toString().replace('\\', '/');
    }
}
