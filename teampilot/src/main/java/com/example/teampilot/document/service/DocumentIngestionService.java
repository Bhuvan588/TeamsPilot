package com.example.teampilot.document.service;

import com.example.teampilot.document.extractor.DocumentTextExtractor;
import com.example.teampilot.document.model.ExtractedDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocumentIngestionService {

    private final DocumentTextExtractor textExtractor;

    public DocumentIngestionService(
            DocumentTextExtractor textExtractor) {
        this.textExtractor = textExtractor;
    }

    public ExtractedDocument ingest(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please upload a valid document"
            );
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "File size cannot exceed 10 MB"
            );
        }

        try {
            return textExtractor.extract(
                    file.getInputStream(),
                    file.getOriginalFilename()
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to read uploaded document", e
            );
        }
    }
}