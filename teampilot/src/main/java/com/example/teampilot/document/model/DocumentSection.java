package com.example.teampilot.document.model;

public record DocumentSection(
        String heading,
        String content,
        int startLine,
        int endLine
) {}