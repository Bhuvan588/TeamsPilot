
package com.example.teampilot.document.model;

import java.util.Map;

public record DocumentSearchResult(
        String id,
        String content,
        Map<String, Object> metadata
) {}
