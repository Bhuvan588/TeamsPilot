package com.example.teampilot.document.model;

import java.util.List;

public record RerankingResponse(
        List<ChunkRelevanceScore> scores
) {}