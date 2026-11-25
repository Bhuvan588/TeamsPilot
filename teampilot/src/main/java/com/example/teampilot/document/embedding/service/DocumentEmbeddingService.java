package com.example.teampilot.document.embedding.service;

import com.example.teampilot.document.embedding.model.EmbeddedChunk;
import com.example.teampilot.document.model.DocumentChunk;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class DocumentEmbeddingService {

    private final EmbeddingModel embeddingModel;

    public DocumentEmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public List<EmbeddedChunk> generateEmbeddings(
            List<DocumentChunk> chunks) {

        if (chunks == null || chunks.isEmpty()) {
            throw new IllegalArgumentException(
                    "Chunks cannot be empty"
            );
        }

        // Extract the text from each chunk
        List<String> texts = chunks.stream()
                .map(DocumentChunk::content)
                .toList();

        // Generate embeddings for all chunks
        List<float[]> vectors = embeddingModel.embed(texts);

        if (vectors.size() != chunks.size()) {
            throw new IllegalStateException(
                    "Number of embeddings does not match number of chunks"
            );
        }

        // Combine each chunk with its corresponding vector
        return IntStream.range(0, chunks.size())
                .mapToObj(index -> {

                    float[] vector = vectors.get(index);

                    List<Float> preview = IntStream.range(
                                    0, Math.min(8, vector.length))
                            .mapToObj(i -> vector[i])
                            .toList();

                    return new EmbeddedChunk(
                            chunks.get(index).chunkIndex(),
                            chunks.get(index).content(),
                            vector.length,
                            preview
                    );
                })
                .toList();
    }
}