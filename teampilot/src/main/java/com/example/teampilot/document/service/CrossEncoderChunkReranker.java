
package com.example.teampilot.document.service;

import com.example.teampilot.document.model.CrossEncoderRerankRequest;
import com.example.teampilot.document.model.CrossEncoderRerankResult;
import com.example.teampilot.document.model.HybridSearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CrossEncoderChunkReranker implements ChunkReranker {

    private static final Logger log =
            LoggerFactory.getLogger(CrossEncoderChunkReranker.class);

    private final RestClient restClient;
    private final String endpoint;

    public CrossEncoderChunkReranker(
            RestClient rerankerRestClient,
            @Value("${teampilot.reranker.endpoint:/rerank}")
            String endpoint) {

        this.restClient = rerankerRestClient;
        this.endpoint = endpoint;
    }

    @Override
    public List<HybridSearchResult> rerank(
            String query,
            List<HybridSearchResult> candidates,
            int topK) {

        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        if (topK <= 0) {
            throw new IllegalArgumentException(
                    "topK must be greater than zero");
        }

        try {

            // 1. Create request DTOs
            List<CrossEncoderRerankRequest.Candidate> requestCandidates =
                    candidates.stream()
                            .map(chunk ->
                                    new CrossEncoderRerankRequest.Candidate(
                                            chunk.id(),
                                            chunk.content()
                                    ))
                            .toList();

            CrossEncoderRerankRequest request =
                    new CrossEncoderRerankRequest(
                            query,
                            requestCandidates
                    );

            log.info(
                    "Sending {} candidates to cross-encoder",
                    candidates.size()
            );

            // 2. Call Python reranking service
            List<CrossEncoderRerankResult> response =
                    restClient.post()
                            .uri(endpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            List<CrossEncoderRerankResult>>() {}
                            );

            // 3. Validate response
            Map<String, Double> scores =
                    validateResponse(response, candidates);

            // 4. Log ranking results
            log.info("========== CROSS-ENCODER RESULTS ==========");

            candidates.stream()
                    .sorted(
                            Comparator.comparingDouble(
                                    (HybridSearchResult chunk) ->
                                            scores.get(chunk.id())
                            ).reversed()
                    )
                    .forEach(chunk -> log.info(
                            "Chunk: {}, Section: {}, Cross-encoder: {}, RRF: {}",
                            chunk.id(),
                            chunk.section(),
                            scores.get(chunk.id()),
                            chunk.rrfScore()
                    ));

            log.info("============================================");

            // 5. Return final topK based on cross-encoder score
            return candidates.stream()
                    .sorted(
                            Comparator
                                    .comparingDouble(
                                            (HybridSearchResult chunk) ->
                                                    scores.get(chunk.id())
                                    )
                                    .reversed()
                                    .thenComparing(
                                            Comparator.comparingDouble(
                                                    HybridSearchResult::rrfScore
                                            ).reversed()
                                    )
                    )
                    .limit(topK)
                    .toList();

        } catch (Exception exception) {

            log.error(
                    "Cross-encoder reranking failed. "
                            + "Falling back to RRF ranking. Reason: {}",
                    exception.getMessage()
            );

            return fallbackToRrf(candidates, topK);
        }
    }

    private Map<String, Double> validateResponse(
            List<CrossEncoderRerankResult> response,
            List<HybridSearchResult> candidates) {

        if (response == null) {
            throw new IllegalStateException(
                    "Cross-encoder returned an empty response");
        }

        // Build the set of valid candidate IDs
        Set<String> validIds = new HashSet<>();

        for (HybridSearchResult candidate : candidates) {
            if (!validIds.add(candidate.id())) {
                throw new IllegalStateException(
                        "Duplicate candidate ID: " + candidate.id());
            }
        }

        Map<String, Double> scores = new HashMap<>();

        for (CrossEncoderRerankResult result : response) {

            if (result == null || result.id() == null) {
                throw new IllegalStateException(
                        "Invalid result returned by cross-encoder");
            }

            if (!validIds.contains(result.id())) {
                throw new IllegalStateException(
                        "Unknown chunk ID returned: " + result.id());
            }

            if (!Double.isFinite(result.score())) {
                throw new IllegalStateException(
                        "Invalid relevance score for: " + result.id());
            }

            if (scores.putIfAbsent(
                    result.id(), result.score()) != null) {
                throw new IllegalStateException(
                        "Duplicate result ID: " + result.id());
            }
        }

        if (scores.size() != candidates.size()) {
            throw new IllegalStateException(
                    "Cross-encoder did not return a score for every candidate");
        }

        return scores;
    }

    private List<HybridSearchResult> fallbackToRrf(
            List<HybridSearchResult> candidates,
            int topK) {

        return candidates.stream()
                .sorted(
                        Comparator.comparingDouble(
                                HybridSearchResult::rrfScore
                        ).reversed()
                )
                .limit(topK)
                .toList();
    }
}
