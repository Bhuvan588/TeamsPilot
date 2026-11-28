package com.example.teampilot.document.service;

import com.example.teampilot.document.model.HybridSearchResult;

import java.util.List;

public interface ChunkReranker {

    List<HybridSearchResult> rerank(
            String query,
            List<HybridSearchResult> candidates,
            int topK
    );
}