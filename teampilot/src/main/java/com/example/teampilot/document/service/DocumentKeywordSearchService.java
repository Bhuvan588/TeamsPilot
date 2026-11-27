package com.example.teampilot.document.service;

import com.example.teampilot.document.model.DocumentSearchFilter;
import com.example.teampilot.document.model.KeywordSearchResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentKeywordSearchService {

    private final JdbcTemplate jdbcTemplate;

    public DocumentKeywordSearchService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<KeywordSearchResult> search(
            String query,
            int topK,
            DocumentSearchFilter filter) {

        if (topK <= 0) {
            throw new IllegalArgumentException("topK must be greater than zero");
        }

        StringBuilder sql = new StringBuilder("""
            SELECT
                id::text AS id,
                content,
                metadata->>'filename' AS filename,
                metadata->>'documentId' AS document_id,
                (metadata->>'chunkIndex')::int AS chunk_index,
                metadata->>'section' AS section,
                ts_rank(
                    to_tsvector('english', content),
                    websearch_to_tsquery('english', ?)
                ) AS rank
            FROM vector_store
            WHERE to_tsvector('english', content)
                  @@ websearch_to_tsquery('english', ?)
            """);

        List<Object> params = new ArrayList<>();

        params.add(query);
        params.add(query);

        // Apply document ID filter
        if (filter != null &&
                filter.documentId() != null &&
                !filter.documentId().isBlank()) {

            sql.append(" AND metadata->>'documentId' = ?");
            params.add(filter.documentId());
        }

        // Apply section filter
        if (filter != null &&
                filter.section() != null &&
                !filter.section().isBlank()) {

            sql.append(" AND metadata->>'section' = ?");
            params.add(filter.section());
        }

        sql.append(" ORDER BY rank DESC LIMIT ?");
        params.add(topK);

        return jdbcTemplate.query(
                sql.toString(),
                (rs, rowNum) -> new KeywordSearchResult(
                        rs.getString("id"),
                        rs.getString("content"),
                        rs.getString("filename"),
                        rs.getString("document_id"),
                        rs.getInt("chunk_index"),
                        rs.getString("section"),
                        rs.getDouble("rank")
                ),
                params.toArray()
        );
    }public List<KeywordSearchResult> search(String query, int topK) {

        String sql = """
                SELECT
                    id::text AS id,
                    content,
                    metadata->>'filename' AS filename,
                    metadata->>'documentId' AS document_id,
                    (metadata->>'chunkIndex')::int AS chunk_index,
                    metadata->>'section' AS section,
                    ts_rank(
                        to_tsvector('english', content),
                        websearch_to_tsquery('english', ?)
                    ) AS rank
                FROM vector_store
                WHERE to_tsvector('english', content)
                      @@ websearch_to_tsquery('english', ?)
                ORDER BY rank DESC
                LIMIT ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new KeywordSearchResult(
                        rs.getString("id"),
                        rs.getString("content"),
                        rs.getString("filename"),
                        rs.getString("document_id"),
                        rs.getInt("chunk_index"),
                        rs.getString("section"),
                        rs.getDouble("rank")
                ),
                query,
                query,
                topK
        );
    }
}