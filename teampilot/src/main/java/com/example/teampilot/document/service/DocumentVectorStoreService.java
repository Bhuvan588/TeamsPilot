package com.example.teampilot.document.service;

import com.example.teampilot.document.model.DocumentChunk;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentVectorStoreService {

    private final VectorStore vectorStore;

    public DocumentVectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void storeChunks(
            List<DocumentChunk> chunks,
            String documentId,
            String filename) {

        List<Document> documents = chunks.stream()
                .map(chunk -> new Document(
                        chunk.content(),
                        Map.of(
                                "documentId", documentId,
                                "filename", filename,
                                "chunkIndex", chunk.chunkIndex(),
                                "section", chunk.section()==null ? "GENERAL" : chunk.section()
                        )
                ))
                .toList();

        vectorStore.add(documents);
    }
}