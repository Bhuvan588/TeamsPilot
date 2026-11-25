package com.example.teampilot.dto;

import com.example.teampilot.entity.RequestStatus;

import java.time.Instant;
import java.util.UUID;

public record AgentRequestResponse(
        UUID requestId,
        RequestStatus status,
        String message,
        Instant createdAt
) {
}
