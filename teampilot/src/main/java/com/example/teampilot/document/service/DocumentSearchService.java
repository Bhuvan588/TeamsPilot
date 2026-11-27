
package com.example.teampilot.document.service;

import com.example.teampilot.document.model.DocumentSearchFilter;
import com.example.teampilot.document.model.DocumentSearchResult;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentSearchService {

    private final VectorStore vectorStore;

    public DocumentSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<DocumentSearchResult> search(String query) {
        return search(query, 5, null);
    }

    public List<DocumentSearchResult> search(
            String query,
            int topK,
            DocumentSearchFilter filter) {

        if (topK <= 0) {
            throw new IllegalArgumentException(
                    "topK must be greater than zero"
            );
        }

        SearchRequest.Builder requestBuilder = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(0.45);

        if (filter != null) {

            FilterExpressionBuilder builder =
                    new FilterExpressionBuilder();

            Filter.Expression expression = null;

            boolean hasDocumentId = filter.documentId() != null
                    && !filter.documentId().isBlank();

            boolean hasSection = filter.section() != null
                    && !filter.section().isBlank();

            if (hasDocumentId && hasSection) {
                expression = builder.and(
                        builder.eq("documentId", filter.documentId()),
                        builder.eq("section", filter.section())
                ).build();

            } else if (hasDocumentId) {
                expression = builder.eq(
                        "documentId", filter.documentId()
                ).build();

            } else if (hasSection) {
                expression = builder.eq(
                        "section", filter.section()
                ).build();
            }

            if (expression != null) {
                requestBuilder.filterExpression(expression);
            }
        }

        SearchRequest searchRequest = requestBuilder.build();

        List<Document> documents =
                vectorStore.similaritySearch(searchRequest);

        return documents.stream()
                .map(document -> new DocumentSearchResult(
                        document.getId(),
                        document.getText(),
                        document.getMetadata()
                ))
                .toList();
    }
}
