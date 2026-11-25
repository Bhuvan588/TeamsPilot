
package com.example.teampilot.document.service;

import com.example.teampilot.document.model.DocumentSearchResult;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentSearchService {

    private final VectorStore vectorStore;

    public DocumentSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<DocumentSearchResult> search(String query) {

        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(5)
                .build();

        List<Document> results =
                vectorStore.similaritySearch(searchRequest);

        return results.stream()
                .map(document -> new DocumentSearchResult(
                        document.getId(),
                        document.getText(),
                        document.getMetadata()
                ))
                .toList();
    }
}
