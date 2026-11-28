package com.example.teampilot.document.model;

import java.util.List;

public record CrossEncoderRerankRequest(
        String query,
        List<Candidate> candidates
) {
    public record Candidate(
            String id,
            String content
    ) {}
}