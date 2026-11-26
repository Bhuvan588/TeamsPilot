package com.example.teampilot.document.model;


import java.util.List;

public record RagResponse(

        String question,
        String answer,
        List<SourceReference> sources
) {

    public record SourceReference(
            String filename,
            String documentId,
            Integer chunkIndex,
            String section,
            Double distance,
            String excerpt
    ){}
}
