package com.example.teampilot.document.service;

import com.example.teampilot.document.model.KeywordSearchResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentKeywordSearchService {

    private final JdbcTemplate jdbcTemplate;

    public DocumentKeywordSearchService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<KeywordSearchResult> search(String query, int topK) {

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