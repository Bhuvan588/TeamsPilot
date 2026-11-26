package com.example.teampilot.document.controller;


import com.example.teampilot.document.embedding.model.EmbeddedChunk;
import com.example.teampilot.document.embedding.service.DocumentEmbeddingService;
import com.example.teampilot.document.model.*;
import com.example.teampilot.document.service.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentIngestionService ingestionService;
    private final DocumentChunkingService chunkingService;
    private final DocumentEmbeddingService embeddingService;
    private final DocumentVectorStoreService vectorStoreService;
    private final DocumentSearchService searchService;
    private final DocumentRagService ragService;
    private final DocumentKeywordSearchService keywordSearchService;
    private final HybridSearchService hybridSearchService;


    public DocumentController(
            DocumentIngestionService ingestionService,DocumentChunkingService chunkingService,
            DocumentEmbeddingService embeddingService,
            DocumentVectorStoreService vectorStoreService,
            DocumentSearchService searchService,
            DocumentRagService ragService,
            DocumentKeywordSearchService keywordSearchService,
            HybridSearchService hybridSearchService) {
        this.ingestionService = ingestionService;
        this.chunkingService = chunkingService;
        this.embeddingService = embeddingService;
        this.vectorStoreService=vectorStoreService;
        this.searchService=searchService;
        this.ragService=ragService;
        this.keywordSearchService=keywordSearchService;
        this.hybridSearchService=hybridSearchService;

    }

    @PostMapping(
            value = "/extract",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ExtractedDocument extractDocument(
            @RequestParam("file") MultipartFile file) {

        return ingestionService.ingest(file);
    }

    //endpoint to test chunks
    @PostMapping(
            value = "/chunk",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public List<DocumentChunk> chunkDocument(
            @RequestParam("file") MultipartFile file) {

        ExtractedDocument extracted =
                ingestionService.ingest(file);

        return chunkingService.chunk(
                extracted
        );
    }

    //endpoint to test embeddings
    @PostMapping(
            value = "/embed",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public List<EmbeddedChunk> embedDocument(
            @RequestParam("file") MultipartFile file) {

        // Step 1: Extract text
        ExtractedDocument extracted =
                ingestionService.ingest(file);

        // Step 2: Split text into chunks
        List<DocumentChunk> chunks =
                chunkingService.chunk(extracted);

        // Step 3: Generate embeddings
        return embeddingService.generateEmbeddings(chunks);
    }

    @PostMapping("/ingest-and-store")
    public ResponseEntity<?> ingestAndStore(
            @RequestParam("file") MultipartFile file) {

        // Step 1: Extract text
        ExtractedDocument extracted =
                ingestionService.ingest(file);

        // Step 2: Split into chunks
        List<DocumentChunk> chunks =
                chunkingService.chunk(
                        extracted
                );

        // Step 3: Generate a unique ID for this upload
        String documentId = UUID.randomUUID().toString();

        // Step 4: Store chunks and generate embeddings internally
        vectorStoreService.storeChunks(
                chunks,
                documentId,
                extracted.filename()
        );

        return ResponseEntity.ok(
                Map.of(
                        "documentId", documentId,
                        "filename", extracted.filename(),
                        "chunksStored", chunks.size(),
                        "status", "STORED"
                )
        );
    }


    //endpoint for testing semantic search
    @GetMapping("/search")
    public ResponseEntity<List<DocumentSearchResult>> search(
            @RequestParam String query) {

        List<DocumentSearchResult> results =
                searchService.search(query);

        return ResponseEntity.ok(results);
    }

    //final RAG endpoint
    @GetMapping("/ask")
    public ResponseEntity<RagResponse> askQuestion(
            @RequestParam String question) {

        RagResponse response =
                ragService.answerQuestion(question);

        return ResponseEntity.ok(response);
    }

    //for keyword search
    @GetMapping("/keyword-search")
    public List<KeywordSearchResult> keywordSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {

        return keywordSearchService.search(query, topK);
    }

    @GetMapping("/hybrid-search")
    public List<HybridSearchResult> hybridSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {

        return hybridSearchService.search(query, topK);
    }
}