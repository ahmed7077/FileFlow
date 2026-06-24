package com.dsafileorganiser.web;

public record UploadedFormFile(
        String fileName,
        byte[] content
) {
}
