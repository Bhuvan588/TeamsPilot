package com.example.teampilot.document.service;

import com.example.teampilot.document.chunking.ChunkingStrategy;
import com.example.teampilot.document.model.DocumentChunk;
import com.example.teampilot.document.model.ExtractedDocument;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentChunkingService {

    private final List<ChunkingStrategy> strategies;

    public DocumentChunkingService(
            List<ChunkingStrategy> strategies) {
        this.strategies = strategies;
    }

    public List<DocumentChunk> chunk(
            ExtractedDocument document) {

        return strategies.stream()
                .filter(strategy -> strategy.supports(document))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No suitable chunking strategy found"
                        ))
                .chunk(document);
    }
}