package com.example.teampilot.document.model;

public record KeywordSearchResult(
        String id,
        String content,
        String filename,
        String documentId,
        Integer chunkIndex,
        String section,
        Double rank
) {}