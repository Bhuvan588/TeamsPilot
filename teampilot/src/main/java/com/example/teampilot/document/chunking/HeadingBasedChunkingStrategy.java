
package com.example.teampilot.document.chunking;


import com.example.teampilot.document.model.DocumentChunk;
import com.example.teampilot.document.model.DocumentSection;
import com.example.teampilot.document.model.DocumentStructure;
import com.example.teampilot.document.model.ExtractedDocument;
import com.example.teampilot.document.service.DocumentStructureAnalyzer;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class HeadingBasedChunkingStrategy implements ChunkingStrategy {

    private final DocumentStructureAnalyzer structureAnalyzer;

    private final TokenTextSplitter sectionSplitter =
            TokenTextSplitter.builder()
                    .withChunkSize(200)
                    .withMinChunkSizeChars(50)
                    .withMinChunkLengthToEmbed(10)
                    .withMaxNumChunks(1000)
                    .withKeepSeparator(true)
                    .build();

    public HeadingBasedChunkingStrategy(
            DocumentStructureAnalyzer structureAnalyzer) {
        this.structureAnalyzer = structureAnalyzer;
    }

    @Override
    public boolean supports(ExtractedDocument document) {
        DocumentStructure structure =
                structureAnalyzer.analyze(document.extractedText());

        return structure.structured()
                && !structure.sections().isEmpty();
    }

    @Override
    public List<DocumentChunk> chunk(ExtractedDocument document) {

        DocumentStructure structure =
                structureAnalyzer.analyze(document.extractedText());

        List<DocumentChunk> chunks = new ArrayList<>();

        // Preserve text appearing before the first heading.
        List<String> lines = document.extractedText()
                .lines()
                .toList();

        int firstHeadingLine = structure.sections().stream()
                .mapToInt(DocumentSection::startLine)
                .min()
                .orElse(0);

        if (firstHeadingLine > 0) {
            String preamble = String.join(
                    "\n",
                    lines.subList(0, firstHeadingLine)
            ).trim();

            if (!preamble.isBlank()) {
                addSectionChunks(
                        "DOCUMENT OVERVIEW",
                        preamble,
                        chunks
                );
            }
        }

        // Process every detected section independently.
        for (DocumentSection section : structure.sections()) {

            if (section.content().isBlank()) {
                continue;
            }

            addSectionChunks(
                    section.heading(),
                    section.content(),
                    chunks
            );
        }

        return chunks;
    }

    private void addSectionChunks(
            String heading,
            String content,
            List<DocumentChunk> chunks) {

        List<org.springframework.ai.document.Document> splitDocuments =
                sectionSplitter.apply(
                        List.of(
                                new org.springframework.ai.document.Document(content)
                        )
                );

        for (org.springframework.ai.document.Document split : splitDocuments) {

            // Prefix every chunk with its section heading.
            String chunkContent =
                    "Section: " + heading + "\n" + split.getText();

            chunks.add(new DocumentChunk(
                    chunks.size(),
                    chunkContent,heading
            ));
        }
    }
}
