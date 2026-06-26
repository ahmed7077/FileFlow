package com.dsafileorganiser.web;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class HttpResponseWriter {
    private HttpResponseWriter() {
    }

    public static void writeText(HttpExchange exchange, int statusCode, String text) throws IOException {
        writeBytes(
                exchange,
                statusCode,
                List.of(new HttpHeader("Content-Type", "text/plain; charset=utf-8")),
                text.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static void writeJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        writeBytes(
                exchange,
                statusCode,
                List.of(new HttpHeader("Content-Type", "application/json; charset=utf-8")),
                json.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static void writeBytes(
            HttpExchange exchange,
            int statusCode,
            List<HttpHeader> headers,
            byte[] bytes
    ) throws IOException {
        for (HttpHeader header : headers) {
            exchange.getResponseHeaders().set(header.name(), header.value());
        }
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (var responseBody = exchange.getResponseBody()) {
            responseBody.write(bytes);
        }
    }
}
