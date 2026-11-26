package com.example.teampilot.document.chunking;

import com.example.teampilot.document.model.DocumentChunk;
import com.example.teampilot.document.model.ExtractedDocument;

import java.util.List;

public interface ChunkingStrategy {

    boolean supports(ExtractedDocument document);

    List<DocumentChunk> chunk(ExtractedDocument document);
}
