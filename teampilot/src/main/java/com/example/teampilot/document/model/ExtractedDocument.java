package com.example.teampilot.document.model;

public record ExtractedDocument (
        String filename,
        String contentType,
        String extractedText
){
}
