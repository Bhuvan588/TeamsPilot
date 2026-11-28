package com.example.teampilot.document.model;

public record ChunkRelevanceScore(
        String chunkId,
        int relevanceScore
) {}