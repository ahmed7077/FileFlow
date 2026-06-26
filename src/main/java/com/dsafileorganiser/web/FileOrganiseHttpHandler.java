package com.dsafileorganiser.web;

import com.dsafileorganiser.organiser.JavaCodeOrganiser;
import com.dsafileorganiser.organiser.OrganisationSummary;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public final class FileOrganiseHttpHandler implements HttpHandler {
    private final JavaCodeOrganiser javaCodeOrganiser;
    private final MultipartFormDataParser multipartFormDataParser;

    public FileOrganiseHttpHandler() {
        this(new JavaCodeOrganiser(), new MultipartFormDataParser());
    }

    public FileOrganiseHttpHandler(
            JavaCodeOrganiser javaCodeOrganiser,
            MultipartFormDataParser multipartFormDataParser
    ) {
        this.javaCodeOrganiser = javaCodeOrganiser;
        this.multipartFormDataParser = multipartFormDataParser;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpResponseWriter.writeText(exchange, 405, "Method not allowed");
            return;
        }

        Path requestDirectory = Files.createTempDirectory("dsa-web-upload-");
        try {
            MultipartFormData formData = multipartFormDataParser.parse(exchange);
            UploadedFormFile uploadedFile = formData.file("file")
                    .orElseThrow(() -> new IllegalArgumentException("Please upload a ZIP file containing Java codes."));

            if (!uploadedFile.fileName().toLowerCase(Locale.ROOT).endsWith(".zip")) {
                throw new IllegalArgumentException("Only .zip uploads are supported by the web UI.");
            }

            Path uploadedZipPath = requestDirectory.resolve("uploaded-java-codes.zip");
            Path outputZipPath = requestDirectory.resolve("organised-java-codes.zip");
            Files.write(uploadedZipPath, uploadedFile.content());

            boolean keepCommentsInHash = formData.value("keepCommentsInHash")
                    .map(Boolean::parseBoolean)
                    .orElse(false);

            OrganisationSummary summary = javaCodeOrganiser.organise(
                    uploadedZipPath,
                    outputZipPath,
                    !keepCommentsInHash
            );

            byte[] zipBytes = Files.readAllBytes(summary.outputZipPath());
            List<HttpHeader> headers = List.of(
                    new HttpHeader("Content-Type", "application/octet-stream"),
                    new HttpHeader("Content-Disposition", "attachment; filename=\"organised-java-codes.zip\""),
                    new HttpHeader("X-Java-Files-Scanned", String.valueOf(summary.scannedFileCount())),
                    new HttpHeader("X-Unique-Files-Kept", String.valueOf(summary.uniqueFileCount())),
                    new HttpHeader("X-Duplicate-Files-Removed", String.valueOf(summary.duplicateFileCount()))
            );
            HttpResponseWriter.writeBytes(exchange, 200, headers, zipBytes);
        } catch (IllegalArgumentException exception) {
            HttpResponseWriter.writeJson(exchange, 400, "{\"message\":\"" + escapeJson(exception.getMessage()) + "\"}");
        } catch (Exception exception) {
            HttpResponseWriter.writeJson(exchange, 500, "{\"message\":\"" + escapeJson(exception.getMessage()) + "\"}");
        } finally {
            deleteRequestDirectory(requestDirectory);
        }
    }

    private void deleteRequestDirectory(Path requestDirectory) throws IOException {
        if (!Files.exists(requestDirectory)) {
            return;
        }

        try (var paths = Files.walk(requestDirectory)) {
            for (Path path : paths.sorted((left, right) -> right.compareTo(left)).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
