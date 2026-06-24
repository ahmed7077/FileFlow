package com.dsafileorganiser.organiser;

import com.dsafileorganiser.duplicate.DuplicateCodeDetector;
import com.dsafileorganiser.duplicate.DuplicateDetectionResult;
import com.dsafileorganiser.output.OrganisedCodeWriter;
import com.dsafileorganiser.output.OrganisedJavaFile;
import com.dsafileorganiser.report.OrganisationReportWriter;
import com.dsafileorganiser.source.JavaSourceFile;
import com.dsafileorganiser.source.JavaSourceScanner;
import com.dsafileorganiser.zip.SafeZipExtractor;
import com.dsafileorganiser.zip.ZipArchiveService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class JavaCodeOrganiser {
    private final JavaSourceScanner javaSourceScanner;
    private final DuplicateCodeDetector duplicateCodeDetector;
    private final OrganisedCodeWriter organisedCodeWriter;
    private final OrganisationReportWriter organisationReportWriter;
    private final SafeZipExtractor safeZipExtractor;
    private final ZipArchiveService zipArchiveService;

    public JavaCodeOrganiser() {
        this(
                new JavaSourceScanner(),
                new DuplicateCodeDetector(),
                new OrganisedCodeWriter(),
                new OrganisationReportWriter(),
                new SafeZipExtractor(),
                new ZipArchiveService()
        );
    }

    public JavaCodeOrganiser(
            JavaSourceScanner javaSourceScanner,
            DuplicateCodeDetector duplicateCodeDetector,
            OrganisedCodeWriter organisedCodeWriter,
            OrganisationReportWriter organisationReportWriter,
            SafeZipExtractor safeZipExtractor,
            ZipArchiveService zipArchiveService
    ) {
        this.javaSourceScanner = javaSourceScanner;
        this.duplicateCodeDetector = duplicateCodeDetector;
        this.organisedCodeWriter = organisedCodeWriter;
        this.organisationReportWriter = organisationReportWriter;
        this.safeZipExtractor = safeZipExtractor;
        this.zipArchiveService = zipArchiveService;
    }

    public OrganisationSummary organise(
            Path inputPath,
            Path outputZipPath,
            boolean removeCommentsForDuplicateCheck
    ) throws IOException {
        if (!Files.exists(inputPath)) {
            throw new IllegalArgumentException("Input path does not exist: " + inputPath);
        }

        Path workingDirectory = Files.createTempDirectory("dsa-file-organiser-");
        try {
            Path sourceRoot = prepareSourceRoot(inputPath, workingDirectory);
            Path organisedRoot = workingDirectory.resolve("organised-java-codes");
            Files.createDirectories(organisedRoot);

            List<JavaSourceFile> sourceFiles = javaSourceScanner.scan(sourceRoot);
            DuplicateDetectionResult duplicateDetectionResult = duplicateCodeDetector.detectDuplicates(
                    sourceFiles,
                    removeCommentsForDuplicateCheck
            );
            List<OrganisedJavaFile> organisedJavaFiles = organisedCodeWriter.writeUniqueFiles(
                    duplicateDetectionResult.uniqueFiles(),
                    organisedRoot
            );

            organisationReportWriter.writeReport(
                    organisedRoot.resolve("organisation-report.txt"),
                    sourceFiles.size(),
                    organisedJavaFiles,
                    duplicateDetectionResult.duplicateFiles()
            );

            zipArchiveService.createZip(organisedRoot, outputZipPath);

            return new OrganisationSummary(
                    sourceFiles.size(),
                    duplicateDetectionResult.uniqueFiles().size(),
                    duplicateDetectionResult.duplicateFiles().size(),
                    outputZipPath
            );
        } finally {
            deleteRecursively(workingDirectory);
        }
    }

    private Path prepareSourceRoot(Path inputPath, Path workingDirectory) throws IOException {
        if (Files.isDirectory(inputPath)) {
            return inputPath;
        }

        if (Files.isRegularFile(inputPath) && isZipFile(inputPath)) {
            Path extractionDirectory = workingDirectory.resolve("extracted-input");
            Files.createDirectories(extractionDirectory);
            safeZipExtractor.extract(inputPath, extractionDirectory);
            return extractionDirectory;
        }

        throw new IllegalArgumentException("Input must be a folder or .zip file: " + inputPath);
    }

    private boolean isZipFile(Path path) {
        return path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".zip");
    }

    private void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(root)) {
            List<Path> orderedPaths = paths
                    .sorted(Comparator.reverseOrder())
                    .toList();

            for (Path path : orderedPaths) {
                Files.deleteIfExists(path);
            }
        }
    }
}
