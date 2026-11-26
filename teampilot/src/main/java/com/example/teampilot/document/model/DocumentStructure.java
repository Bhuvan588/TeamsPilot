package com.example.teampilot.document.model;

import java.util.List;

public record DocumentStructure(
        List<DocumentSection> sections,
        double confidence,
        boolean structured
) {}