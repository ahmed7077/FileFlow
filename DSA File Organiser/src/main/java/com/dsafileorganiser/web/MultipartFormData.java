package com.dsafileorganiser.web;

import java.util.Map;
import java.util.Optional;

public record MultipartFormData(
        Map<String, UploadedFormFile> files,
        Map<String, String> values
) {
    public Optional<UploadedFormFile> file(String name) {
        return Optional.ofNullable(files.get(name));
    }

    public Optional<String> value(String name) {
        return Optional.ofNullable(values.get(name));
    }
}
