package com.example.teampilot.document.model;

public record DocumentChunk(
        int chunkIndex,
        String content,
        String section
) {
}