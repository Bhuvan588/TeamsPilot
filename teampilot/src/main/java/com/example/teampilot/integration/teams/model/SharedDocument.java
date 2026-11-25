package com.example.teampilot.integration.teams.model;

import java.time.Instant;

public record SharedDocument(
        String id,
        String name,
        String sharedByUserId,
        String sharedByName,
        String chatId,
        String downloadUrl,
        String mimeType,
        Instant sharedAt
) {}