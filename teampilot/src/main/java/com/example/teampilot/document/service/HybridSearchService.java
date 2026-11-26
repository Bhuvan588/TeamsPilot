package com.example.teampilot.document.service;

import com.example.teampilot.document.model.DocumentSearchResult;
import com.example.teampilot.document.model.HybridSearchResult;
import com.example.teampilot.document.model.KeywordSearchResult;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HybridSearchService {

    private static final int RRF_K = 60;

    private final DocumentSearchService documentSearchService;
    private final DocumentKeywordSearchService keywordSearchService;

    public HybridSearchService(
            DocumentSearchService documentSearchService,
            DocumentKeywordSearchService keywordSearchService) {
        this.documentSearchService = documentSearchService;
        this.keywordSearchService = keywordSearchService;
    }

    public List<HybridSearchResult> search(String query, int topK) {

        // 1. Retrieve results from both search systems
        List<DocumentSearchResult> vectorResults =
                documentSearchService.search(query);

        List<KeywordSearchResult> keywordResults =
                keywordSearchService.search(query, topK);

        // 2. Store and merge results using chunk UUID
        Map<String, ResultAccumulator> resultMap = new HashMap<>();

        // 3. Process vector search results
        for (int i = 0; i < vectorResults.size(); i++) {

            DocumentSearchResult result = vectorResults.get(i);

            ResultAccumulator accumulator = resultMap.computeIfAbsent(
                    result.id(),
                    id -> ResultAccumulator.fromVector(result)
            );

            accumulator.addScore(i + 1);
        }

        // 4. Process keyword search results
        for (int i = 0; i < keywordResults.size(); i++) {

            KeywordSearchResult result = keywordResults.get(i);

            ResultAccumulator accumulator = resultMap.computeIfAbsent(
                    result.id(),
                    id -> ResultAccumulator.fromKeyword(result)
            );

            accumulator.addScore(i + 1);
        }

        // 5. Sort by RRF score and return top K
        return resultMap.values()
                .stream()
                .map(ResultAccumulator::toResult)
                .sorted(Comparator.comparingDouble(
                        HybridSearchResult::rrfScore
                ).reversed())
                .limit(topK)
                .toList();
    }

    private static class ResultAccumulator {

        private final String id;
        private final String content;
        private final String filename;
        private final String documentId;
        private final Integer chunkIndex;
        private final String section;

        private double rrfScore;

        private ResultAccumulator(
                String id,
                String content,
                String filename,
                String documentId,
                Integer chunkIndex,
                String section) {

            this.id = id;
            this.content = content;
            this.filename = filename;
            this.documentId = documentId;
            this.chunkIndex = chunkIndex;
            this.section = section;
        }

        static ResultAccumulator fromVector(DocumentSearchResult result) {

            Map<String, Object> metadata = result.metadata();

            return new ResultAccumulator(
                    result.id(),
                    result.content(),
                    value(metadata, "filename"),
                    value(metadata, "documentId"),
                    integerValue(metadata, "chunkIndex"),
                    value(metadata, "section")
            );
        }

        static ResultAccumulator fromKeyword(KeywordSearchResult result) {

            return new ResultAccumulator(
                    result.id(),
                    result.content(),
                    result.filename(),
                    result.documentId(),
                    result.chunkIndex(),
                    result.section()
            );
        }

        void addScore(int rank) {
            this.rrfScore += 1.0 / (RRF_K + rank);
        }

        HybridSearchResult toResult() {
            return new HybridSearchResult(
                    id,
                    content,
                    filename,
                    documentId,
                    chunkIndex,
                    section,
                    rrfScore
            );
        }

        private static String value(
                Map<String, Object> metadata, String key) {

            Object value = metadata.get(key);
            return value == null ? null : value.toString();
        }

        private static Integer integerValue(
                Map<String, Object> metadata, String key) {

            Object value = metadata.get(key);
            return value == null ? null : Integer.valueOf(value.toString());
        }
    }
}