
package com.example.teampilot.document.embedding.model;

import java.util.List;

public record EmbeddedChunk(
        int chunkIndex,
        String content,
        int dimensions,
        List<Float> vectorPreview
) {
}