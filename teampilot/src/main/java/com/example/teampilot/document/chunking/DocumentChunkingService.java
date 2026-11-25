package com.example.teampilot.document.chunking;


import com.example.teampilot.document.model.DocumentChunk;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class DocumentChunkingService {

    private final TokenTextSplitter textSplitter;

    public DocumentChunkingService() {
        this.textSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .withMinChunkSizeChars(50)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(1000)
                .withKeepSeparator(true)
                .build();
    }

    public List<DocumentChunk> chunk(String extractedText) {

        Document document = new Document(extractedText);

        List<Document> splitDocuments =
                textSplitter.apply(List.of(document));

        return IntStream.range(0, splitDocuments.size())
                .mapToObj(index -> new DocumentChunk(
                        index,
                        splitDocuments.get(index).getText()
                ))
                .toList();
    }
}
