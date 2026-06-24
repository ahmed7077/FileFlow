package com.dsafileorganiser.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class StaticFileHttpHandler implements HttpHandler {
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            ".html", "text/html; charset=utf-8",
            ".css", "text/css; charset=utf-8",
            ".js", "application/javascript; charset=utf-8"
    );

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpResponseWriter.writeText(exchange, 405, "Method not allowed");
            return;
        }

        String requestPath = exchange.getRequestURI().getPath();
        String resourcePath = "/".equals(requestPath) ? "index.html" : requestPath.substring(1);

        if (resourcePath.contains("..")) {
            HttpResponseWriter.writeText(exchange, 400, "Invalid path");
            return;
        }

        byte[] content = readStaticResource(resourcePath);
        if (content.length == 0) {
            HttpResponseWriter.writeText(exchange, 404, "Not found");
            return;
        }

        HttpResponseWriter.writeBytes(
                exchange,
                200,
                List.of(new HttpHeader("Content-Type", contentTypeFor(resourcePath))),
                content
        );
    }

    private byte[] readStaticResource(String resourcePath) throws IOException {
        String classpathResource = "/static/" + resourcePath;
        try (InputStream inputStream = StaticFileHttpHandler.class.getResourceAsStream(classpathResource)) {
            if (inputStream != null) {
                return inputStream.readAllBytes();
            }
        }

        Path developmentResourcePath = Path.of("src", "main", "resources", "static", resourcePath);
        if (Files.exists(developmentResourcePath) && Files.isRegularFile(developmentResourcePath)) {
            return Files.readAllBytes(developmentResourcePath);
        }

        return new byte[0];
    }

    private String contentTypeFor(String resourcePath) {
        for (Map.Entry<String, String> entry : CONTENT_TYPES.entrySet()) {
            if (resourcePath.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "application/octet-stream";
    }
}
