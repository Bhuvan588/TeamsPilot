package com.example.teampilot.document.chunking;

import com.example.teampilot.document.model.DocumentChunk;
import com.example.teampilot.document.model.ExtractedDocument;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class TokenBasedChunkingStrategy implements ChunkingStrategy {

    private final TokenTextSplitter textSplitter =
            TokenTextSplitter.builder()
                    .withChunkSize(200)
                    .withMinChunkSizeChars(50)
                    .withMinChunkLengthToEmbed(10)
                    .withMaxNumChunks(1000)
                    .withKeepSeparator(true)
                    .build();

    @Override
    public boolean supports(ExtractedDocument document) {
        // Generic fallback: supports every document.
        return true;
    }

    @Override
    public List<DocumentChunk> chunk(ExtractedDocument document) {

        Document inputDocument =
                new Document(document.extractedText());

        List<Document> splitDocuments =
                textSplitter.apply(List.of(inputDocument));

        return IntStream.range(0, splitDocuments.size())
                .mapToObj(i -> new DocumentChunk(
                        i,
                        splitDocuments.get(i).getText(),null
                ))
                .toList();
    }
}