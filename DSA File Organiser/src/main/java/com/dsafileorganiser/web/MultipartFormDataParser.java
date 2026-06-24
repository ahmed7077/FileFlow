package com.dsafileorganiser.web;

import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MultipartFormDataParser {
    private static final Pattern FORM_NAME_PATTERN = Pattern.compile("name=\"([^\"]+)\"");
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("filename=\"([^\"]*)\"");

    public MultipartFormData parse(HttpExchange exchange) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        String boundary = extractBoundary(contentType)
                .orElseThrow(() -> new IllegalArgumentException("Missing multipart boundary."));
        byte[] requestBytes = readAllBytes(exchange);

        Map<String, UploadedFormFile> files = new LinkedHashMap<>();
        Map<String, String> values = new LinkedHashMap<>();

        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.ISO_8859_1);
        int searchIndex = 0;

        while (true) {
            int boundaryIndex = indexOf(requestBytes, boundaryBytes, searchIndex);
            if (boundaryIndex < 0) {
                break;
            }

            int partStart = boundaryIndex + boundaryBytes.length;
            if (partStart + 1 < requestBytes.length
                    && requestBytes[partStart] == '-'
                    && requestBytes[partStart + 1] == '-') {
                break;
            }

            if (partStart + 1 < requestBytes.length
                    && requestBytes[partStart] == '\r'
                    && requestBytes[partStart + 1] == '\n') {
                partStart += 2;
            }

            int nextBoundaryIndex = indexOf(requestBytes, boundaryBytes, partStart);
            if (nextBoundaryIndex < 0) {
                break;
            }

            parsePart(Arrays.copyOfRange(requestBytes, partStart, trimPartEnd(requestBytes, nextBoundaryIndex)), files, values);
            searchIndex = nextBoundaryIndex;
        }

        return new MultipartFormData(files, values);
    }

    private void parsePart(
            byte[] partBytes,
            Map<String, UploadedFormFile> files,
            Map<String, String> values
    ) {
        byte[] separator = "\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);
        int separatorIndex = indexOf(partBytes, separator, 0);
        if (separatorIndex < 0) {
            return;
        }

        String headers = new String(partBytes, 0, separatorIndex, StandardCharsets.ISO_8859_1);
        byte[] content = Arrays.copyOfRange(partBytes, separatorIndex + separator.length, partBytes.length);

        Optional<String> formName = extractHeaderValue(FORM_NAME_PATTERN, headers);
        if (formName.isEmpty()) {
            return;
        }

        Optional<String> fileName = extractHeaderValue(FILE_NAME_PATTERN, headers);
        if (fileName.isPresent() && !fileName.get().isBlank()) {
            files.put(formName.get(), new UploadedFormFile(fileName.get(), content));
        } else {
            values.put(formName.get(), new String(content, StandardCharsets.UTF_8).trim());
        }
    }

    private Optional<String> extractBoundary(String contentType) {
        if (contentType == null) {
            return Optional.empty();
        }

        for (String token : contentType.split(";")) {
            String trimmedToken = token.trim();
            if (trimmedToken.startsWith("boundary=")) {
                return Optional.of(trimmedToken.substring("boundary=".length()).replace("\"", ""));
            }
        }
        return Optional.empty();
    }

    private Optional<String> extractHeaderValue(Pattern pattern, String headers) {
        Matcher matcher = pattern.matcher(headers);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }

    private byte[] readAllBytes(HttpExchange exchange) throws IOException {
        try (var requestBody = exchange.getRequestBody();
             var buffer = new ByteArrayOutputStream()) {
            requestBody.transferTo(buffer);
            return buffer.toByteArray();
        }
    }

    private int trimPartEnd(byte[] requestBytes, int nextBoundaryIndex) {
        int partEnd = nextBoundaryIndex;
        if (partEnd >= 2 && requestBytes[partEnd - 2] == '\r' && requestBytes[partEnd - 1] == '\n') {
            partEnd -= 2;
        }
        return partEnd;
    }

    private int indexOf(byte[] source, byte[] target, int startIndex) {
        outer:
        for (int sourceIndex = Math.max(0, startIndex); sourceIndex <= source.length - target.length; sourceIndex++) {
            for (int targetIndex = 0; targetIndex < target.length; targetIndex++) {
                if (source[sourceIndex + targetIndex] != target[targetIndex]) {
                    continue outer;
                }
            }
            return sourceIndex;
        }
        return -1;
    }
}
