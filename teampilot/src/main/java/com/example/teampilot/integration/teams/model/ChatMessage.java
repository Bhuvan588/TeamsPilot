package com.example.teampilot.integration.teams.model;


import java.time.Instant;

public record ChatMessage(
        String id,
        String chatId,
        String senderId,
        String senderName,
        String content,
        Instant createdAt
) {}