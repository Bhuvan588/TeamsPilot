package com.example.teampilot.document.model;

public record HybridSearchResult(
        String id,
        String content,
        String filename,
        String documentId,
        Integer chunkIndex,
        String section,
        double rrfScore
) {}